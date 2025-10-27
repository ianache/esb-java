package com.crossnetcorp.integrationflow.impl;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A processor that converts a text payload to a JSON payload.
 *
 * @param <A> The type of the payload.
 */
public class StrJson2JavaProcessor<A> extends IProcessor<A> { 
    /**
     * Constructs a new Json2JavaProcessor.
     *
     * @param flow The integration flow.
     */
    public StrJson2JavaProcessor(IIntegrationFlow<A,A> flow) {
        super();
        this.setFlow(flow);
    }

    /**
     * Processes the message by converting the text payload to a JSON payload.
     *
     * @param in The input message.
     * @return The output message.
     * @throws FlowException If an error occurs during the conversion.
     */
    @SuppressWarnings("unchecked")
    @Override
    public FlowMessage<A> process(FlowMessage<A> in) throws FlowException {
        ObjectMapper mapper = new ObjectMapper();
        FlowMessage<A> out = in;
        try {
            // Converts the JSON string to a JsonNode object
            JsonNode json = mapper.readTree(in.getPayload().toString());
            out.setPayload((A)json);

        } catch (Exception e) { }

        return out;
    }
}