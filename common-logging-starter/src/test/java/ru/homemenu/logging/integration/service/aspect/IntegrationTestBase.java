package ru.homemenu.logging.integration.service.aspect;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.homemenu.logging.autoconfig.ServiceLoggingAutoConfiguration;
import ru.homemenu.logging.config.TestServiceLoggingConfig;

@ActiveProfiles("test")
@SpringBootTest(classes = {
        ServiceLoggingAutoConfiguration.class,
        TestServiceLoggingConfig.class
})
public class IntegrationTestBase {
}
