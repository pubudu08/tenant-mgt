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
import org.wso2.carbon.tenant.artifact.config.TenantArtifactConfiguration;
import org.wso2.carbon.tenant.artifact.config.TenantArtifactXMLProcessor;
import org.wso2.carbon.tenant.artifact.internal.DataHolder;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TenantManagementAdminService implements ServerStartupHandler {

    private static final Log LOGGER = LogFactory.getLog(TenantManagementAdminService.class);
    DataHolder dataHolder = DataHolder.getInstance();

    @Override
    public void invoke() {
        try {
            processTenantArtifacts();
        } catch (IOException e) {
            LOGGER.error("Tenant configuration is not found,", e);
        } catch (UserStoreException e) {
            LOGGER.error("Unexpected error occurred when retrieving tenant Manager", e);
        } catch (XMLStreamException e) {
            LOGGER.error("Unable to process init-tenant.xml file", e);
        } catch (JAXBException e) {
            LOGGER.error("Unexpected errors occur while unmarshalling", e);
        }
    }

    /**
     * Processing tenant artifacts when server startup
     */
    private void processTenantArtifacts() throws IOException, UserStoreException, XMLStreamException, JAXBException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Tenant Artifact Management Service has been invoked");
        }
        TenantArtifactConfiguration tenantArtifactConfiguration =
                TenantArtifactXMLProcessor.getInstance().buildTenantInitConfigFromFile();
        ArrayList<String> validTenantDomains = getPreExistsTenantDomains();
        validTenantDomains.removeAll(tenantArtifactConfiguration.getExcludeTenantList());
        invokeStartupTenantProcess(validTenantDomains);
    }

    /**
     * Method to return existing tenant domains from UserRealm service
     *
     * @return sorted existing tenant domains
     * @throws UserStoreException
     */
    private ArrayList<String> getPreExistsTenantDomains() throws UserStoreException {
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
        Collections.sort(sortedTenantDomains);
        return new ArrayList<String>(sortedTenantDomains);
    }

    /**
     * Method will load all the tenants which are defined in init-tenant.xml when the server startup.
     *
     * @param validTenantDomains ArrayList of valid tenant domains
     */
    private void invokeStartupTenantProcess(ArrayList<String> validTenantDomains) {
        for (String tenantDomain : validTenantDomains) {
            PrivilegedCarbonContext.startTenantFlow();
            ConfigurationContext context = TenantAxisUtils
                    .getTenantConfigurationContext(tenantDomain,
                                                   dataHolder.getConfigurationContextService().
                                                           getServerConfigContext());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(tenantDomain + " deployed service count : " +
                             context.getAxisConfiguration().getServices().entrySet().size());
            }
            PrivilegedCarbonContext.destroyCurrentContext();
        }

    }

}
