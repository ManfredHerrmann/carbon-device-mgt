/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.policy.mgt.common.impl;

import org.wso2.carbon.policy.mgt.common.FeatureManagementException;
import org.wso2.carbon.policy.mgt.common.Policy;
import org.wso2.carbon.policy.mgt.common.PolicyAdministratorService;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;

public class PolicyManagement implements PolicyAdministratorService {
    @Override
    public int addPolicy(Policy policy) {
        return 0;
    }

    @Override
    public int addPolicyToDevice(String deviceId, String deviceType, Policy policy) throws FeatureManagementException, PolicyManagementException {
        return 0;
    }

    @Override
    public int addPolicyToDeviceType(String deviceType, Policy policy) throws FeatureManagementException, PolicyManagementException {
        return 0;
    }

    @Override
    public int addPolicyToRole(String roleName, Policy policy) throws FeatureManagementException, PolicyManagementException {
        return 0;
    }

    @Override
    public Policy getPolicy() {
        return null;
    }

    @Override
    public Policy getPolicyOfDevice(String deviceId, String deviceType) throws FeatureManagementException, PolicyManagementException {
        return null;
    }

    @Override
    public Policy getPolicyOfDeviceType(String deviceType) throws FeatureManagementException, PolicyManagementException {
        return null;
    }

    @Override
    public Policy getPolicyOfRole(String roleName) throws FeatureManagementException, PolicyManagementException {
        return null;
    }

    @Override
    public boolean isPolicyAvailableForDevice(String deviceId, String deviceType) throws PolicyManagementException {
        return false;
    }

    @Override
    public boolean isPolicyUsed(String deviceId, String deviceType) throws PolicyManagementException {
        return false;
    }

    @Override
    public void setPolicyUsed(String deviceId, String deviceType, Policy policy) throws PolicyManagementException {

    }
}
