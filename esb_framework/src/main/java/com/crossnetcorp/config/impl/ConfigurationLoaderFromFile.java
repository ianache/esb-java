package com.crossnetcorp.config.impl;

import com.crossnetcorp.CustomIntegrationFlow;
import com.crossnetcorp.config.IConfigurationLoader;
import com.crossnetcorp.config.ConfigurationException;
import com.crossnetcorp.config.Configurations;
import com.crossnetcorp.integrationflow.IIntegrationFlow;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ConfigurationLoaderFromFile<X> extends IConfigurationLoader<X> {
    private static final Logger logger = LogManager.getLogger(ConfigurationLoaderFromFile.class);
    private final static String DEFAULT_CONFIG = "flows.yaml";
    private Configurations configs = new Configurations();
    private String configFileName;

    public ConfigurationLoaderFromFile(Class<? extends IIntegrationFlow<?, ?>> flowClass, String fileName) {
        super(flowClass);
        if(fileName == null || "".equals(fileName.trim())) fileName = DEFAULT_CONFIG;
        this.configFileName = fileName.endsWith(".yaml") ? fileName : fileName + ".yaml";
    }

    public Map<String, IIntegrationFlow<X, X>> loadConfigurations() throws ConfigurationException {
        Map<String, IIntegrationFlow<X, X>> flows = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        logger.info("Reading flows configuration file: {}", this.configFileName);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (inputStream == null) {
                logger.error("Archivo de configuración NO encontrado en el classpath: {}", configFileName);
                throw new ConfigurationException(String.format("Archivo de configuración NO encontrado en el classpath: %s", configFileName));
            }

            this.configs = mapper.readValue(inputStream, Configurations.class);
            logger.debug(configs.toString());

            this.configs.getFlows().forEach(flow -> {
                try {
                    Constructor<?> _constructor = this.flowClass.getDeclaredConstructor(String.class);
                    CustomIntegrationFlow<X> _flow = (CustomIntegrationFlow<X>)_constructor.newInstance(new Object [] {flow.name});
                    _flow.setUpflows(flow);
                    logger.info("Registering flow: {}", flow.name);
                    flows.put(flow.name, _flow);
                } catch(Throwable ex) {
                    logger.error(ex.getMessage());
                }
            });

        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ConfigurationException(e.getMessage(), e);
        }

        return flows;
    }
}
