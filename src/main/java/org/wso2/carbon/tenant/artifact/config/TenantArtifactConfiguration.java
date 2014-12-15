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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Class that represents the init-tenant.xml
 */
@XmlRootElement(name = "Tenants")
public class TenantArtifactConfiguration {
    private List<String> includeTenantList;
    private List<String> excludeTenantList;

    @XmlElement(name = "Include")
    protected void setIncludeTenants(String include) {
        String includeTenants = include.trim();
        if (!includeTenants.contains("*")) {
            includeTenantList = new ArrayList<String>(splitter(include));
            Collections.sort(includeTenantList);
        } else {
            includeTenantList = new ArrayList<String>();
            includeTenantList.add(include);
        }

    }

    @XmlElement(name = "Exclude")
    protected void setExcludeTenants(String exclude) {
        String excludeTenants = exclude.trim();
        if (!excludeTenants.contains("*")) {
            excludeTenantList = new ArrayList<String>(splitter(exclude));
            Collections.sort(excludeTenantList);
        } else {
            excludeTenantList = new ArrayList<String>();
            excludeTenantList.add(exclude);
        }
    }

    public List<String> getIncludeTenantList() {
        return includeTenantList;
    }

    public List<String> getExcludeTenantList() {
        return excludeTenantList;
    }

    private ArrayList<String> splitter(String text) {
        String[] parts;
        ArrayList<String> temp;
        if (text.contains(",")) {
            parts = text.split(",");
        } else {
            parts = new String[] { text };
        }
        //The LinkedHashSet will contain each element only once, and in the same order as the List effectively, it's a one-liner:
        temp = new ArrayList<String>(new LinkedHashSet<String>(Arrays.asList(parts)));
        return temp;
    }
}
