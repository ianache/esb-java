package com.crossnetcorp.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of integration flow configurations.
 */
public class Configurations {
    private List<Configuration> flows = new ArrayList<>();

    /**
     * Constructs a new Configurations object.
     */
    public Configurations() {
    }

    /**
     * Gets the list of flow configurations.
     *
     * @return The list of flow configurations.
     */
    public List<Configuration> getFlows() {
        return flows;
    }

    /**
     * Sets the list of flow configurations.
     *
     * @param flows The list of flow configurations.
     */
    public void setFlows(List<Configuration> flows) {
        this.flows = flows;
    }
}