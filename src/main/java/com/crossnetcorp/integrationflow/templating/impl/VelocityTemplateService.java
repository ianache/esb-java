package com.crossnetcorp.integrationflow.templating.impl;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.templating.ITemplateService;

/**
 * A template service that uses the Velocity template engine.
 *
 * @param <X> The type of the payload.
 */
public class VelocityTemplateService<X> implements ITemplateService<X> {
    /** A constant representing an empty JSON object string, returned on processing failure. */
    private static String EMPTY_JSON = "{}"; 

    /**
     * Transforms a document using a Velocity template.
     *
     * @param template The Velocity template to use for the transformation.
     * @param in       The input message.
     * @return The transformed document as a string.
     */
    @Override
    public String transform(String template, FlowMessage<X> in) {
        VelocityEngine ve = new VelocityEngine();
        //Properties p = new Properties();
        //p.setProperty("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        //ve.init(p);

        VelocityContext context = new VelocityContext();
        context.put("payload", in.getPayload());
        in.getProperties().forEach((k,v)->context.put(k,v));
        context.put("headers", in.getHeaders());

        String templateId = UUID.randomUUID().toString();

        StringWriter writer = new StringWriter();
        boolean result = Velocity.evaluate(context, writer, templateId, template);
        String document = result ? writer.getBuffer().toString() : EMPTY_JSON; 
        return document;
    }
}