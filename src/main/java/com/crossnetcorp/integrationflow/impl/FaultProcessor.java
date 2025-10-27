package com.crossnetcorp.integrationflow.impl;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;

public class FaultProcessor<A> extends IProcessor<A> {
    /**
     * Error code
    */ 
    private Integer code;
    /**
     * Error message
    */
    private String message;

    public FaultProcessor(IIntegrationFlow<A,A> flow) {
        super();
        this.setFlow(flow);
    }

    @Override
    public FlowMessage<A> process(FlowMessage<A> in) throws FlowException {
        return in;
    }
}
