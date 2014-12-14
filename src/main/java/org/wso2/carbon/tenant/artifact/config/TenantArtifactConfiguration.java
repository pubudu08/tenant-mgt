/*
 *
 *  * Copyright 2014 The Apache Software Foundation.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *  http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.wso2.carbon.tenant.artifact.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class that represents the init-tenant.xml
 */
@XmlRootElement(name = "Tenants")
public class TenantArtifactConfiguration {
    private List<String> includeTenantList;
    private List<String> excludeTenantList = null;
    private String includeTenants;
    private String excludeTenants;
    private boolean isIncludeAWildcard;

    public String getIncludeTenants() {
        return includeTenants;
    }

    @XmlElement(name = "Include")
    public void setIncludeTenants(String include) {
        this.includeTenants = include.trim();
        if (!includeTenants.contains("*")) {
            this.includeTenantList = Arrays.asList(splitter(includeTenants));
        }
    }

    public String getExcludeTenants() {
        return excludeTenants;
    }

    @XmlElement(name = "Exclude")
    public void setExcludeTenants(String exclude) {
        this.excludeTenants = exclude;
        if (!excludeTenants.contains("*")) {
            excludeTenantList = Arrays.asList(splitter(excludeTenants));
            Collections.sort(excludeTenantList);
        }
    }

    public List<String> getIncludeTenantList() {
        return includeTenantList;
    }

    public List<String> getExcludeTenantList() {
        return excludeTenantList;
    }

    private String[] splitter(String text) {
        String[] parts;
        if (text.contains(",")) {
            parts = text.split(",");
        } else {
            parts = new String[] { text };
        }
        return parts;
    }
}
