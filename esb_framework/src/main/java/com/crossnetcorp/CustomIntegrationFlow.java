package com.crossnetcorp;

import com.crossnetcorp.config.Configurations;
import com.crossnetcorp.config.Step;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crossnetcorp.config.Configuration;

/**
 * A custom integration flow that can be configured with a YAML file.
 *
 * @param <A> The type of the payload.
 */
public class CustomIntegrationFlow<A> extends IIntegrationFlow<A, A>{
    private static final Logger logger = LogManager.getLogger(CustomIntegrationFlow.class);
    
    private final Properties classMapping = new Properties();
    private String name;

    /**
     * Constructs a new CustomIntegrationFlow.
     *
     * @param configFile The path to the configuration file (without the .yaml extension).
     * @param flowName The name of the flow to use from the configuration file.
     */
    public CustomIntegrationFlow(String flowName) {
        super();
        this.name = flowName;
        this.setPayloadType(getPayloadTypeFromSuperclass());
        // this.loadConfiguration(configFile, flowName != null ? flowName : "default");
    }

    @Override
    public void setUpflows(Configuration config) {
        if(config.name != null && config.name.equalsIgnoreCase(this.name)) {
            this.setInflow(buildFlow(config.inflow));
            this.setOutflow(buildFlow(config.outflow));
            this.setExceptionflow(buildFlow(config.exception));
        }
    }

    /**
     * Builds a list of processors for a flow.
     *
     * @param steps The list of steps in the flow.
     * @return A list of processors.
     */
    @SuppressWarnings("unchecked")
    private List<IProcessor<A>> buildFlow(List<Step> steps) {
        List<IProcessor<A>> list = new ArrayList<>();
        Object [] args = new Object [] { this };

        for(Step step : steps) {
            try {
                Class<?> clazz = getClassByName(step.type); 
                Constructor<?> _constructor = clazz.getDeclaredConstructor(IIntegrationFlow.class);
                IProcessor<A> processor = (IProcessor<A>)_constructor.newInstance(args);
                if(step.properties != null) {
                    processor.loadProperties(step.properties);
                }
                list.add(processor);
            } catch(Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        return list;
    }

    /**
     * Gets the payload type from the superclass's generic type arguments.
     *
     * @return The payload type.
     * @throws IllegalStateException If the generic type cannot be inferred.
     */
    @SuppressWarnings("unchecked")
    private Class<A> getPayloadTypeFromSuperclass() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                // Se asume que 'A' es el primer argumento genérico de la superclase.
                return (Class<A>) typeArguments[0];
            }
        }
        // Manejo del caso en que la reflexión falle
        throw new IllegalStateException("No se pudo inferir el tipo genérico 'A' del payload.");
    }

    static String CATALOG_PATH="catalog.properties";
    private Class<?> getClassByName(String tag) throws ClassNotFoundException {
        if(this.classMapping.isEmpty()) {
            try (InputStream input = getClass().getClassLoader().getResourceAsStream(CATALOG_PATH)) {
                if (input == null) {
                    logger.error("Catalog file {} not found ", CATALOG_PATH);
                }
                classMapping.load(input);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return Class.forName(classMapping.getProperty(tag));
    }

    public String getName() {
        return this.name;
    }
}