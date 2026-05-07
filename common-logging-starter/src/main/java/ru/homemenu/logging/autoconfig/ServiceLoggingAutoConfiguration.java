package ru.homemenu.logging.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.homemenu.logging.config.property.ServiceLoggingProperty;
import ru.homemenu.logging.service.aspect.ServiceLoggingAspect;

@AutoConfiguration
@ConditionalOnProperty(prefix = "app.logging.service", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ServiceLoggingProperty.class)
public class ServiceLoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ServiceLoggingAspect serviceLoggingAspect(ServiceLoggingProperty serviceLoggingProperty) {
        return new ServiceLoggingAspect(serviceLoggingProperty);
    }
}
