package com.crossnetcorp.integrationflow.impl;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A processor that validates a JSON document against a schema.
 *
 * @param <A> The type of the payload.
 */
public class JsonDocValidatorProcessor<A> extends IProcessor<A> {
    private static final Logger logger = LogManager.getLogger(JsonDocValidatorProcessor.class);

    private static String DOCUMENT_SCHEMA = "schema";
    
    /**
     * Constructs a new JsonDocValidatorProcessor.
     *
     * @param flow The integration flow.
     */
    public JsonDocValidatorProcessor(IIntegrationFlow<A,A> flow) {
        super();
        this.setFlow(flow);
        this.setProp(DOCUMENT_SCHEMA, null);
    }

    /**
     * Processes the message by validating the JSON payload against the schema.
     *
     * @param in The input message.
     * @return The output message.
     * @throws FlowException If the validation fails.
     */
    @Override
    public FlowMessage<A> process(FlowMessage<A> in) throws FlowException {
        String schemaContent = this.getProp(DOCUMENT_SCHEMA).toString();
        JSONObject rawSchema = new JSONObject(new JSONTokener(schemaContent));
        Schema schema = SchemaLoader.load(rawSchema);
        JSONObject jsonObject = new JSONObject(in.getPayload().toString());
        try { 
            schema.validate(jsonObject); 
            logger.info("✅ The JSON document is VALID according to the schema.");
        } catch(org.everit.json.schema.ValidationException e) {
            logger.error(e.getMessage());
            in.setException(new FlowException(getFlow().getName(), "❌ ".concat(e.getMessage()).concat(e.getCausingExceptions().toString())));
        }
        return in;
    }
}