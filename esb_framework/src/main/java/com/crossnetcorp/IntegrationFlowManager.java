package com.crossnetcorp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.crossnetcorp.config.Configurations;

public class IntegrationFlowManager<X> {
    private static final Logger logger = LogManager.getLogger(IntegrationFlowManager.class);

    private Configurations configs = new Configurations();
    private Map<String, IIntegrationFlow<X, X>> flows;
    private Class<? extends IIntegrationFlow<?, ?>> flowClass = null;

    public IntegrationFlowManager(Class<? extends IIntegrationFlow<?, ?>> flowClass) {
        this.flowClass = flowClass;
        this.flows = new HashMap<>();
    }

    /**
     * Loads the configuration from the specified YAML file.
     *
     * @param configFile The path to the configuration file (without the .yaml extension).
     * @param flowName The name of the flow to use.
     */
    public void loadConfigurationFromFile(String configFile) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String resourcePath = configFile + ".yaml";
        logger.info("Reading flows configuration file: {}", resourcePath);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            // File configData = new File("classpath:" + configFile + ".yaml");
            if (inputStream == null) {
                logger.error("Archivo de configuración NO encontrado en el classpath: {}", resourcePath);
                return; 
            }

            this.configs = mapper.readValue(inputStream, Configurations.class);
            logger.debug(configs.toString());

            this.configs.getFlows().forEach(flow -> {
                try {
                    Constructor<?> _constructor = this.flowClass.getDeclaredConstructor(String.class);
                    CustomIntegrationFlow<X> _flow = (CustomIntegrationFlow<X>)_constructor.newInstance(new Object [] {flow.name});
                    _flow.setUpflows(flow);
                    logger.info("Registering flow: {}", flow.name);
                    this.flows.put(flow.name, _flow);
                } catch(Throwable ex) {
                    logger.error(ex.getMessage());
                }
            });

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public IIntegrationFlow<X,X> getFlow(String name) {
        return this.flows.get(name);
    }
}
