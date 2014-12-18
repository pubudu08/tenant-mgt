/*
 *
 *    Copyright 2014 The Apache Software Foundation.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.wso2.carbon.tenant.eager.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.api.ServerConfigurationService;
import org.wso2.carbon.tenant.eager.internal.DataHolder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class TenantLoadingConfig {

    private static final Log logger = LogFactory.getLog(TenantLoadingConfig.class);
    public static final String TENANT_IDLE_TIME = "tenant.idle.time";
    private LinkedHashSet<String> includeTenantList = new LinkedHashSet<String>();
    private LinkedHashSet<String> excludeTenantList = new LinkedHashSet<String>();
    private boolean includeAllTenants;
    //Tenant element in carbon.xml is optional, so that if it is not specified,
    // we fallback on the default tenant loading mechanism, which is lazy loading.
    private boolean isOptional;

    public void init() {
        ServerConfigurationService serverConfigurationService =
                DataHolder.getInstance().getServerConfigurationService();
        if(serverConfigurationService == null){
            throw new IllegalStateException("ServerConfigurationService is null");
        }
        String tenantIdleTime = serverConfigurationService.getFirstProperty("Tenant.LoadingPolicy.LazyLoading.IdleTime");
        String eagerLoadingString = serverConfigurationService.getFirstProperty("Tenant.LoadingPolicy.EagerLoading.Include");

        if(tenantIdleTime != null) {
            logger.info("Using tenant lazy loading policy...");
            System.setProperty(TENANT_IDLE_TIME, tenantIdleTime);
        } else {
            // The eagerLoadingString could be something like; *,!wso2.com,!test.com
            if (eagerLoadingString == null || eagerLoadingString.trim().isEmpty()) {
                isOptional = true;
                logger.info("Switching to default mode : Tenant lazy loading mechanism has been activated...");
                return;
                /*throw new IllegalArgumentException(
                        "Tenant.LoadingPolicy.EagerLoading.Include element has not been specified in the carbon.xml");*/
            }
            isOptional = false;
            logger.info("Using tenant eager loading policy...");
            String[] tenants = eagerLoadingString.split(",");
            for (String tenant : tenants) {
                tenant = tenant.trim();
                if (tenant.equals("*")) {
                    includeAllTenants = true;
                } else if (tenant.contains("!")) {
                    if(tenant.contains("*")){
                        throw new IllegalArgumentException(tenant + " is not a valid tenant domain");
                    }
                    excludeTenantList.add(tenant.replace("!", ""));
                } else {
                    includeTenantList.add(tenant);
                }
            }
        }
    }

    public boolean isEagerLoadingEnabled(){
        // If the excludeTenantList or includeTenantList is not empty, it means that we are using the eager loading
        // policy
        return includeAllTenants || !excludeTenantList.isEmpty() || !includeTenantList.isEmpty();
    }

    public List<String> getExcludeTenantList() {
        return new ArrayList<String>(excludeTenantList);
    }

    public List<String> getIncludeTenantList() {
        return new ArrayList<String>(includeTenantList);
    }

    public boolean includeAllTenants() {
        return includeAllTenants;
    }

    public boolean isOptional() {
        return isOptional;
    }
}
