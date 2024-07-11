package lv.emendatus.Destiny_PropMan.annotation.manager_controller;

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
@RequestMapping(method = RequestMethod.PUT)

@Operation(
        summary = "Update Manager",
        description = "Intended for use in more complex functions for updating Manager details" +
                "Update the details of an existing manager by ID.",
        tags = {"OTHER"},
        parameters = @Parameter(
                name = "id",
                description = "The ID of the manager to update",
                example = "1",
                required = true
        ),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "The updated manager object",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                name = "Example Manager",
                                value = "{\n" +
                                        "  \"type\": \"PRIVATE\",\n" +
                                        "  \"managerName\": \"Jane Doe\",\n" +
                                        "  \"description\": \"Another manager\",\n" +
                                        "  \"isActive\": true,\n" +
                                        "  \"joinDate\": \"2023-01-01T00:00:00\",\n" +
                                        "  \"login\": \"jdoe\",\n" +
                                        "  \"password\": \"Password1\",\n" +
                                        "  \"phone\": \"555-5678\",\n" +
                                        "  \"email\": \"jane.doe@example.com\",\n" +
                                        "  \"iban\": \"DE89370400440532013000\",\n" +
                                        "  \"paymentCardNo\": \"9876543210987654\",\n" +
                                        "  \"cardValidityDate\": \"2025-12\",\n" +
                                        "  \"cvv\": \"456\",\n" +
                                        "  \"confirmationToken\": \"def456\",\n" +
                                        "  \"expirationTime\": \"2024-01-01T00:00:00\",\n" +
                                        "  \"authorities\": [{\n" +
                                        "    \"authority\": \"ROLE_MANAGER\"\n" +
                                        "  }],\n" +
                                        "  \"knownIps\": [\"192.168.1.2\"]\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Manager successfully updated",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Manager not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"No manager found with ID: 1\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request: Invalid input data",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Invalid input data\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while updating the manager\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)

public @interface Manager_Update {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
