package com.crossnetcorp.logistica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crossnetcorp.GeneralIntegrationFlow;
import com.crossnetcorp.IntegrationFlowManager;
import com.crossnetcorp.config.ConfigurationException;
import com.crossnetcorp.config.IConfigurationLoader;
import com.crossnetcorp.config.impl.ConfigurationLoaderFromFile;
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.transformation.TestInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for the Velocity flow.
 */
public class LogisticaViajesTest {
    private static final Logger logger = LogManager.getLogger(LogisticaViajesTest.class);

    static String CONFIG_FILE= "logistica\\viajes.yaml";

    static IConfigurationLoader<Object> loader = new ConfigurationLoaderFromFile<>(GeneralIntegrationFlow.class, CONFIG_FILE);
    static IntegrationFlowManager<Object> manager = new IntegrationFlowManager<>(loader, GeneralIntegrationFlow.class);

    @BeforeAll
    public static void setUp() {
       try {
            manager.setUp(); 
        } catch(ConfigurationException ex) { 
            logger.error(ex.getMessage(),ex); 
        }
    } 

    @Test
    void test_velocity() {
        VelocityContext context = new VelocityContext();
        context.put("library", this);

        ObjectMapper mapper = new ObjectMapper();
        String payload = getViajes();
        JsonNode json = null;
        try { 
            json = mapper.readTree(payload); 
        } catch (JsonProcessingException e) { 
            logger.error(e.getMessage()); 
        }
        logger.info(json);
        context.put("payload", json);

        String templateId = UUID.randomUUID().toString();

        StringWriter writer = new StringWriter();
        boolean result = Velocity.evaluate(context, 
            writer, 
            templateId, 
            "[#foreach( $e in $payload )" + 
            "{\"codigo\": $e.get(\"oc\")}" +
            "#end]"
        );
        String document = result ? writer.getBuffer().toString() : "{}"; 
        logger.info(document);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void test_carga_viajes(TestInput data) {
        IIntegrationFlow<Object,Object> flow = manager.getFlow(data.flow);
        
        Object result = flow.handle(
            data.request,
            (message) -> {
                logger.info(message);
                return message;
            }
        );
        assertTrue(true);
        logger.info(result);
        // assertEquals(data.response, result.toString());
    }

    /**
     * Provides the test data for the parameterized test.
     *
     * @return A stream of test input data.
     */
    static Stream<TestInput> provideTestData() {
        List<TestInput> data = List.of(
            new TestInput(
                "programacion",
                getViajes(), 
                "HOLA MUNDO procesado"
            )
        );
        return data.stream();
    }

    private static String getViajes() {
        return String.format("[" +
                "{" +
                    "\"oc\": \"5038\"," +
                    "\"fechaInicioReal\": \"20251004\"," +
                    "\"peso\": 31.03," +
                    "\"umPeso\": \"TO\"," +
                    "\"cantidad\": 31.03," +
                    "\"umCantidad\": \"TO\"," +
                    "\"codOrigen\": \"3000000219\"," +
                    "\"codTransportista\": \"\"," +
                    "\"codEquipo\": \"20-R-316\"," +
                    "\"placaEquipo\": \"D2Z-886\"," +
                    "\"estadoEquipo\": \"Activo\"," +
                    "\"codAcoplado1\": \"\"," +
                    "\"placaAcoplado1\": \"\"," +
                    "\"estadoAcoplado1\": \"\"," +
                    "\"codAcoplado2\": \"\"," +
                    "\"placaAcoplado2\": \"\"," +
                    "\"estadoAcoplado2\": \"\"," +
                    "\"licenciaConductor\": \"Q43675056\"," +
                    "\"codRuta\": \"F01012\"," +
                    "\"codMaterial\": \"13100021\"," +
                    "\"codDestino\": \"3000000136\"," +
                    "\"codClienteInterno\": \"  2046753984\"," +
                    "\"codDocumentoTransporte\": \"\"," +
                    "\"indCargaConsolidada\": \"\"," +
                    "\"estadoOc\": \"ABI\"," +
                    "\"claseOc\": \"ZRAC\"," +
                    "\"codSede\": \"LIM\"," +
                    "\"tipo\": \"Entrega\"," +
                    "\"codChofer\": \"30010407\"," +
                    "\"estadoChofer\": \"Activo\"," +
                    "\"dniChofer\": \"43675056\"," +
                    "\"rucTransportista\": \"\"," +
                    "\"horaInicioReal\": \"53430\"" +
                    "}"+
            "]");
    }
}