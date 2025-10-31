package com.crossnetcorp.config.impl;

import com.crossnetcorp.CustomIntegrationFlow;
import com.crossnetcorp.config.IConfigurationLoader;
import com.crossnetcorp.config.ConfigurationException;
import com.crossnetcorp.config.Configurations;
import com.crossnetcorp.integrationflow.IIntegrationFlow;

import java.io.File;
import java.io.FileInputStream;
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

        // Bandera para saber si el archivo es local o de classpath
        boolean isAbsolutePath = new File(configFileName).isAbsolute();

        try (InputStream inputStream = getStream(configFileName, isAbsolutePath)) {
            if (inputStream == null) {
                String location = isAbsolutePath ? "absolute system path" : "classpath";
                logger.error("Configuration file NOT FOUND at {}: {}", location, configFileName);
                throw new ConfigurationException(String.format("Configuration file NOT FOUND at %s: %s", location, configFileName));
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

    /**
     * Método auxiliar para obtener el InputStream del recurso.
     */
    private InputStream getStream(String fileName, boolean isAbsolutePath) throws IOException {
    if (isAbsolutePath) {
        // Opción 1: Leer desde una ruta absoluta del disco
        logger.info("Loading configuration from absolute path: {}", fileName);
        return new FileInputStream(fileName);
    } else {
        // Opción 2: Leer desde el Classpath (comportamiento original)
        logger.info("Leyendo configuración desde el classpath: {}", fileName);
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
}
