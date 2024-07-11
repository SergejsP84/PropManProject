package lv.emendatus.Destiny_PropMan.annotation.manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.responses.ValidationErrorResponse;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Adds a new manager",
        description = "Adds a new Manager entity to the system." +
                "Not intended to be available directly; Managers can add themselves via registration, or be added by an Admin.",
        tags = {"OTHER"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = Manager.class),
                        examples = @ExampleObject(
                                name = "Example Manager",
                                value = "{\n" +
                                        "  \"type\": \"PRIVATE\",\n" +
                                        "  \"managerName\": \"John Smith\",\n" +
                                        "  \"description\": \"Manager of luxury properties\",\n" +
                                        "  \"isActive\": true,\n" +
                                        "  \"joinDate\": \"2024-01-01T00:00:00\",\n" +
                                        "  \"login\": \"johnsmith\",\n" +
                                        "  \"password\": \"P@ssw0rd\",\n" +
                                        "  \"phone\": \"1234567890\",\n" +
                                        "  \"email\": \"john.smith@example.com\",\n" +
                                        "  \"iban\": \"DE89370400440532013000\",\n" +
                                        "  \"paymentCardNo\": \"1234567812345678\",\n" +
                                        "  \"cardValidityDate\": \"2024-12\",\n" +
                                        "  \"cvv\": \"123\",\n" +
                                        "  \"confirmationToken\": \"\",\n" +
                                        "  \"expirationTime\": \"2024-12-31T23:59:59\",\n" +
                                        "  \"authorities\": [\n" +
                                        "    { \"authority\": \"ROLE_MANAGER\" }\n" +
                                        "  ],\n" +
                                        "  \"knownIps\": [\n" +
                                        "    \"192.168.1.1\",\n" +
                                        "    \"192.168.1.2\"\n" +
                                        "  ]\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Manager added successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Manager.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Validation error: One or more fields are invalid",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ValidationErrorResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Endpoint should not be exposed as per the current setup")
        }
)

public @interface Manager_Add {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

