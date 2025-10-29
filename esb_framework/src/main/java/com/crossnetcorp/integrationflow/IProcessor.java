package com.crossnetcorp.integrationflow;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents an abstract processor in an integration flow.
 *
 * @param <A> The type of the payload.
 */
public abstract class IProcessor<A> {
    private IIntegrationFlow<A,A> flow = null;
    private Map<String, Object> properties = new HashMap<String, Object>();

    private String from_vault;
    /** 
     * Un valor diferente a nulo indica que cuando hay procesamiento y 
     * se muta el payload lo coloca en una propiedad dentro del payload. 
     */
    private String update_payload = null;

    /**
     * Casts an object to the payload type of the flow.
     *
     * @param value The object to cast.
     * @return The casted object, or null if the payload type is not set.
     */
    public A cast_to(Object value) {
        Class<A> payloadType = this.getFlow().getPayloadType();
        return payloadType != null ? payloadType.cast(value) : null;
    }

    /**
     * Gets the names of the properties.
     *
     * @return The names of the properties.
     */
    public Set<String> getPropertiesNames() {
        return this.properties.keySet();
    }

    /**
     * Processes the incoming message and produces a new message at the output.
     *
     * @param in The input message.
     * @return The output message.
     * @throws FlowException If an error occurs during processing.
     */
    public FlowMessage<A> process(FlowMessage<A> in) throws FlowException {
        return in;
    }

    /**
     * Loads the properties for the processor.
     *
     * @param properties The properties to load.
     */
    public void loadProperties(Map<String, Object> props) {
        if(props != null) {
            props.forEach((k,v)->setProp(k, v));
        }
     }

    /**
     * Gets the properties of the processor.
     *
     * @return The properties.
     */
    final public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Gets a property by name.
     *
     * @param pName The name of the property.
     * @return The value of the property.
     */
    public Object getProp(String pName) {
        return properties.get(pName);
    }

    /**
     * Sets a property.
     *
     * @param pName  The name of the property.
     * @param pValue The value of the property.
     */
    public void setProp(String pName, Object pValue) {
        try {
            Field f = this.getClass().getDeclaredField(pName);
            if(pValue == null || f.getType().equals(Object.class)) {
                f.setAccessible(true);
                f.set(this, pValue);
            } else if(f != null && pValue != null) {
                String mn = "set" + pName.substring(0,1).toUpperCase() + pName.substring(1);
                Method method = findMethod(this.getClass(), mn);
                if(method != null) { method.invoke(this, pValue); }
            }
        } catch (Exception e) {}
        properties.put(pName, pValue);
    }

    /**
     * Sets the integration flow.
     *
     * @param flow The integration flow.
     */
    public void setFlow(IIntegrationFlow<A,A> flow) {
        this.flow = flow;
    }

    /**
     * Gets the integration flow.
     *
     * @return The integration flow.
     */
    public IIntegrationFlow<A,A> getFlow() {
        return flow;
    }

    public void setFrom_vault(String value) {
        this.from_vault = value;
    }

    public void setUpdate_payload(String value) {
        this.update_payload = value;
    }

    private Method findMethod(Class<?> clazz, String name) {
        Optional<Method> method = List.of(clazz.getMethods())
            .stream()
            .filter(m -> {
                return m.getName().equals(name) && m.getParameterCount() == 1;
            })
            .findFirst();
        return method.isPresent() ? method.get() : null;
    }
}