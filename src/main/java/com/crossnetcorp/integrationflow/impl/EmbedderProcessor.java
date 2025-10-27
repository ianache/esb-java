package com.crossnetcorp.integrationflow.impl;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.crossnetcorp.integrationflow.FlowException;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.integrationflow.IProcessor;
import com.crossnetcorp.utils.HttpUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EmbedderProcessor<A> extends IProcessor<A> { 
    private static final String DEFAULT_PROVIDER = "ollama";
    private static final String DEFAULT_MODEL = "embeddinggemma:latest";

    private static final Logger logger = LogManager.getLogger(EmbedderProcessor.class);
    private HttpUtils http = new HttpUtils();

    private EmbedderConfig embedder = EmbedderConfig.builder().build();

    public EmbedderProcessor(IIntegrationFlow<A,A> flow) {
        super();
        this.setFlow(flow);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FlowMessage<A> process(FlowMessage<A> in) throws FlowException {
        String payload = String.format("{\"model\": \"%s\", \"input\": \"%s\"}",
            embedder.getModel(),
            in.getPayload().toString());
        /*HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.embedder.getApi_url()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload)) // Define el método GET
                .build();*/
        try {
            HttpResponse<String> result = http.post(this.embedder.getApi_url(), payload.toString());
            // client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info(result.body());
            JSONObject json = new JSONObject(result.body());
            JSONArray embeddings = json.getJSONArray("data");
            json = new JSONObject();
            json.put("embedding", embeddings.getJSONObject(0).getJSONArray("embedding"));
            json.put("content", in.getPayload().toString());
            in.setPayload((A)json);
        } catch(Exception e) {
            logger.error(e.getMessage());
            in.setException(new FlowException(getFlow().getName(), e.getMessage()));
        }
        return in;
    }

    @Data @Builder
    public static class EmbedderConfig {
        @Builder.Default 
        private String provider = DEFAULT_PROVIDER; // Default to ollama
        @Builder.Default
        private String model = DEFAULT_MODEL;
        @Builder.Default
        private String api_url = "http://localhost:11434/v1/embeddings";
        @Builder.Default
        private String api_key = "noname";
    }

    @SuppressWarnings("unchecked")
    public void setEmbedder(Object value) {
        if(value instanceof EmbedderConfig) {
            this.embedder = (EmbedderConfig)value;
        } else {
            if(value instanceof LinkedHashMap) {
                LinkedHashMap<String, Object> obj = (LinkedHashMap<String, Object>)value;
                this.embedder = EmbedderConfig.builder()
                    .model((String)obj.getOrDefault("model",DEFAULT_MODEL))
                    .api_url((String)obj.get("api_url"))
                    .api_key((String)obj.get("api_key"))
                    .provider((String)obj.getOrDefault("provider", DEFAULT_PROVIDER))
                    .build();
            }
        }
    }
}
