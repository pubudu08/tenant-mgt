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

package org.wso2.carbon.tenant.artifact.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This reads the init-tenant.xml through axiom api and constructs an object of
 * TenantMgtConfiguration
 */
public class TenantArtifactXMLProcessor {
    private static final String INIT_TENANT_XML = "init-tenant.xml";
    private static final Log LOGGER = LogFactory.getLog(TenantArtifactXMLProcessor.class);
    private static TenantArtifactXMLProcessor instance = new TenantArtifactXMLProcessor();

    private TenantArtifactXMLProcessor() {
    }

    public static TenantArtifactXMLProcessor getInstance() {
        return instance;
    }

    /**
     * Responsible of retrieving init-tenant.xml file from configuration directory
     *
     * @return omElement
     * @throws IOException
     * @throws XMLStreamException
     */
    public TenantArtifactConfiguration buildTenantInitConfigFromFile() throws IOException, XMLStreamException {
        InputStream inputStream = null;
        TenantArtifactConfiguration tenantArtifactConfiguration = null;
        File tenantIntConfigFile = new File(CarbonUtils.getCarbonConfigDirPath(), INIT_TENANT_XML);
        if (tenantIntConfigFile.exists()) {
            inputStream = new FileInputStream(tenantIntConfigFile);
        }
        String errorMsg = "Tenant configuration not found, Cause: Could not find resource "
                          + INIT_TENANT_XML
                          + " or user does not have sufficient permission to access the resource.";
        if (inputStream == null) {
            LOGGER.error(errorMsg);
            throw new FileNotFoundException(errorMsg);
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(TenantArtifactConfiguration.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            tenantArtifactConfiguration =
                    (TenantArtifactConfiguration) jaxbUnmarshaller.unmarshal(tenantIntConfigFile);
        } catch (JAXBException e) {
            LOGGER.error(" Error xml un");
        }
        return tenantArtifactConfiguration;
    }

}