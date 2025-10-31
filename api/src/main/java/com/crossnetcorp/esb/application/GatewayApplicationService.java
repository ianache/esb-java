package com.crossnetcorp.esb.application;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import com.crossnetcorp.GeneralIntegrationFlow;
import com.crossnetcorp.IntegrationFlowManager;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.config.ConfigurationException;
import com.crossnetcorp.config.IConfigurationLoader;

import com.crossnetcorp.esb.infrastructure.prometheus.MetricExecutionTime;
import com.crossnetcorp.esb.domain.IEndpointsManagementDomainService;

import jakarta.annotation.PostConstruct;

@Service
public class GatewayApplicationService {
    private static final Logger logger = LogManager.getLogger(GatewayApplicationService.class);

    private IntegrationFlowManager<Object> manager;
    private IConfigurationLoader<Object> loader;

    @Autowired
    private IEndpointsManagementDomainService endpointsManager;

    public GatewayApplicationService(@Qualifier("runtimeConfigurationService") IConfigurationLoader<Object> loader) {
        this.loader = loader;
    }

    @PostConstruct
    public void init() {
       try {
            this.manager = new IntegrationFlowManager<>(
                this.loader, 
                GeneralIntegrationFlow.class
            );

            manager.setUp(); 
        } catch(ConfigurationException ex) { 
            logger.error(ex.getMessage(),ex); 
        }
    }

    @MetricExecutionTime("flow.processing.time")
    public Object executeFlow(String domain, String service, String payload, Map<String, Object> headers) {
        logger.info("Executing {} / {}", domain, service);
        FlowMessage<Object> messageIn = new FlowMessage<>((Object)payload);
        messageIn.setHeaders(headers != null ? headers : messageIn.getHeaders());
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
    }
}
