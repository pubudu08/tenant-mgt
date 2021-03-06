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

package org.wso2.carbon.tenant.eager.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.ServerStartupHandler;
import org.wso2.carbon.core.multitenancy.utils.TenantAxisUtils;
import org.wso2.carbon.tenant.eager.config.TenantLoadingConfig;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class responsible of processing/validating and load once server startup completed.
 */
public class TenantEagerLoader implements ServerStartupHandler {

    private static final Log logger = LogFactory.getLog(TenantEagerLoader.class);
    private DataHolder dataHolder = DataHolder.getInstance();
    private TenantLoadingConfig tenantLoadingConfig = new TenantLoadingConfig();

    public TenantEagerLoader() {
        tenantLoadingConfig.init();
    }

    @Override
    public void invoke() {
        List<String> validTenantDomains;
        try {
            validTenantDomains = getValidTenantDomains();
        } catch (UserStoreException e) {
            String msg = "Could not load valid tenant domains";
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        if(!tenantLoadingConfig.isOptional()){
            List<String> tenantsToBeEagerLoaded = getTenantsToBeEagerLoaded(validTenantDomains);
            loadTenants(tenantsToBeEagerLoaded);
        }
    }

    /**
     * Method to return existing tenant domains in the system from UserRealm service
     *
     * @return sorted existing tenant domains
     * @throws UserStoreException
     */
    private List<String> getValidTenantDomains() throws UserStoreException {
        RealmService realmService = dataHolder.getRealmService();
        if (realmService == null) {
            throw new IllegalStateException("UserRealmService has not been set");
        }
        List<String> tenantDomains = new ArrayList<String>();
        List<Tenant> validTenantList = Arrays.asList(realmService.getTenantManager().getAllTenants());
        for (Tenant tenant : validTenantList) {
            tenantDomains.add(tenant.getDomain());
        }
        return tenantDomains;
    }

    /**
     * Method will load all the tenants which are defined in init-tenant.xml when the server startup.
     *
     * @param validTenantDomains ArrayList of valid tenant domains
     */
    private void loadTenants(List<String> validTenantDomains) {
        for (String tenantDomain : validTenantDomains) {
            try {
                PrivilegedCarbonContext.startTenantFlow();
                if (dataHolder.getConfigurationContextService().getServerConfigContext() != null) {
                    TenantAxisUtils.getTenantConfigurationContext(tenantDomain,
                                                                  dataHolder.getConfigurationContextService()
                                                                            .getServerConfigContext());
                }
            } finally {
                PrivilegedCarbonContext.endTenantFlow();
            }
        }
    }

    /**
     * Validate xml inputs
     *
     * @param validTenantDomains existing tenant domains
     * @return validated tenant domain list
     */
    private List<String> getTenantsToBeEagerLoaded(List<String> validTenantDomains) {
        List<String> tenantsToBeLoaded = new ArrayList<String>();
        if (tenantLoadingConfig.isEagerLoadingEnabled()) {
            List<String> includeTenantList =
                    validateTenantDomains(tenantLoadingConfig.getIncludeTenantList(), validTenantDomains);
            List<String> excludeTenantList =
                    validateTenantDomains(tenantLoadingConfig.getExcludeTenantList(), validTenantDomains);

            if (tenantLoadingConfig.includeAllTenants()) {
                tenantsToBeLoaded.addAll(validTenantDomains);
                // Now that we have included  all tenants, let's see whether any tenant have been excluded
                if (!excludeTenantList.isEmpty()) {
                    tenantsToBeLoaded.removeAll(excludeTenantList);
                }
            } else { // include only tenants such as foo.com, bar.com
                tenantsToBeLoaded.addAll(includeTenantList);
            }
        }

        // Nothing to do if lazy loading is enabled since the default lazy loading mechanism will kick in

        return tenantsToBeLoaded;
    }

    /**
     * Check for non-existing tenant domains & print a warning is any non-existent tenants have been specified,
     * and return a List of tenant domains which are valid
     *
     * @param tenantDomains      configuration bean
     * @param validTenantDomains valid list
     * @return The validated tenant domain List
     */
    private List<String> validateTenantDomains(List<String> tenantDomains, List<String> validTenantDomains) {
        List<String> validatedTenantDomains = new ArrayList<String>();
        for (String domain : tenantDomains) {
            if (!validTenantDomains.contains(domain)) {
                logger.warn("Tenant " + domain + " is not available in the system.");
            } else {
                validatedTenantDomains.add(domain);
            }
        }
        return validatedTenantDomains;
    }
}
