package com.crossnetcorp.transformation;

/**
 * Represents the input for a test case.
 */
public class TestInput {
    /**
     * The path to the flow configuration file.
     */
    public String flow;
    /**
     * The request payload.
     */
    public String request;
    /**
     * The expected response payload.
     */
    public String response;

    /**
     * Constructs a new TestInput.
     *
     * @param flow     The path to the flow configuration file.
     * @param request  The request payload.
     * @param response The expected response payload.
     */
    public TestInput(String flow, String request, String response) {
        this.flow = flow;
        this.request = request;
        this.response = response;
    }
}