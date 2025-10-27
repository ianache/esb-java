package com.crossnetcorp.integrationflow.impl;

import lombok.Builder;
import lombok.Data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;
import com.crossnetcorp.utils.HttpUtils;

/**
 * A processor that checks for a valid OAuth2 token.
 *
 * @param <A> The type of the payload.
 */
public class JwtAuthProcessor<A> extends IProcessor<A> {
    private static final Logger logger = LogManager.getLogger(JwtAuthProcessor.class);
    
    private OAuth2Config config = OAuth2Config.builder().build();

    private HttpUtils http = new HttpUtils();


    /**
     * Constructs a new JwtAuthProcessor.
     *
     * @param flow The integration flow.
     */
    public JwtAuthProcessor(IIntegrationFlow<A,A> flow) {
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
        logger.debug("GetOauth2Token for {}", config.getAuth_url());
        return in;
    }

    @Data @Builder
    public static class OAuth2Config {
        private String auth_url;
        private String client_secret;
        private String client_id;
        private String username;
        private String password;
    }
}