package com.crossnetcorp.integrationflow.templating.impl;

import java.io.StringWriter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;



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
        VelocityContext context = new VelocityContext();
        context.put("payload", in.getPayload());
        in.getProperties().forEach((k,v)->context.put(k,v));
        context.put("headers", in.getHeaders());
        context.put("library", this);

        String templateId = UUID.randomUUID().toString();

        StringWriter writer = new StringWriter();
        boolean result = Velocity.evaluate(context, writer, templateId, template);
        String document = result ? writer.getBuffer().toString() : EMPTY_JSON; 
        return document;
    }
    /**
     * List of functions
     */
    public String strToDate(String valor, String sourceFormat, String targetFormat) {
        if (valor == null || valor.trim().isEmpty()) {
            return valor;
        }
        valor = valor.replaceAll("\"","");

        try {
            // 2. Crear los formateadores
            DateTimeFormatter sourceFormatter = DateTimeFormatter.ofPattern(sourceFormat);
            DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern(targetFormat);

            // 3. Determinar si es solo fecha o incluye tiempo
            // Intentar primero parsear como LocalDateTime (fecha y hora)
            Object dateObj;
            try {
                dateObj = LocalDateTime.parse(valor, sourceFormatter);
            } catch (DateTimeParseException e) {
                // Si falla, intentar como LocalDate (solo fecha)
                try {
                    dateObj = LocalDate.parse(valor, sourceFormatter);
                } catch (DateTimeParseException ex) {
                    // Si ambos fallan, lanzar el error para el manejo general
                    throw ex;
                }
            }
            
            // 4. Formatear la fecha/hora al formato de destino
            // Usamos el método 'format' del objeto de fecha con el formateador de destino
            return targetFormatter.format((java.time.temporal.TemporalAccessor) dateObj);

        } catch (DateTimeParseException e) {
            // Manejo de error: la cadena de fecha no coincide con el sourceFormat
            System.err.println("Error de parseo: El valor '" + valor + 
                               "' no coincide con el formato '" + sourceFormat + "'.");
            // Devuelve la cadena original o un String de error según se prefiera
            return valor; 
        } catch (IllegalArgumentException e) {
            // Manejo de error: El sourceFormat o targetFormat no son válidos
            System.err.println("Error de formato: El sourceFormat o targetFormat es inválido.");
            return valor;
        }
    }

    public String strToTime(String valor, String sourceFormat, String targetFormat) {
        return strToDate(valor,sourceFormat,targetFormat);
    }
    
}