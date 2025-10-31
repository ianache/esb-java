package com.crossnetcorp.esb.infrastructure.rest;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.HandleProcess;
import com.crossnetcorp.integrationflow.FlowMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndpointRestProcessor<X> implements HandleProcess<X> {
    private static final Logger logger = LogManager.getLogger(EndpointRestProcessor.class);

    @Override
    public FlowMessage<X> process(FlowMessage<X> message) throws FlowException {
        String endpoint = (String)message.getProperties().get("endpoint");
        logger.debug(message);
        logger.info("Calling ENDPOINT {}", endpoint != null ? endpoint : "-");
        //
        // Aquí se debería invocar al endpoint determinado por (domain, service)
        // y la respuesta colocada en message.setPayload( ... )
        //
        return message;              
    }
}
