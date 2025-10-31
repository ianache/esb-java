package com.crossnetcorp.processors.whiteblacklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crossnetcorp.utils.CIDRChecker;

import java.util.List;

import java.util.Arrays;
import java.util.stream.Stream;
import java.net.UnknownHostException;

@ExtendWith(MockitoExtension.class)
public class CIDRCheckTest {
    private static final Logger logger = LogManager.getLogger(CIDRCheckTest.class);

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void test_valid_cidr(CIDRDataItem data) {
        CIDRChecker checker = new CIDRChecker(data.getCidrs());
        try {
            Boolean result = checker.isIPInList(data.getIp());
            assertEquals(data.getExpected(), result);
        } catch(UnknownHostException e) {
            logger.error(e.getMessage());
        }
    }

    static Stream<CIDRDataItem> provideTestData() {
        List<CIDRDataItem> data = List.of(
            CIDRDataItem.builder()
                .cidrs(List.of("127.0.0.1/32"))
                .ip("127.0.0.1")
                .expected(true)
                .build(),
            CIDRDataItem.builder()
                .cidrs(List.of("192.168.0.1/16"))
                .ip("127.0.0.1")
                .expected(false)
                .build()
        );

        return data.stream();
    }
}
