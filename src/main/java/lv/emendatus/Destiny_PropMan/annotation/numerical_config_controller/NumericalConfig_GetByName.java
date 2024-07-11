package lv.emendatus.Destiny_PropMan.annotation.numerical_config_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.GET)

@Operation(
        summary = "Get numerical configuration by name",
        description = "NOT directly accessible in the current setup, but retained for possible future use." +
                "Retrieve a numerical configuration by searching for its name.",
        tags = {"OTHER"},
        parameters = @Parameter(
                name = "name",
                description = "The name of the numerical configuration",
                example = "MaxAllowedConnections"
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Numerical configuration matching the name",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        name = "Example NumericalConfig",
                                        value = "{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"name\": \"ClaimPeriodInDays\",\n" +
                                                "  \"value\": 7.0,\n" +
                                                "  \"type\": \"SYSTEM_SETTING\",\n" +
                                                "  }\n" +
                                                "}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Numerical configuration not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Numerical configuration not found\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "415",
                        description = "Unsupported Media Type: The server cannot process the request because the payload is in an unsupported format",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unsupported Media Type\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving the numerical configuration\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)

public @interface NumericalConfig_GetByName {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
