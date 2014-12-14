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

package org.wso2.carbon.tenant.artifact.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.core.ServerStartupHandler;
import org.wso2.carbon.tenant.artifact.service.TenantManagementAdminService;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * @scr.component name="org.wso2.carbon.tenant.artifact"
 * immediate="true"
 * @scr.reference name="config.context.service"
 * interface="org.wso2.carbon.utils.ConfigurationContextService"
 * cardinality="1..1" policy="dynamic"
 * bind="setConfigurationContextService"
 * unbind="unsetConfigurationContextService"
 * @scr.reference name="user.realmservice.default"
 * interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1" policy="dynamic"
 * bind="setRealmService"
 * unbind="unsetRealmService"
 */
public class TenantMgtServiceComponent {
    private ServiceRegistration registration;
    private static TenantManagementAdminService adminService;
    private static BundleContext bundleContext;
    private static final Log logger = LogFactory.getLog(TenantMgtServiceComponent.class);
    private DataHolder dataHolder = DataHolder.getInstance();

    protected void activate(ComponentContext context) {
        logger.info("Tenant Management Service bundle is activated");
        adminService = new TenantManagementAdminService();
        bundleContext = context.getBundleContext();
        registration = bundleContext.registerService(ServerStartupHandler.class.getName(), adminService, null);
    }

    protected void deactivate(ComponentContext context) {
        logger.info("Tenant Management Service bundle is deactivated");
        registration.unregister();
        adminService = null;
        bundleContext = null;
    }

    protected void setConfigurationContextService(ConfigurationContextService contextService) {
        dataHolder.setConfigurationContextService(contextService);
    }

    protected void unsetConfigurationContextService(ConfigurationContextService contextService) {
        dataHolder.setConfigurationContextService(null);
    }

    protected void setRealmService(RealmService realmService) {
        dataHolder.setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {
        dataHolder.setRealmService(null);
    }

}
