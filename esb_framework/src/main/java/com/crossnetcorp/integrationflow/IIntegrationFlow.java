package com.crossnetcorp.integrationflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crossnetcorp.config.Configuration;

/**
 * Represents an abstract integration flow.
 *
 * @param <A> The type of the payload.
 * @param <B> The type of the response.
 */
public abstract class IIntegrationFlow<A, B> {
    private static final Logger logger = LogManager.getLogger(IIntegrationFlow.class);

    private Class<A> payloadType;
    private String name;
    private String description;
    private List<IProcessor<A>> inflow = new ArrayList<>();
    private List<IProcessor<A>> outflow = new ArrayList<>();
    private List<IProcessor<A>> exceptionflow = new ArrayList<>();

    /**
     *  Configuracion del flujo a partir de una configuracion especifica.
     * @param config
     */
    public abstract void setUpflows(Configuration config);

    public FlowMessage<A> handle(FlowMessage<A> in, HandleProcess<A> process) throws FlowException {
        if(in == null) throw new FlowException(this.name, "Input message is null");
        
        FlowMessage<A> message = in;
        message.setException(null);
        message.setProperties(message.getProperties() == null ? new HashMap<>() : message.getProperties());
        message.setHeaders(message.getHeaders() == null ? new HashMap<>() : message.getHeaders());
        return run(message, process);
    }

    /**
     * Handles the integration flow.
     *
     * @param payload The input payload.
     * @param process The process to be executed.
     * @return The output payload.
     * @throws FlowException If an error occurs during the flow.
     */
    public A handle(A payload, HandleProcess<A> process) throws FlowException {
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        FlowMessage<A> message = new FlowMessage<>(payload, headers, properties );
        FlowMessage<A> result = run(message, process);
        return result != null ? result.getPayload() : null;
    }

    private FlowMessage<A> run(FlowMessage<A> message, HandleProcess<A> process) throws FlowException {
        if(message == null) throw new FlowException(this.name, "Input message is null");
        if(process == null) throw new FlowException(this.name, "Process must be specified");
        
        message.getProperties().put("x-flow_name", this.name);
        message.getProperties().put("x-flow_id", UUID.randomUUID().toString());

        for(IProcessor<A> processor : inflow) {
            try {
                message = processor.process(message);
            } catch (FlowException e) {
                message.setException(e);
                break;
            }
        }

        try {
            message = process.process(message);
        } catch (FlowException e) {
            logger.error(e.getMessage());
            message.setException(e);
        }

        if(message.getException() == null) {
            try {
                // Se realiza el procesamiento del flujo de respuesta de forma
                // satisfactoria.
                for(IProcessor<A> processor : outflow) {
                    message = processor.process(message);
                }
            } catch(FlowException e) {
                logger.error(e.getMessage());
                message.setException(e);
            }
        }

        if(message.getException() != null) {
            // Si se produjo un error, entonces realiza la respuesta
            // de la falla encontrada.
            for(IProcessor<A> processor : exceptionflow) {
                message = processor.process(message);
            }
            throw message.getException();
        }

        return message;
    }

    /**
     * Gets the name of the flow.
     *
     * @return The name of the flow.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the flow.
     *
     * @param name The name of the flow.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the description of the flow.
     *
     * @return The description of the flow.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the flow.
     *
     * @param description The description of the flow.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the inflow processors.
     *
     * @return The inflow processors.
     */
    public List<IProcessor<A>> getInflow() {
        return inflow;
    }

    /**
     * Sets the inflow processors.
     *
     * @param inflow The inflow processors.
     */
    public void setInflow(List<IProcessor<A>> inflow) {
        this.inflow = inflow;
    }

    /**
     * Gets the outflow processors.
     *
     * @return The outflow processors.
     */
    public List<IProcessor<A>> getOutflow() {
        return outflow;
    }   

    /**
     * Gets the payload type.
     *
     * @return The payload type.
     */
    public Class<A> getPayloadType() {
        return this.payloadType;
    }

    /**
     * Sets the payload type.
     *
     * @param payloadType The payload type.
     */
    public void setPayloadType(Class<A> payloadType) {
        this.payloadType = payloadType;
    }

    /**
     * Sets the outflow processors.
     *
     * @param outflow The outflow processors.
     */
    public void setOutflow(List<IProcessor<A>> outflow) {
        this.outflow = outflow;
    }

    /**
     * Sets the exception flow processors.
     *
     * @param flow The exception flow processors.
     */
    public void setExceptionflow(List<IProcessor<A>> flow) {
        this.exceptionflow = flow;
    }

}