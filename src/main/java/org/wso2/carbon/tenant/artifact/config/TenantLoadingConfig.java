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

package org.wso2.carbon.tenant.artifact.config;

import org.wso2.carbon.tenant.artifact.internal.DataHolder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class TenantLoadingConfig {
    private String lazyLoadingIdleTime;
    private String eagerLoadingValue;
    private LinkedHashSet<String> includeTenantList = new LinkedHashSet<String>();
    private LinkedHashSet<String> excludeTenantList = new LinkedHashSet<String>();
    private static TenantLoadingConfig instance = new TenantLoadingConfig();

    private TenantLoadingConfig() {
    }

    public String getLazyLoadingIdleTime() {
        return lazyLoadingIdleTime;
    }

    private void setLazyLoadingIdleTime(String lazyLoadingIdleTime) {
        this.lazyLoadingIdleTime = lazyLoadingIdleTime;
    }

    public String getEagerLoadingValue() {
        return eagerLoadingValue;
    }

    private void setEagerLoadingValue(String eagerLoadingValue) {
        this.eagerLoadingValue = eagerLoadingValue;
    }

    public void processEagerLoadingTenants() {
        setLazyLoadingIdleTime(DataHolder.getInstance().getServerConfigurationService()
                                         .getFirstProperty("Tenant.LoadingPolicy.LazyLoading.IdleTime"));
        String eagerLoadingString = DataHolder.getInstance().getServerConfigurationService().
                getFirstProperty("Tenant.LoadingPolicy.EagerLoading.Include");
        setEagerLoadingValue(eagerLoadingString);
        String[] tenantLoadingParam = eagerLoadingString.replaceAll("\\s", "").split(",");
        for (String params : tenantLoadingParam) {
            if (params.contains("!")) {
                excludeTenantList.add(params.replace("!", ""));
            } else {
                includeTenantList.add(params);
            }
        }
    }

    //*,!wso2.com,!test.com
    public static TenantLoadingConfig getInstance() {
        return instance;
    }

    public List<String> getExcludeTenantList() {
        return new ArrayList<String>(excludeTenantList);
    }

    public List<String> getIncludeTenantList() {
        return new ArrayList<String>(includeTenantList);
    }
}
