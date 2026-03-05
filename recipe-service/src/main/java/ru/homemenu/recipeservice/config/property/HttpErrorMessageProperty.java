package ru.homemenu.recipeservice.config.property;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@ConfigurationProperties("app.http-error-message")
public record HttpErrorMessageProperty(

        Map<String, Map<String, List<String>>> constraints,

        @NotBlank
        String unknownConstraintMessage
) {

    public HttpErrorMessageProperty {
        constraints = constraints != null ? constraints : new HashMap<>();
    }

}
