package com.crossnetcorp.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single integration flow configuration.
 */
public class Configuration {
    /**
     * The name of the flow.
     */
    public String name;
    /**
     * A description of the flow.
     */
    public String description;
    /**
     * The list of steps in the inflow.
     */
    public List<Step> inflow = new ArrayList<Step>();
    /**
     * The list of steps in the outflow.
     */
    public List<Step> outflow = new ArrayList<Step>();
    /**
     * The list of steps in the exception flow.
     */
    public List<Step> exception = new ArrayList<Step>();
}