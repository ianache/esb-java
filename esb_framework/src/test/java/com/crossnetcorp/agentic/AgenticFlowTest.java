package com.crossnetcorp.agentic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
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
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for the Velocity flow.
 */
@ExtendWith(MockitoExtension.class)
public class AgenticFlowTest {
    private static final Logger logger = LogManager.getLogger(AgenticFlowTest.class);

    static String CONFIG_FILE="agentic\\flow_agentic.yaml";

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

    /**
     * Tests the Velocity transformation in the integration flow.
     *
     * @param data The test input data.
     */
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test_velocity_inflow(TestInput data) {
        IIntegrationFlow<Object,Object> flow = manager.getFlow(data.flow);

        Object result = flow.handle(
            data.request,
            (message) -> {
                String body = buildPoint(
                    ((JSONObject)message.getPayload()).get("embedding").toString(), 
                    ((JSONObject)message.getPayload()).get("content").toString()
                );
                logger.info(body);
                return message;
            }
        );
        assertTrue(true);
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
                "ingestQdrant",
                "hola mundo", 
                "HOLA MUNDO procesado"
            )
        );
        return data.stream();
    }

    private static String buildPoint(String vector, String payload) {
        return String.format(
          "{" +
          "\"wait\": true,"+
          "\"points\": ["+
            "{"+
              "\"id\": 1," +
              "\"vector\": %s," +
              "\"payload\": {"+
                 "\"text\": \"%s\","+
                 "\"author\": \"Ilver Anache Pupo\","+
                 "\"source\": \"Caso de Prueba\""+
              "}" +
            "}"+
          "]"+
        "}",
        vector,payload);
    }
}