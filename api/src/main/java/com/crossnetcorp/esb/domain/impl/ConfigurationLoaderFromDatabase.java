package com.crossnetcorp.esb.domain.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crossnetcorp.config.ConfigurationException;
import com.crossnetcorp.config.IConfigurationLoader;
import com.crossnetcorp.esb.domain.EsbConfigurationProperties;
import com.crossnetcorp.integrationflow.IIntegrationFlow;

public class ConfigurationLoaderFromDatabase<X> extends IConfigurationLoader<X> {
    private EsbConfigurationProperties config;
    
    public ConfigurationLoaderFromDatabase(Class<? extends IIntegrationFlow<?, ?>> flowClass, EsbConfigurationProperties config) {
        super(flowClass);
        this.config = config;
    }

    public Map<String, IIntegrationFlow<X, X>> loadConfigurations() throws ConfigurationException {
        Map<String, IIntegrationFlow<X, X>> flows = new HashMap<>();
        return flows;
    }
}
