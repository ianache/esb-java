package com.crossnetcorp.integrationflow.impl;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;

/**
 * A processor that executes a JavaScript script.
 *
 * @param <A> The type of the payload.
 */
public class JavaScriptProcessor<A> extends IProcessor<A> {
    /**
     * Codigo del JavaScript
     */
    private String code;

    private static String JAVASCRIPT_CODE = "code";

    /**
     * Constructs a new JavaScriptProcessor.
     *
     * @param flow The integration flow.
     */
    public JavaScriptProcessor(IIntegrationFlow<A,A> flow) {
        super();
        this.setFlow(flow);
        this.setProp(JAVASCRIPT_CODE, "");
    }

    /**
     * Constructs a new JavaScriptProcessor with the specified code.
     *
     * @param flow The integration flow.
     * @param code The JavaScript code to execute.
     */
    public JavaScriptProcessor(IIntegrationFlow<A,A> flow,String code) {
        super();
        this.setFlow(flow);
        this.setProp(JAVASCRIPT_CODE, code != null ? JAVASCRIPT_CODE : "");
    }

    /**
     * Processes the message by executing the JavaScript code.
     *
     * @param in The input message.
     * @return The output message.
     * @throws FlowException If an error occurs during script execution.
     */
    @SuppressWarnings("unchecked")
    @Override
    public FlowMessage<A> process(FlowMessage<A> in) throws FlowException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("graal.js");

        try {
            // Step 1: Se establecen variables de contexto
            in.getProperties().forEach((k,v) -> { engine.put(k.strip(),v); });
            engine.put("payload",in.getPayload());
            engine.put("this",this);
            // Step 2: Se ejecuta el codigo proporcionado en el Step
            Object result = engine.eval(this.code != null ? this.code.trim() : "");

            A typedResult = (A) result;
            in.setPayload(typedResult);
        } catch (Exception e) {
            throw new FlowException(this.getFlow().getName(),e.getMessage());
        }

        return in;
    }

    public void setCode(String code) {
        this.code = code;
    }
}