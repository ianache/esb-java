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
