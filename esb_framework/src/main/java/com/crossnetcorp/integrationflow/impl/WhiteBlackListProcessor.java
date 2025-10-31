package com.crossnetcorp.integrationflow.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;
import com.crossnetcorp.utils.CIDRChecker;

public class WhiteBlackListProcessor<A> extends IProcessor<A> {
    private static final Logger logger = LogManager.getLogger(WhiteBlackListProcessor.class);
    
    private final static String X_FORWARDED_FOR = "X-Forwarded-For";  // Proxy Inverso o Balanceador Nginx, Apache, AWS ALB, etc.
    private final static String X_REAL_IP = "X-Real-IP";              // Comúnmente usado por Nginx.
    private final static String TRUE_CLIENT_IP = "True-Client-IP";    // Servicios CDN/Seguridad.
    
    private String kind = "whitelist";
    private List<String> cidrs = new ArrayList<>();

    public WhiteBlackListProcessor(IIntegrationFlow<A,A> flow) {
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
        FlowMessage<A> out = in;
        if(this.cidrs.isEmpty()) return out;    // Do nothing

        logger.debug("{} filtering", this.kind);
        boolean result = this.cidrs.isEmpty();

        // Determina desde donde se optiene la IP remita, segun
        // buenas practicas.
        String remoteIp = (String)in.getHeaders().get(X_FORWARDED_FOR);
        if(remoteIp == null || "".equals(remoteIp.trim())) {
            remoteIp = (String)in.getHeaders().get(X_REAL_IP);
            if(remoteIp == null || "".equals(remoteIp.trim())) {
                remoteIp = (String)in.getHeaders().get(TRUE_CLIENT_IP);
            }
        }

        // Si se ha proporcionado una IP remota se debe validar contra
        // la regla (white o black) definida.
        if(remoteIp != null && remoteIp.trim().length() > 0) {
            remoteIp = remoteIp.trim();
            CIDRChecker checker = new CIDRChecker(cidrs);
            try {
                result = checker.isIPInList(remoteIp);
            } catch(java.net.UnknownHostException e) {
                logger.error(e.getMessage());
                throw new FlowException(this.getFlow().getName(), e.getMessage());
            }
        }

        FlowException ex = null;
        // Si existe una lista negra y se la IP corresponde a dicha lista
        // se debe reportar falla.
        if(result && this.kind.equals("blacklist")) {
            ex = new FlowException(
                this.getFlow().getName(), 
                String.format("IP %s is in Blacklist", remoteIp)
            );
        } else if(!result && this.kind.equals("whitelist")) {
            ex = new FlowException(
                this.getFlow().getName(), 
                String.format("IP %s is not in Whitelist", remoteIp)
            );
        }

        out.setException(ex);
        if(ex != null) {
            throw ex;
        } else {
            return out;
        }
    }

}
