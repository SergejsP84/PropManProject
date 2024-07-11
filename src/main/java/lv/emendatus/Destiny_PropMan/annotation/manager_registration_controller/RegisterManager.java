package lv.emendatus.Destiny_PropMan.annotation.manager_registration_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
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
        summary = "Register Manager",
        description = "Registers a new manager with the provided details.",
        tags = {"ACCESS AND ACCOUNT MANAGEMENT"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = ManagerRegistrationDTO.class),
                        examples = @ExampleObject(
                                name = "Example Manager Registration",
                                value = "{\n" +
                                        "  \"type\": \"GENERAL\",\n" +
                                        "  \"managerName\": \"Jane Smith\",\n" +
                                        "  \"description\": \"Manager for commercial properties\",\n" +
                                        "  \"phone\": \"+1234567890\",\n" +
                                        "  \"email\": \"jane.smith@example.com\",\n" +
                                        "  \"iban\": \"DE89370400440532013000\",\n" +
                                        "  \"paymentCardNo\": \"1234567812345678\",\n" +
                                        "  \"cardValidityDate\": \"2025-05\",\n" +
                                        "  \"cvv\": \"123\",\n" +
                                        "  \"login\": \"janesmith\",\n" +
                                        "  \"password\": \"SecurePass123\",\n" +
                                        "  \"reEnterPassword\": \"SecurePass123\"\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Manager registered successfully",
                        content = @Content(
                                mediaType = "application/json"
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid registration details",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"Invalid registration details\"\n" +
                                                "}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "409",
                        description = "Conflict - email or login already exists",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"Email or login already exists\"\n" +
                                                "}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"An unexpected error occurred while processing the registration\"\n" +
                                                "}"
                                )
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to unauthorized users")
)
public @interface RegisterManager {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
