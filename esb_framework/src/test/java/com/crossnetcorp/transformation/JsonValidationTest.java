package com.crossnetcorp.transformation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

/**
 * Test class for JSON validation in the integration flow.
 */
@ExtendWith(MockitoExtension.class)
public class JsonValidationTest {
    private static final Logger logger = LogManager.getLogger(JsonValidationTest.class);

    static String CONFIG_FILE="transformaciones\\example2";

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
     * Tests the document validation within the integration flow.
     *
     * @param data The test input data.
     */
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test_doc_validation_inflow_valid(TestInput data) {
        IIntegrationFlow<Object,Object> flow = manager.getFlow(data.flow);

        Object result = flow.handle(
            data.request,
            (message)->{
                message.setPayload(message.getPayload().toString());
                logger.info(message.getPayload().toString());
                return message;
            }
        );
        assertEquals(data.response.trim(), result.toString().trim());
    }

    /**
     * Provides the test data for the parameterized test.
     *
     * @return A stream of test input data.
     */
    static Stream<TestInput> provideTestData() {
        List<TestInput> data = List.of(
            new TestInput(
                "default",
                "{\"code\": \"CUS001\", \"edad\": 20}", 
                "{\"code\": \"CUS001\", \"edad\": 20}"
            )
        );
        return data.stream();
    }
}