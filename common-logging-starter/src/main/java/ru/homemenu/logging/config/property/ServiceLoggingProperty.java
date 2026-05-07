package ru.homemenu.logging.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.slf4j.event.Level;

@ConfigurationProperties("app.logging.service")
public record ServiceLoggingProperty(
        boolean enable,
        boolean enableError,
        Level level,
        Level errorLevel
) {
    public ServiceLoggingProperty {
        enable = true;
        level = level != null ? level : Level.INFO;
        errorLevel = errorLevel != null ? errorLevel : Level.WARN;
    }
}
