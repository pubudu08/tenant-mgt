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
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.ServerStartupHandler;
import org.wso2.carbon.core.multitenancy.utils.TenantAxisUtils;
import org.wso2.carbon.tenant.artifact.internal.DataHolder;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;

public class TenantManagementAdminService implements ServerStartupHandler {

    private static final Log LOGGER = LogFactory.getLog(TenantManagementAdminService.class);
    DataHolder dataHolder = DataHolder.getInstance();

    @Override
    public void invoke() {
        processTenantArtifacts();
    }

    /**
     * Processing tenant artifacts when server startup
     */
    private void processTenantArtifacts() {
        LOGGER.info("== Invoked : Tenant Artifact Management Service ==");
        try {
            Tenant[] tenantCollection = dataHolder.getRealmService().getTenantManager().getAllTenants();
            for (Tenant tenant : tenantCollection) {
                PrivilegedCarbonContext.startTenantFlow();
                String tenantDomain = tenant.getDomain();
                ConfigurationContext context = TenantAxisUtils
                        .getTenantConfigurationContext(tenantDomain, dataHolder.getConfigurationContextService().
                                getServerConfigContext());
                LOGGER.info("==" + tenantDomain + " deployed service count : " +
                            context.getAxisConfiguration().getServices().entrySet().size() + " " +
                            "==");
                PrivilegedCarbonContext.destroyCurrentContext();
            }
        } catch (UserStoreException e) {
            LOGGER.error("Error occurred when retrieving tenant Manager");
        }
    }

}
