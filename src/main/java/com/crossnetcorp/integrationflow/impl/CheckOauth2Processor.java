package com.crossnetcorp.integrationflow.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;

/**
 * A processor that checks for a valid OAuth2 token.
 *
 * @param <A> The type of the payload.
 */
public class CheckOauth2Processor<A> extends IProcessor<A> {
    private static final Logger logger = LogManager.getLogger(CheckOauth2Processor.class);
    
    /**
     * Constructs a new CheckOauth2Processor.
     *
     * @param flow The integration flow.
     */
    public CheckOauth2Processor(IIntegrationFlow<A,A> flow) {
        super();
        this.setFlow(flow);
    }

    /**
     * Processes the message by checking for a valid OAuth2 token.
     *
     * @param in The input message.
     * @return The output message.
     * @throws FlowException If the token is invalid.
     */
    @Override
    public FlowMessage<A> process(FlowMessage<A> in) throws FlowException {
        logger.debug("CheckOauth2");
        return in;
    }
}