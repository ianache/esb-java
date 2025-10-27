package com.crossnetcorp.integrationflow.impl;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A processor that executes a Python script.
 *
 * @param <A> The type of the payload.
 */
public class PythonScriptProcessor<A> extends IProcessor<A> {
    private static final Logger logger = LogManager.getLogger(PythonScriptProcessor.class);

    /**
     * Codigo Python
     */
    private String code = "";

    /**
     * Constructs a new PythonScriptProcessor.
     *
     * @param flow The integration flow.
     */
    public PythonScriptProcessor(IIntegrationFlow<A,A> flow) {
        super();
        this.setFlow(flow);
    }

    /**
     * Constructs a new PythonScriptProcessor with the specified code.
     *
     * @param flow The integration flow.
     * @param code The Python script to execute.
     */
    public PythonScriptProcessor(IIntegrationFlow<A,A> flow,String code) {
        super();
        this.setFlow(flow);
        this.code = code;
    }

    /**
     * Processes the message by executing the Python script.
     *
     * @param in The input message.
     * @return The output message.
     * @throws FlowException If an error occurs during script execution.
     */
    @SuppressWarnings("unchecked")
    @Override
    public FlowMessage<A> process(FlowMessage<A> in) throws FlowException {
        try (Context context = Context.newBuilder("python")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(s -> true)
                .build()) {
            
            in.getProperties().forEach((k, v) -> context.getBindings("python").putMember(k.strip(), v));
            context.getBindings("python").putMember("payload", in.getPayload());
            context.getBindings("python").putMember("this", this);

            Value result = context.eval("python", code != null ? code.trim() : "");
            logger.debug("Eval() result: {}", result);

            if(result.isNull()) {
                in.setPayload(in.getPayload());
            } else if(result.isHostObject() && this.getFlow().getPayloadType().isInstance(result.asHostObject())) {
                A typedResult = (A) result.asHostObject();
                in.setPayload(typedResult);
            } else {
                Class<A> _clazz = this.getFlow().getPayloadType();
                A typedResult = result.as(_clazz); 
                in.setPayload(typedResult);
            }

        } catch (Exception e) {
            throw new FlowException(this.getFlow().getName(), e.getMessage());
        }

        return in;     
    }

    public void setCode(String code) {
        this.code = code;
    }

}