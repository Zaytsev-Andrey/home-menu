package ru.homemenu.logging.unit.service.aspect;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import ru.homemenu.logging.config.property.ServiceLoggingProperty;
import ru.homemenu.logging.service.aspect.ServiceLoggingAspect;
import ru.homemenu.logging.service.event.ServiceLogEvent;
import ru.homemenu.logging.structure.LogField;

import java.lang.reflect.Method;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceLoggingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature signature;

    private final SampleTarget target = new SampleTarget();

    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void attachAppender() {
        Logger logger = (Logger) LoggerFactory.getLogger(SampleTarget.class);
        logger.setLevel(ch.qos.logback.classic.Level.TRACE);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        ((Logger) LoggerFactory.getLogger(SampleTarget.class)).detachAppender(appender);
    }

    @Test
    void logServiceCall_whenSucceeds_thenLogsEntryAndExitWithExpectedFields() throws Throwable {
        ServiceLoggingProperty property = new ServiceLoggingProperty(true, true, Level.INFO, Level.WARN);
        ServiceLoggingAspect aspect = new ServiceLoggingAspect(property);
        Method method = SampleTarget.class.getDeclaredMethod("doWork", String.class);
        when(joinPoint.getTarget())
                .thenReturn(target);
        when(joinPoint.getSignature())
                .thenReturn(signature);
        when(joinPoint.getArgs())
                .thenReturn(new Object[]{"alice"});
        when(signature.getMethod())
                .thenReturn(method);
        when(signature.getParameterNames())
                .thenReturn(new String[]{"name"});
        when(joinPoint.proceed())
                .thenReturn("Hello, alice");

        Object result = aspect.logServiceCall(joinPoint);

        assertThat(result).isEqualTo("Hello, alice");
        assertThat(appender.list).hasSize(2);

        ILoggingEvent entry = appender.list.getFirst();
        assertThat(kv(entry, LogField.EVENT)).isEqualTo(ServiceLogEvent.ENTRY);
        assertThat(kv(entry, LogField.CLASS)).isEqualTo("SampleTarget");
        assertThat(kv(entry, LogField.METHOD)).isEqualTo("doWork");
        assertThat(kv(entry, LogField.ARGS)).asString().contains("name=alice");

        ILoggingEvent exit = appender.list.getLast();
        assertThat(kv(exit, LogField.EVENT)).isEqualTo(ServiceLogEvent.EXIT);
        assertThat(kv(exit, LogField.RESULT)).isEqualTo("Hello, alice");
        assertThat(((Number) Objects.requireNonNull(raw(exit, LogField.DURATION_MS))).longValue()).isGreaterThanOrEqualTo(0L);

        verify(joinPoint).proceed();
        verifyNoMoreInteractions(joinPoint);
    }

    @Test
    void logServiceCall_whenResultIsNull_thenLogsResultAsLiteralNull() throws Throwable {
        ServiceLoggingProperty property = new ServiceLoggingProperty(true, true, Level.INFO, Level.WARN);
        ServiceLoggingAspect aspect = new ServiceLoggingAspect(property);
        Method method = SampleTarget.class.getDeclaredMethod("voidWork");
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(signature.getMethod()).thenReturn(method);
        when(signature.getParameterNames()).thenReturn(new String[0]);
        when(joinPoint.proceed()).thenReturn(null);

        aspect.logServiceCall(joinPoint);

        ILoggingEvent exit = appender.list.getLast();
        assertThat(kv(exit, LogField.RESULT)).isEqualTo("null");
    }

    @Test
    void logServiceCall_whenThrowsAndEnableErrorTrue_thenLogsErrorAndRethrows() throws Throwable {
        ServiceLoggingProperty property = new ServiceLoggingProperty(true, true, Level.INFO, Level.WARN);
        ServiceLoggingAspect aspect = new ServiceLoggingAspect(property);
        Method method = SampleTarget.class.getDeclaredMethod("doWork", String.class);
        IllegalStateException boom = new IllegalStateException("kaboom");
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"bob"});
        when(signature.getMethod()).thenReturn(method);
        when(signature.getParameterNames()).thenReturn(new String[]{"name"});
        when(joinPoint.proceed()).thenThrow(boom);

        assertThatThrownBy(() -> aspect.logServiceCall(joinPoint))
                .isSameAs(boom);

        assertThat(appender.list).hasSize(2);
        ILoggingEvent error = appender.list.getLast();
        assertThat(kv(error, LogField.EVENT)).isEqualTo(ServiceLogEvent.ERROR);
        assertThat(kv(error, LogField.ERROR_CLASS)).isEqualTo(IllegalStateException.class.getName());
        assertThat(error.getThrowableProxy()).isNotNull();
    }

    @Test
    void logServiceCall_whenThrowsAndEnableErrorFalse_thenSuppressesErrorLogButStillRethrows() throws Throwable {
        ServiceLoggingProperty property = new ServiceLoggingProperty(true, false, Level.INFO, Level.WARN);
        ServiceLoggingAspect aspect = new ServiceLoggingAspect(property);
        Method method = SampleTarget.class.getDeclaredMethod("doWork", String.class);
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"bob"});
        when(signature.getMethod()).thenReturn(method);
        when(signature.getParameterNames()).thenReturn(new String[]{"name"});
        when(joinPoint.proceed()).thenThrow(new IllegalStateException("kaboom"));

        assertThatThrownBy(() -> aspect.logServiceCall(joinPoint))
                .isInstanceOf(IllegalStateException.class);

        assertThat(appender.list).hasSize(1);
        assertThat(kv(appender.list.getFirst(), LogField.EVENT)).isEqualTo(ServiceLogEvent.ENTRY);
    }

    @Test
    void logServiceCall_whenParameterNamesUnavailable_thenFallsBackToArgIndex() throws Throwable {
        ServiceLoggingProperty property = new ServiceLoggingProperty(true, true, Level.INFO, Level.WARN);
        ServiceLoggingAspect aspect = new ServiceLoggingAspect(property);
        Method method = SampleTarget.class.getDeclaredMethod("doWork", String.class);
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"x"});
        when(signature.getMethod()).thenReturn(method);
        when(signature.getParameterNames()).thenReturn(null);
        when(joinPoint.proceed()).thenReturn("ok");

        aspect.logServiceCall(joinPoint);

        ILoggingEvent entry = appender.list.getFirst();
        assertThat(kv(entry, LogField.ARGS)).asString().contains("arg_0=x");
    }

    private static Object raw(ILoggingEvent event, String key) {
        return event.getKeyValuePairs() == null ? null : event.getKeyValuePairs().stream()
                .filter(p -> p.key.equals(key))
                .map(p -> p.value)
                .findFirst()
                .orElse(null);
    }

    private static String kv(ILoggingEvent event, String key) {
        Object v = raw(event, key);
        return v == null ? null : String.valueOf(v);
    }

    static class SampleTarget {

        String doWork(String name) {
            return "Hello, " + name;
        }

        void voidWork() {}

    }
}