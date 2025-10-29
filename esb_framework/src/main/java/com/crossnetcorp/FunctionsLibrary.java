package com.crossnetcorp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionsLibrary {
    private static final Logger logger = LogManager.getLogger(FunctionsLibrary.class);

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
            logger.error("Error de parseo: El valor '" + valor + 
                               "' no coincide con el formato '" + sourceFormat + "'.");
            // Devuelve la cadena original o un String de error según se prefiera
            return valor; 
        } catch (IllegalArgumentException e) {
            // Manejo de error: El sourceFormat o targetFormat no son válidos
            logger.error("Error de formato: El sourceFormat o targetFormat es inválido.");
            return valor;
        }
    }

    public String strToTime(String valor, String sourceFormat, String targetFormat) {
        return strToDate(valor,sourceFormat,targetFormat);
    }

    /*
     * Permite convertir un valor numerico (segundos) a una representacion ISO de hora.
     */
    public String secondsToTime(String valor, String targetFormat) {
        if (valor == null || valor.trim().isEmpty()) { return valor; }
        try {
            valor = valor.replaceAll("\"","");
            long segundos = Long.parseLong(valor.trim());
            LocalTime hora = LocalTime.MIN.plusSeconds(segundos);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(targetFormat);
            return hora.format(formatter);
        } catch (NumberFormatException | DateTimeParseException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}
