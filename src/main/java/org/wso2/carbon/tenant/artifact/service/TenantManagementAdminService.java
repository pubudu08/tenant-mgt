/*
 * Copyright 2014 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.tenant.artifact.service;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.ServerStartupHandler;
import org.wso2.carbon.core.multitenancy.utils.TenantAxisUtils;
import org.wso2.carbon.tenant.artifact.mgt.TenantArtifactManagerService;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * @scr.reference name="config.context.service"
 * interface="org.wso2.carbon.utils.ConfigurationContextService"
 * cardinality="1..1" policy="dynamic"
 * bind="setConfigurationContextService"
 * unbind="unsetConfigurationContextService"
 */
public class TenantManagementAdminService implements ServerStartupHandler {

    private static final Log LOGGER = LogFactory.getLog(TenantManagementAdminService.class);
    private ConfigurationContextService configurationContextService;

    @Override
    public void invoke() {
        //Getting server configuration
        LOGGER.info("========================Invoked : Tenant Artifact Management Service==================");
        ConfigurationContext context = TenantAxisUtils.getTenantConfigurationContext("test.com",
                                                                                     configurationContextService
                                                                                             .getServerConfigContext());
        int size = context.getAxisConfiguration().getServices().entrySet().size();
        LOGGER.info("======context.getAxisConfiguration().getServices().entrySet().size() = " + size + " =====");
        LOGGER.info("========================End: Tenant Artifact Management Service==================");

    }

    protected void setConfigurationContextService(ConfigurationContextService contextService) {
        configurationContextService = contextService;
    }

    protected void unsetConfigurationContextService(ConfigurationContextService contextService) {
        configurationContextService = null;
    }

}
