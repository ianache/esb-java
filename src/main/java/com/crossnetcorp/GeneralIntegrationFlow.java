package com.crossnetcorp;

/**
 * A general integration flow that extends the CustomIntegrationFlow with a payload of type Object.
 */
public class GeneralIntegrationFlow extends CustomIntegrationFlow<Object> {

    /**
     * Constructs a new GeneralIntegrationFlow.
     *
     * @param configFile The path to the configuration file (without the .yaml extension).
     * @param flowName The name of the flow to use from the configuration file.
     */
    public GeneralIntegrationFlow(String configFile, String flowName) {
        super(configFile, flowName, null);
    }

}