package com.crossnetcorp.config;

import java.util.Map;
import com.crossnetcorp.integrationflow.IIntegrationFlow;

public abstract class IConfigurationLoader<X> {
    protected Class<? extends IIntegrationFlow<?, ?>> flowClass = null;

    public IConfigurationLoader(Class<? extends IIntegrationFlow<?, ?>> flowClass) {
        this.flowClass = flowClass;
    }

    /**
     * Loads the configuration from the specified YAML file.
     */
    public abstract Map<String, IIntegrationFlow<X, X>> loadConfigurations() throws ConfigurationException;
}
