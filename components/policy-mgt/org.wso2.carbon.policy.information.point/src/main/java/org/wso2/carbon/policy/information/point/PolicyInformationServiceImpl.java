/*
*  Copyright (c) 2015 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.policy.information.point;

import org.wso2.carbon.policy.mgt.common.Feature;
import org.wso2.carbon.policy.mgt.common.PIPDeviceData;
import org.wso2.carbon.policy.mgt.common.Policy;
import org.wso2.carbon.policy.mgt.common.PolicyInformationService;

import java.util.List;

public class PolicyInformationServiceImpl implements PolicyInformationService{
    @Override
    public void getDeviceData(PIPDeviceData pipDeviceData) {

    }

    @Override
    public List<Policy> getRelatedPolicies(PIPDeviceData pipDeviceData) {
        return null;
    }

    @Override
    public List<Feature> getRelatedFeatures(String deviceType) {
        return null;
    }
}
