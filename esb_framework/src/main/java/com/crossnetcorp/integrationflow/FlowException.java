package com.crossnetcorp.integrationflow;

/**
 * Represents an exception that occurs during the execution of an integration flow.
 */
public class FlowException extends RuntimeException {
    private String flowId;
    private String message;
    private Exception root;

    /**
     * Constructs a new FlowException with the specified flow ID and message.
     *
     * @param flowId  The ID of the flow where the exception occurred.
     * @param message The detail message.
     */
    public FlowException(String flowId, String message) {
        super(message);
        this.flowId = flowId;
        this.message = message;
        this.root = this;
    }

    /**
     * Constructs a new FlowException with the specified flow ID, message, and root cause.
     *
     * @param flowId  The ID of the flow where the exception occurred.
     * @param message The detail message.
     * @param root    The root cause of the exception.
     */
    public FlowException(String flowId, String message, Exception root) {
        super(message);
        this.flowId = flowId;
        this.message = message;
        this.root = root;
    }    

    /**
     * Gets the ID of the flow where the exception occurred.
     *
     * @return The flow ID.
     */
    public String getFlowId() {
        return flowId;
    }

    /**
     * Gets the detail message of the exception.
     *
     * @return The detail message.
     */
    public String getMessage() {
        return message;
    } 

    /**
     * Gets the root cause of the exception.
     *
     * @return The root cause.
     */
    public Exception getRoot() {
        return this.root;
    }
}