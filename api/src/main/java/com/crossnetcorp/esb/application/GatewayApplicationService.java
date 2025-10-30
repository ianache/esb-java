package com.crossnetcorp.esb.application;

import java.util.concurrent.Flow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.crossnetcorp.GeneralIntegrationFlow;
import com.crossnetcorp.IntegrationFlowManager;
import com.crossnetcorp.integrationflow.IIntegrationFlow;

import com.crossnetcorp.config.ConfigurationException;
import com.crossnetcorp.config.IConfigurationLoader;
import com.crossnetcorp.config.impl.ConfigurationLoaderFromFile;
import com.crossnetcorp.esb.infrastructure.prometheus.MetricExecutionTime;
import com.crossnetcorp.integrationflow.FlowMessage;

@Service
public class GatewayApplicationService {
    private static final Logger logger = LogManager.getLogger(GatewayApplicationService.class);

    private IConfigurationLoader<Object> loader = new ConfigurationLoaderFromFile<>(GeneralIntegrationFlow.class, CONFIG_FILE);
    private IntegrationFlowManager<Object> manager = 
        new IntegrationFlowManager<>(loader, GeneralIntegrationFlow.class);

    static String CONFIG_FILE="flows";

    public GatewayApplicationService() {
       try {
            manager.setUp(); 
        } catch(ConfigurationException ex) { 
            logger.error(ex.getMessage(),ex); 
        }
    }

    @MetricExecutionTime("flow.processing.time")
    public Object executeFlow(String domain, String service, String payload) {
        logger.info("Executing {} / {}", domain, service);
        FlowMessage<Object> messageIn = new FlowMessage<>((Object)payload);
        FlowMessage<Object> messageOut = this.manager.handle(
            service, 
            messageIn, 
            (message)-> { 
                String endpoint = (String)message.getProperties().get("endpoint");
                logger.debug(message);
                logger.info("Calling ENDPOINT {}", endpoint != null ? endpoint : "-");
                //
                // Aquí se debería invocar al endpoint determinado por (domain, service)
                // y la respuesta colocada en message.setPayload( ... )
                //
                return message;                
            });

        return messageOut.getPayload();

        /*IIntegrationFlow<Object,Object> flow = manager.getFlow(service);

        Object result = flow.handle(
            (Object)payload,
            (message) -> {
                String endpoint = (String)message.getProperties().get("endpoint");
                logger.debug(message);
                logger.info("Calling ENDPOINT {}", endpoint != null ? endpoint : "-");
                //
                // Aquí se debería invocar al endpoint determinado por (domain, service)
                // y la respuesta colocada en message.setPayload( ... )
                //
                return message;
            }
        );
        return result;*/
    }
}
