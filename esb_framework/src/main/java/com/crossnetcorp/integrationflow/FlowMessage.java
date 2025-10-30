package com.crossnetcorp.integrationflow;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a message that is passed through an integration flow.
 *
 * @param <A> The type of the payload.
 */
public class FlowMessage<A> {
    private Map<String, Object> headers = new HashMap<>();
    private Map<String, Object> properties = new HashMap<>();
    private A payload;
    private FlowException exception;
    private Class<A> baseType;

    /**
     * Constructs a new FlowMessage with the specified payload, headers, and properties.
     *
     * @param payload    The payload of the message.
     * @param headers    The headers of the message.
     * @param properties The properties of the message.
     */
    public FlowMessage(A payload, Map<String, Object> headers, Map<String, Object> properties) {
        super();
        this.payload = payload;
        this.headers = headers;
        this.properties = properties;
    }

    public FlowMessage(A payload) {
        super();
        this.payload = payload;
        this.headers = new HashMap<>();
        this.properties = new HashMap<>();
    }

    /**
     * Gets the headers of the message.
     *
     * @return The headers.
     */
    public Map<String, Object> getHeaders() {
        return headers;
    }

    /**
     * Sets the headers of the message.
     *
     * @param headers The headers.
     */
    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    /**
     * Gets the properties of the message.
     *
     * @return The properties.
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Sets the properties of the message.
     *
     * @param properties The properties.
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Gets the payload of the message.
     *
     * @return The payload.
     */
    public A getPayload() {
        return payload;
    }

    /**
     * Sets the payload of the message.
     *
     * @param payload The payload.
     */
    public void setPayload(A payload) {
        this.payload = payload;
    }

    /**
     * Gets the exception associated with the message.
     *
     * @return The exception.
     */
    public FlowException getException() {
        return exception;
    }

    /**
     * Sets the exception associated with the message.
     *
     * @param exception The exception.
     */
    public void setException(FlowException exception) {
        this.exception = exception;
    }
}