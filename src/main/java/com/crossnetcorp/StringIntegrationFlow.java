package com.crossnetcorp;

/**
 * A string-based integration flow that extends the CustomIntegrationFlow with a payload of type String.
 */
public class StringIntegrationFlow extends CustomIntegrationFlow<String> {

    /**
     * Constructs a new StringIntegrationFlow.
     *
     * @param configFile The path to the configuration file (without the .yaml extension).
     * @param flowName The name of the flow to use from the configuration file.
     */
    public StringIntegrationFlow(String configFile, String flowName) {
        super(configFile, flowName, null);
    }

}