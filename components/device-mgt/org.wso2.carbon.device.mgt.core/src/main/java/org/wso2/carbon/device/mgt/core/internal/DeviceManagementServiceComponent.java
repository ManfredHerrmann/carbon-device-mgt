/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.device.mgt.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;
import org.wso2.carbon.device.mgt.core.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.core.DeviceManagementRepository;
import org.wso2.carbon.device.mgt.core.DeviceManager;
import org.wso2.carbon.device.mgt.core.DeviceManagerImpl;
import org.wso2.carbon.device.mgt.core.config.DeviceConfigurationManager;
import org.wso2.carbon.device.mgt.core.config.DeviceManagementConfig;
import org.wso2.carbon.device.mgt.core.config.datasource.DataSourceConfig;
import org.wso2.carbon.device.mgt.core.config.license.License;
import org.wso2.carbon.device.mgt.core.config.license.LicenseConfig;
import org.wso2.carbon.device.mgt.core.config.license.LicenseConfigurationManager;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.core.license.mgt.LicenseManagementException;
import org.wso2.carbon.device.mgt.core.license.mgt.LicenseManagementService;
import org.wso2.carbon.device.mgt.core.license.mgt.LicenseManager;
import org.wso2.carbon.device.mgt.core.license.mgt.LicenseManagerImpl;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.device.mgt.core.util.DeviceManagementSchemaInitializer;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="org.wso2.carbon.device.manager" immediate="true"
 * @scr.reference name="user.realmservice.default"
 * interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setRealmService"
 * unbind="unsetRealmService"
 * @scr.reference name="device.manager.service"
 * interface="org.wso2.carbon.device.mgt.common.spi.DeviceManagerService"
 * cardinality="0..n"
 * policy="dynamic"
 * bind="setDeviceManagerService"
 * unbind="unsetDeviceManagerService"
 * @scr.reference name="registry.service"
 * interface="org.wso2.carbon.registry.core.service.RegistryService"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setRegistryService"
 * unbind="unsetRegistryService"
 */
public class DeviceManagementServiceComponent {

	private static Log log = LogFactory.getLog(DeviceManagementServiceComponent.class);
	private DeviceManagementRepository pluginRepository = new DeviceManagementRepository();

