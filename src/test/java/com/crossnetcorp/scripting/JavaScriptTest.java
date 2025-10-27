package com.crossnetcorp.scripting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crossnetcorp.GeneralIntegrationFlow;
import com.crossnetcorp.transformation.TestInput;

/**
 * Test class for the Velocity flow.
 */
@ExtendWith(MockitoExtension.class)
public class JavaScriptTest {
    private static final Logger logger = LogManager.getLogger(JavaScriptTest.class);

    /**
     * Tests the Velocity transformation in the integration flow.
     *
     * @param data The test input data.
     */
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test_velocity_inflow(TestInput data) {
        GeneralIntegrationFlow flow = new GeneralIntegrationFlow(data.flow, null);
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
                "src\\test\\resources\\scripting\\example3_javascript",
                "hola mundo", 
                "HOLA MUNDO procesado"
            )
        );
        return data.stream();
    }
}