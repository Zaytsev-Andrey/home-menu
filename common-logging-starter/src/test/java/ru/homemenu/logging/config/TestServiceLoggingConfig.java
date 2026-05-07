package ru.homemenu.logging.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.homemenu.logging.sample.SampleService;

@TestConfiguration
@EnableAspectJAutoProxy
public class TestServiceLoggingConfig {

    @Bean
    public SampleService sampleService() {
        return new SampleService();
    }
}
