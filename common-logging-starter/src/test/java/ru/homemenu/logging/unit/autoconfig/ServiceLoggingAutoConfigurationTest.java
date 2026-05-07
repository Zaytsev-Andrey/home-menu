package ru.homemenu.logging.unit.autoconfig;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import ru.homemenu.logging.autoconfig.ServiceLoggingAutoConfiguration;
import ru.homemenu.logging.config.property.ServiceLoggingProperty;
import ru.homemenu.logging.service.aspect.ServiceLoggingAspect;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceLoggingAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ServiceLoggingAutoConfiguration.class));

    @Test
    void byDefault_thenAspectAndPropertyBeansArePresent() {
        runner.run(ctx -> {
            assertThat(ctx).hasSingleBean(ServiceLoggingAspect.class);
            assertThat(ctx).hasSingleBean(ServiceLoggingProperty.class);
        });
    }

    @Test
    void whenEnableTrueExplicitly_thenAspectIsRegistered() {
        runner
                .withPropertyValues("app.logging.service.enable=true")
                .run(ctx -> assertThat(ctx).hasSingleBean(ServiceLoggingAspect.class));
    }

    @Test
    void whenEnableFalse_thenAspectIsNotRegistered() {
        runner
                .withPropertyValues("app.logging.service.enable=false")
                .run(ctx -> assertThat(ctx).doesNotHaveBean(ServiceLoggingAspect.class));
    }

    @Test
    void whenUserDefinesAspect_thenUserBeanIsKept() {
        ServiceLoggingProperty userProperty = new ServiceLoggingProperty(true, true, null, null);
        ServiceLoggingAspect userAspect = new ServiceLoggingAspect(userProperty);

        runner
                .withBean(ServiceLoggingAspect.class, () -> userAspect)
                .run(ctx -> {
                    assertThat(ctx).hasSingleBean(ServiceLoggingAspect.class);
                    assertThat(ctx.getBean(ServiceLoggingAspect.class)).isSameAs(userAspect);
                });
    }
}