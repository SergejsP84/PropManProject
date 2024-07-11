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
        summary = "Get numerical configurations by currency ID",
        description = "NOT directly accessible in the current setup, but retained for possible future use." +
                "Retrieve a list of numerical configurations that are associated with a specific currency.",
        tags = {"OTHER"},
        parameters = @Parameter(
                name = "cur_id",
                description = "The ID of the currency",
                example = "1"
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of numerical configurations associated with the currency",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        name = "Example NumericalConfig List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"name\": \"ClaimPeriodInDays\",\n" +
                                                "  \"value\": 7.0,\n" +
                                                "  \"type\": \"SYSTEM_SETTING\",\n" +
                                                "  }\n" +
                                                "}]"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Currency or numerical configurations not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Currency not found or no configurations associated with the given currency\"}")
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving the numerical configurations\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)

public @interface NumericalConfig_GetByCurrency {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

