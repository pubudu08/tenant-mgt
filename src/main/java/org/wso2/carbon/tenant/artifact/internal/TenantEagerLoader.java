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

import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.ServerStartupHandler;
import org.wso2.carbon.core.multitenancy.utils.TenantAxisUtils;
import org.wso2.carbon.tenant.artifact.config.TenantArtifactConfiguration;
import org.wso2.carbon.tenant.artifact.config.TenantArtifactXMLProcessor;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class responsible of processing/validating and load once server startup completed.
 */
public class TenantEagerLoader implements ServerStartupHandler {

    private static final Log logger = LogFactory.getLog(TenantEagerLoader.class);
    private DataHolder dataHolder = DataHolder.getInstance();

    @Override
    public void invoke() {
        try {
            loadTenants();
        } catch (IOException e) {
            logger.error("Tenant configuration is not found,", e);
        } catch (UserStoreException e) {
            logger.error("Unexpected error occurred when retrieving tenant Manager", e);
        } catch (XMLStreamException e) {
            logger.error("Unable to process init-tenant.xml file", e);
        } catch (JAXBException e) {
            logger.error("Unexpected errors occur while unmarshalling", e);
        }
    }

    /**
     * Processing tenant artifacts when server startup
     */
    private void loadTenants() throws IOException, UserStoreException, XMLStreamException, JAXBException {
        if (logger.isDebugEnabled()) {
            logger.debug("Tenant Artifact Management Service has been invoked");
        }
        TenantArtifactConfiguration tenantArtifactConfiguration =
                TenantArtifactXMLProcessor.getInstance().buildTenantInitConfigFromFile();
        List<String> validTenantDomains = getPreExistsTenantDomains();
        validTenantDomains = validateTenantLoadingProcess(validTenantDomains, tenantArtifactConfiguration);
        if (validTenantDomains != null) {
            invokeStartupTenantProcess(validTenantDomains);
        }
    }

    /**
     * Method to return existing tenant domains from UserRealm service
     *
     * @return sorted existing tenant domains
     * @throws UserStoreException
     */
    private List<String> getPreExistsTenantDomains() throws UserStoreException {
        List<String> sortedTenantDomains = new ArrayList<String>();
        List<Tenant> validTenantList = null;
        if (dataHolder.getRealmService() != null) {
            validTenantList = Arrays.asList(dataHolder.getRealmService().getTenantManager().getAllTenants());
        }
        if (validTenantList != null) {
            for (Tenant tenant : validTenantList) {
                sortedTenantDomains.add(tenant.getDomain());
            }
        }
        return sortedTenantDomains;
    }

    /**
     * Method will load all the tenants which are defined in init-tenant.xml when the server startup.
     *
     * @param validTenantDomains ArrayList of valid tenant domains
     */
    private void invokeStartupTenantProcess(List<String> validTenantDomains) {
        for (String tenantDomain : validTenantDomains) {
            PrivilegedCarbonContext.startTenantFlow();
            ConfigurationContext context = null;
            if (dataHolder.getConfigurationContextService().getServerConfigContext() != null) {
                context = TenantAxisUtils
                        .getTenantConfigurationContext(tenantDomain,
                                                       dataHolder.getConfigurationContextService().
                                                               getServerConfigContext());
            }
            if (logger.isDebugEnabled()) {
                if (context != null) {
                    logger.debug(tenantDomain + " deployed service count : " +
                                 context.getAxisConfiguration().getServices().entrySet().size());
                }
            }
            PrivilegedCarbonContext.endTenantFlow();
        }

    }

    /**
     * Validate xml inputs
     *
     * @param validTenantDomains          existing tenant domains
     * @param tenantArtifactConfiguration configuration object
     * @return validated tenant domain list
     */
    private List<String> validateTenantLoadingProcess(List<String> validTenantDomains,
                                                           TenantArtifactConfiguration tenantArtifactConfiguration) {
        // where include = *  and exclude contains domain values
        if (tenantArtifactConfiguration.isIncludeAll() && tenantArtifactConfiguration.getExcludeTenantList() != null) {
            validTenantDomains.removeAll(tenantArtifactConfiguration.getExcludeTenantList());
            //where exclude = * and include contains values
        } else if (!tenantArtifactConfiguration.isIncludeAll() && tenantArtifactConfiguration.getIncludeTenantList()
                                                                  != null) {
            validTenantDomains.retainAll(tenantArtifactConfiguration.getIncludeTenantList());
        }
        return validTenantDomains;
    }

}
