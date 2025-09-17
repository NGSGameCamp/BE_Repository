package com.imfine.ngs;

import com.imfine.ngs.payment.client.PortOneClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class NgsApplicationTests {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PortOneClient portOneClient() {
            return Mockito.mock(PortOneClient.class);
        }
    }

    @Test
    void contextLoads() {
    }

}
