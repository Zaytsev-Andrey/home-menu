package ru.homemenu.logging.unit.config.property;

import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;
import ru.homemenu.logging.config.property.ServiceLoggingProperty;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceLoggingPropertyTest {

    @Test
    void compactConstructor_whenLevelIsNull_thenDefaultsToInfo() {
        ServiceLoggingProperty property = new ServiceLoggingProperty(true, true, null, null);

        assertThat(property.level()).isEqualTo(Level.INFO);
        assertThat(property.errorLevel()).isEqualTo(Level.WARN);
    }

    @Test
    void compactConstructor_whenLevelsProvided_thenKeepsProvidedValues() {
        ServiceLoggingProperty property = new ServiceLoggingProperty(true, true, Level.DEBUG, Level.ERROR);

        assertThat(property.level()).isEqualTo(Level.DEBUG);
        assertThat(property.errorLevel()).isEqualTo(Level.ERROR);
    }

    @Test
    void compactConstructor_whenEnableFalse_thenAlwaysOverridesToTrue() {
        ServiceLoggingProperty property = new ServiceLoggingProperty(false, false, Level.INFO, Level.WARN);

        assertThat(property.enable()).isTrue();
    }

    @Test
    void enableError_whenFalse_thenIsRespected() {
        ServiceLoggingProperty property = new ServiceLoggingProperty(true, false, Level.INFO, Level.WARN);

        assertThat(property.enableError()).isFalse();
    }
}