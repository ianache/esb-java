package com.crossnetcorp.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single step in an integration flow.
 */
public class Step {
    /**
     * The name of the step.
     */
    public String name;
    /**
     * The documented description of the step in the flow.
     */
    public String description;
    /**
     * The name of the class to which the step should be mapped within the flow.
     */
    public String type;
    /**
     * The properties of the step.
     */
    public Map<String, Object> properties = new HashMap<String, Object>();
}