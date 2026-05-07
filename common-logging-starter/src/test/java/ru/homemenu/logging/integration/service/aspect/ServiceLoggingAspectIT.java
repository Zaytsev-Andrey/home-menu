package ru.homemenu.logging.integration.service.aspect;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.homemenu.logging.sample.SampleService;
import ru.homemenu.logging.service.event.ServiceLogEvent;
import ru.homemenu.logging.structure.LogField;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceLoggingAspectIT extends IntegrationTestBase {

    @Autowired
    private SampleService sampleService;

    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void attachAppender() {
        Logger logger = (Logger) LoggerFactory.getLogger(SampleService.class);
        logger.setLevel(Level.TRACE);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        ((Logger) LoggerFactory.getLogger(SampleService.class)).detachAppender(appender);
    }

    @Test
    void greet_whenInvoked_thenLogsEntryAndExit() {
        sampleService.greet("carol");

        assertThat(appender.list).hasSize(2);
        assertThat(appender.list.getFirst().getKeyValuePairs())
                .anyMatch(p -> LogField.EVENT.equals(p.key) && ServiceLogEvent.ENTRY.equals(p.value));
        assertThat(appender.list.getLast().getKeyValuePairs())
                .anyMatch(p -> LogField.EVENT.equals(p.key) && ServiceLogEvent.EXIT.equals(p.value));
    }

    @Test
    void boom_whenInvoked_thenLogsErrorAndRethrows() {
        assertThatThrownBy(() -> sampleService.boom())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("kaboom");

        assertThat(appender.list).hasSize(2);
        assertThat(appender.list.getLast().getKeyValuePairs())
                .anyMatch(p -> LogField.EVENT.equals(p.key) && ServiceLogEvent.ERROR.equals(p.value));
    }

}