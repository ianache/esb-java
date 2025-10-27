package com.crossnetcorp.integrationflow.templating.impl;

import com.crossnetcorp.integrationflow.FlowMessage;
import com.crossnetcorp.integrationflow.templating.ITemplateService;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A template service that uses the Mustache template engine.
 *
 * @param <X> The type of the payload.
 */
public class MustashTemplateService<X> implements ITemplateService<X> {
    private static final Logger logger = LogManager.getLogger(MustashTemplateService.class);

    /**
     * A constant representing an empty JSON object string, returned on processing failure.
     */
    private static String EMPTY_JSON = "{}";

    /**
     * The factory for creating and compiling Mustache templates.
     */
    private MustacheFactory mustacheFactory;

    /**
     * A cache to store compiled Mustache templates, keyed by a unique template ID.
     */
    private Map<String, Mustache> templates;

    /**
     * Constructs a new MustashTemplateService.
     */
    public MustashTemplateService() {
        this.mustacheFactory = new DefaultMustacheFactory();
        this.templates = new java.util.concurrent.ConcurrentHashMap<>(); // Use a thread-safe map
    }

    /**
     * Transforms a document using a Mustache template.
     *
     * @param template The Mustache template to use for the transformation.
     * @param in       The input message.
     * @return The transformed document as a string.
     */
    @Override
    public String transform(String template, FlowMessage<X> in) {
        String templateId = UUID.randomUUID().toString();
        MustacheFactory mf = new DefaultMustacheFactory();

        // Compile the template
        logger.debug("Template content: {}", template);
        Mustache mustache = this.mustacheFactory.compile(new StringReader(template), templateId);

        if (mustache != null) {
            StringWriter strWriter = new StringWriter();
            // Create a new map to avoid modifying the original context
            Map<String, Object> variables = new java.util.HashMap<>(in.getProperties());
            variables.put("payload", in.getPayload());
            variables.put("headers", in.getHeaders());

            variables.put("this", this);
            variables.put("fnDateFormat", new DateFormatterFunction());

            logger.info("Variables: {}", variables);
            mustache.execute(strWriter, variables);
            return strWriter.toString();
        }

        return in.getPayload() != null ? in.getPayload().toString() : "";
    }

    /**
     * A function to format dates in Mustache templates.
     */
    public class DateFormatterFunction implements Function<String, String> {
        /**
         * Default constructor.
         */
        public DateFormatterFunction() {
        }

        /**
         * Applies the date formatting function.
         *
         * @param input The input date string.
         * @return The formatted date string.
         */
        @Override
        public String apply(String input) {
            return input;
        }

    }

}