package com.crossnetcorp.integrationflow.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;
import com.crossnetcorp.integrationflow.templating.ITemplateService;
import com.crossnetcorp.integrationflow.templating.impl.MustashTemplateService;
import com.crossnetcorp.integrationflow.templating.impl.VelocityTemplateService;

/**
 * A processor that transforms a JSON document using a template engine.
 *
 * @param <A> The type of the payload.
 */
public class JsonTransformationProcessor<A> extends IProcessor<A> {
    private static final Logger logger = LogManager.getLogger(JsonTransformationProcessor.class);
    
    private static String VELOCITY_TEMPLATE = "velocity";
    private static String MUSTASH_TEMPLATE = "mustash";

    private String engine = VELOCITY_TEMPLATE;
    private String code = "";

    private ITemplateService<A> service = null;

    /**
     * Constructs a new JsonTransformationProcessor.
     *
     * @param flow The integration flow.
     */
    public JsonTransformationProcessor(IIntegrationFlow<A,A> flow) {
        super();
        this.setFlow(flow);
    }

    /**
     * Constructs a new JsonTransformationProcessor with the specified template engine and code.
     *
     * @param templateEngine The name of the template engine to use.
     * @param templateCode   The template code.
     */
    public JsonTransformationProcessor(String engine, String code) {
        super();
        this.engine = engine != null ? engine : VELOCITY_TEMPLATE;
        this.code = code != null ? code : "";
    }

    /**
     * Performs a transformation of the input message into a new output message,
     * where only the payload changes.
     *
     * @param in The input message.
     * @return The output message.
     * @throws FlowException If an error occurs during the transformation.
     */
    @SuppressWarnings("unchecked")
    @Override
    public FlowMessage<A> process(FlowMessage<A> in) throws FlowException {
        logger.debug("Starting transformation");

        this.service = (VELOCITY_TEMPLATE.equals(this.engine))
            ? new VelocityTemplateService<>()
            : new MustashTemplateService<>();

        FlowMessage<A> out = in;
        String result = this.service.transform(this.code, in);
        out.setPayload( (A)result );
        logger.debug("Document transformed: \n", out.getPayload().toString());
        return out;
    }

    public void setEngine(String value) {
        this.engine = value;
    }

    public void setCode(String value) {
        this.code = value;
    }

}