	protected void activate(ComponentContext componentContext) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Initializing device management core bundle");
			}
		    /* Initializing Device Management Configuration */
			DeviceConfigurationManager.getInstance().initConfig();
			DeviceManagementConfig config =
					DeviceConfigurationManager.getInstance().getDeviceManagementConfig();

			DataSourceConfig dsConfig = config.getDeviceMgtRepository().getDataSourceConfig();
			DeviceManagementDAOFactory.init(dsConfig);

			DeviceManager deviceManager = new DeviceManagerImpl(config, this.getPluginRepository());
			DeviceManagementDataHolder.getInstance().setDeviceManager(deviceManager);

			LicenseConfigurationManager.getInstance().initConfig();
			LicenseConfig licenseConfig =
					LicenseConfigurationManager.getInstance().getLicenseConfig();

			LicenseManager licenseManager = new LicenseManagerImpl();
			DeviceManagementDataHolder.getInstance().setLicenseManager(licenseManager);
			DeviceManagementDataHolder.getInstance().setLicenseConfig(licenseConfig);

            /* If -Dsetup option enabled then create device management database schema */
			String setupOption =
					System.getProperty(DeviceManagementConstants.Common.PROPERTY_SETUP);
			if (setupOption != null) {
				if (log.isDebugEnabled()) {
					log.debug(
							"-Dsetup is enabled. Device management repository schema initialization " +
							"is about to begin");
				}
				this.setupDeviceManagementSchema(dsConfig);
				// this.setupDefaultLicenses(licenseConfig);
			}

			if (log.isDebugEnabled()) {
				log.debug("Registering OSGi service DeviceManagementService");
			}
            /* Registering Device Management service */
			BundleContext bundleContext = componentContext.getBundleContext();
			bundleContext.registerService(DeviceManagementService.class.getName(),
			                              new DeviceManagementService(), null);
            /* Registering License Management service */
			bundleContext.registerService(LicenseManagementService.class.getName(),
			                              new LicenseManagementService(), null);
			if (log.isDebugEnabled()) {
				log.debug("Device management core bundle has been successfully initialized");
			}
		} catch (Throwable e) {
			String msg = "Error occurred while initializing device management core bundle";
			log.error(msg, e);
		}
	}

	private void setupDeviceManagementSchema(DataSourceConfig config)
			throws DeviceManagementException {
		DeviceManagementSchemaInitializer initializer =
				new DeviceManagementSchemaInitializer(config);
		log.info("Initializing device management repository database schema");

		try {
			initializer.createRegistryDatabase();
		} catch (Exception e) {
			throw new DeviceManagementException(
					"Error occurred while initializing Device Management " +
					"database schema", e);
		}
		if (log.isDebugEnabled()) {
			log.debug(
					"Device management metadata repository schema has been successfully initialized");
		}
	}

	private void setupDefaultLicenses(LicenseConfig licenseConfig)
			throws LicenseManagementException {
		LicenseManager licenseManager =
				DeviceManagementDataHolder.getInstance().getLicenseManager();
		for (License license : licenseConfig.getLicenses()) {
			License extLicense =
					licenseManager.getLicense(license.getName(), license.getLanguage());
			if (extLicense != null) {
				continue;
			}
			licenseManager.addLicense(license);
		}
	}

	/**
	 * Sets Device Manager service.
	 *
	 * @param deviceManagerService An instance of DeviceManagerService
	 */
	protected void setDeviceManagerService(DeviceManagerService deviceManagerService) {
		if (log.isDebugEnabled()) {
			log.debug("Setting Device Management Service Provider : '" +
			          deviceManagerService.getProviderType() + "'");
		}
		this.getPluginRepository().addDeviceManagementProvider(deviceManagerService);
	}

	/**
	 * Unsets Device Management service.
	 *
	 * @param deviceManagerService An Instance of DeviceManagerService
	 */
	protected void unsetDeviceManagerService(DeviceManagerService deviceManagerService) {
		if (log.isDebugEnabled()) {
			log.debug("Unsetting Device Management Service Provider : '" +
			          deviceManagerService.getProviderType() + "'");
		}
		this.getPluginRepository().removeDeviceManagementProvider(deviceManagerService);
	}

	/**
	 * Sets Realm Service.
	 *
	 * @param realmService An instance of RealmService
	 */
	protected void setRealmService(RealmService realmService) {
		if (log.isDebugEnabled()) {
			log.debug("Setting Realm Service");
		}
		DeviceManagementDataHolder.getInstance().setRealmService(realmService);
	}

	/**
	 * Unsets Realm Service.
	 *
	 * @param realmService An instance of RealmService
	 */
	protected void unsetRealmService(RealmService realmService) {
		if (log.isDebugEnabled()) {
			log.debug("Unsetting Realm Service");
		}
		DeviceManagementDataHolder.getInstance().setRealmService(null);
	}

	/**
	 * Sets Registry Service.
	 *
	 * @param registryService An instance of RegistryService
	 */
	protected void setRegistryService(RegistryService registryService) {
		if (log.isDebugEnabled()) {
			log.debug("Setting Registry Service");
		}
		DeviceManagementDataHolder.getInstance().setRegistryService(registryService);
	}

	/**
	 * Unsets Registry Service.
	 *
	 * @param registryService An instance of RegistryService
	 */
	protected void unsetRegistryService(RegistryService registryService) {
		if (log.isDebugEnabled()) {
			log.debug("Unsetting Registry Service");
		}
		DeviceManagementDataHolder.getInstance().setRegistryService(null);
	}

	private DeviceManagementRepository getPluginRepository() {
		return pluginRepository;
	}

}
