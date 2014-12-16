/*
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.tenant.artifact.internal;

import org.wso2.carbon.base.api.ServerConfigurationService;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * This singleton data holder contains all the data required by the Tenant.artifact OSGi bundle
 */
public class DataHolder {
    private ConfigurationContextService configurationContextService;
    private RealmService realmService;
    private ServerConfigurationService serverConfigurationService;
    private static DataHolder instance = new DataHolder();

    public static DataHolder getInstance() {
        return instance;
    }

    private DataHolder() {
    }

    public ConfigurationContextService getConfigurationContextService() {
        return configurationContextService;
    }

    public void setConfigurationContextService(ConfigurationContextService configContextService) {
        configurationContextService = configContextService;
    }

    public RealmService getRealmService() {
        return realmService;
    }

    public void setRealmService(RealmService realm) {
        realmService = realm;
    }

    public ServerConfigurationService getServerConfigurationService() {
        return serverConfigurationService;
    }

    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }
}
