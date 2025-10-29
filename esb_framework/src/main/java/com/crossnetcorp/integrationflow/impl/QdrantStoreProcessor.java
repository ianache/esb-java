package com.crossnetcorp.integrationflow.impl;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;

public class QdrantStoreProcessor<A> extends IProcessor<A> { 
    /**
     * URL del servidor de Qdrant
     */
    private String url;
    /**
     * Clave de acceso a Qdrant
     */
    private String key;
    /**
     * Nombre de la colección en Qdrant
     */
    private String collection = "default";
    /**
     * Tamaño del vectos
     */
    private Integer vector_size = 768;
    /**
     * Funcion de similaridad
     */
    private String similarity = "cosine";

    public QdrantStoreProcessor(IIntegrationFlow<A,A> flow) {
        super();
        this.setFlow(flow);
    }

    @Override
    public FlowMessage<A> process(FlowMessage<A> in) throws FlowException {
        return in;
    }
}
