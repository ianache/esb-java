package com.crossnetcorp.integrationflow.templating;

import com.crossnetcorp.integrationflow.FlowMessage;

/**
 * An interface for a template service.
 *
 * @param <X> The type of the payload.
 */
public interface ITemplateService<X> {
    /**
     * Transforms a document.
     *
     * @param template The template to use for the transformation.
     * @param in       The input message.
     * @return The transformed document as a string.
     */
    public String transform(String template, FlowMessage<X> in);
}