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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Class that represents the init-tenant.xml
 */
@XmlRootElement(name = "Tenants")
public class TenantArtifactConfiguration {
    @XmlElementWrapper(name = "Includes", required = true)
    @XmlElement(name = "Include")
    private LinkedHashSet<String> includeTenantList;

    @XmlElementWrapper(name = "Excludes", required = true)
    @XmlElement(name = "Exclude")
    private LinkedHashSet<String> excludeTenantList;
    private boolean isIncludeAll;
    private boolean isExcludeAll;

    public List<String> getIncludeTenantList() {
        if (includeTenantList == null) {
            includeTenantList = new LinkedHashSet<String>();
        }
        List<String> arrayListIncludes = new ArrayList<String>(includeTenantList);
        if (!includeTenantList.isEmpty()) {
            Collections.sort(arrayListIncludes);
        }
        return arrayListIncludes;
    }

    public List<String> getExcludeTenantList() {
        if (excludeTenantList == null) {
            excludeTenantList = new LinkedHashSet<String>();
        }
        List<String> arrayListExcludes = new ArrayList<String>(excludeTenantList);
        if (!excludeTenantList.isEmpty()) {
            Collections.sort(arrayListExcludes);
            if (excludeTenantList.contains("*")) {
                isExcludeAll = true;
            }
        }
        return arrayListExcludes;
    }

    public boolean isIncludeAll() {
        if (includeTenantList.contains("*")) {
            isIncludeAll = true;
        }
        return isIncludeAll;
    }

    public boolean isExcludeAll() {
        if (excludeTenantList.contains("*")) {
            isExcludeAll = true;
        }
        return isExcludeAll;
    }
}
