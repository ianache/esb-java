package com.crossnetcorp.scripting;

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
import com.crossnetcorp.integrationflow.IIntegrationFlow;
import com.crossnetcorp.transformation.TestInput;

/**
 * Test class for the Python Scripting flow.
 */
@ExtendWith(MockitoExtension.class)
public class PythonTest {
    private static final Logger logger = LogManager.getLogger(JavaScriptTest.class);

    static IntegrationFlowManager<Object> manager = 
        new IntegrationFlowManager<>(GeneralIntegrationFlow.class);

    static String CONFIG_FILE="scripting\\example3_python";

    @BeforeAll
    public static void setUp() {
       manager.loadConfigurationFromFile(CONFIG_FILE);
    }

    /**
     * Tests the Python transformation in the integration flow.
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
                message.setPayload(message.getPayload().toString());
                logger.info(message.getPayload().toString());
                return message;
            }
        );
        assertEquals(data.response, result.toString());
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
                "hola mundo", 
                "HOLA MUNDO procesado"
            ),
            new TestInput(
                "respuestaJson",
                "hola mundo", 
                "{\"respuesta\": \"HOLA MUNDO procesado\"}"
            )
        );
        return data.stream();
    }
}