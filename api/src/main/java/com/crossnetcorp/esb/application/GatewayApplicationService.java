package com.crossnetcorp.esb.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import com.crossnetcorp.GeneralIntegrationFlow;
import com.crossnetcorp.IntegrationFlowManager;
import com.crossnetcorp.integrationflow.IIntegrationFlow;

@Service
public class GatewayApplicationService {
    private static final Logger logger = LogManager.getLogger(GatewayApplicationService.class);

    private IntegrationFlowManager<Object> manager = 
        new IntegrationFlowManager<>(GeneralIntegrationFlow.class);

    static String CONFIG_FILE="flows";

    public GatewayApplicationService() {
        this.manager.loadConfigurationFromFile(CONFIG_FILE);
    }

    public Object executeFlow(String domain, String service, String payload) {
        logger.info("Executing {} / {}", domain, service);
        IIntegrationFlow<Object,Object> flow = manager.getFlow(service);

        Object result = flow.handle(
            (Object)payload,
            (message) -> {
                logger.info(message);
                return message;
            }
        );
        return result;
    }
}
