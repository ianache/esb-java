package com.crossnetcorp.esb.presentation;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping; // Para peticiones POST
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody; // Para recibir el cuerpo de la petición
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import com.crossnetcorp.esb.application.GatewayApplicationService;



@RestController
@RequestMapping("/gateway/api/v1")
public class GatewayController {

    private static final Logger logger = LogManager.getLogger(GatewayController.class);

    @Autowired
    private GatewayApplicationService gatewayService;

/**
     * Endpoint para procesar datos de texto para un recurso específico.
     * La URL será del tipo: POST /api/resource/{nombreDelRecurso}
     * El cuerpo (Body) de la petición debe contener un valor de tipo texto.
     *
     * @param resource El valor de la variable de ruta 'resource'.
     * @param textPayload El contenido de texto enviado en el cuerpo de la petición.
     * @return ResponseEntity con un mensaje de confirmación.
     */
    @PostMapping(
        value = "/{domain}/{resource}",
        consumes = MediaType.APPLICATION_JSON_VALUE, // Espera JSON
        produces = MediaType.APPLICATION_JSON_VALUE  // Devuelve JSON
    )
    public ResponseEntity<Object> processResourceData(
            @RequestHeader Map<String, String> headersMap,
            @PathVariable String domain,
            @PathVariable String resource,
            @RequestBody String payload) {

        logger.debug("--- Nueva Petición ---");
        logger.debug("Recurso (Path Variable) solicitado: " + resource);
        logger.debug("Cuerpo (Body) de texto recibido:\n" + payload);
        logger.debug("------------------------");

        // Aquí iría la lógica de negocio para guardar o procesar el 'textPayload'
        // asociado al 'resource'.
        Map<String, Object> headers = new HashMap<>();
        headersMap.forEach((k, v) -> headers.put(k,v) );
        Object result = gatewayService.executeFlow(domain, resource,payload, headers);
        logger.debug(result);

        //String responseMessage = String.format(
        //    "Datos de texto para el recurso '%s' recibidos correctamente. Contenido procesado: **%d** caracteres.",
        //    resource,
        //    payload.length()
        //);

        //logger.info(responseMessage);

        // Retorna un código de estado 200 OK
        return ResponseEntity.ok(result);
    }
}
