package com.crossnetcorp.integrationflow.templating.impl;

import java.io.StringWriter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.crossnetcorp.FunctionsLibrary;
import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.templating.ITemplateService;

/**
 * A template service that uses the Velocity template engine.
 *
 * @param <X> The type of the payload.
 */
public class VelocityTemplateService<X> implements ITemplateService<X> {
    private static final Logger logger = LogManager.getLogger(VelocityTemplateService.class);

    /** A constant representing an empty JSON object string, returned on processing failure. */
    private static String EMPTY_JSON = "{}"; 

    private FunctionsLibrary functions = new FunctionsLibrary();


    /**
     * Transforms a document using a Velocity template.
     *
     * @param template The Velocity template to use for the transformation.
     * @param in       The input message.
     * @return The transformed document as a string.
     */
    @Override
    public String transform(String template, FlowMessage<X> in) {
        VelocityContext context = new VelocityContext();
        context.put("payload", in.getPayload());
        in.getProperties().forEach((k,v)->context.put(k,v));
        context.put("headers", in.getHeaders());
        context.put("library", functions);

        String templateId = UUID.randomUUID().toString();

        StringWriter writer = new StringWriter();
        boolean result = Velocity.evaluate(context, writer, templateId, template);
        String document = result ? writer.getBuffer().toString() : EMPTY_JSON; 
        return document;
    }
}