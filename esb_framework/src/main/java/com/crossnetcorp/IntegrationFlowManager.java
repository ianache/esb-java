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

import com.crossnetcorp.config.ConfigurationException;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.crossnetcorp.config.Configurations;
import com.crossnetcorp.config.IConfigurationLoader;
import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.HandleProcess;

public class IntegrationFlowManager<X> {
    private static final Logger logger = LogManager.getLogger(IntegrationFlowManager.class);

    private Configurations configs = new Configurations();
    private Map<String, IIntegrationFlow<X, X>> flows;
    private Class<? extends IIntegrationFlow<?, ?>> flowClass = null;
    private IConfigurationLoader<X> loader = null;

    public IntegrationFlowManager(IConfigurationLoader<X> loader, Class<? extends IIntegrationFlow<?, ?>> flowClass) {
        this.flowClass = flowClass;
        this.flows = new HashMap<>();
        this.loader = loader;
    }

    /**
     * Loads the configuration from the specified YAML file.
     *
     * @param configFile The path to the configuration file (without the .yaml extension).
     * @param flowName The name of the flow to use.
     */
    /* public void loadConfigurationFromFile(String configFile) {
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
    */
    public void setUp() throws ConfigurationException {
        this.flows = (loader != null) ? loader.loadConfigurations(): new HashMap<>();
    }

    public IIntegrationFlow<X,X> getFlow(String name) {
        return this.flows.get(name);
    }

    /**
     * Realiza la ejecucion sincrona de un flujo de integracion
     * @param name: nombre del flow a ejecutar
     * @param in: Mensaje de entrada al flow
     * @return Mensaje final contenido la respuesta del flow
     */
    public FlowMessage<X> handle(String name, FlowMessage<X> in, HandleProcess<X> process) throws FlowException {
        IIntegrationFlow<X,X> flow = this.flows.get(name);
        if(flow == null) throw new FlowException(name, String.format("Flow '%s' not found",name));
        return flow.handle(in, process);
    }
}
