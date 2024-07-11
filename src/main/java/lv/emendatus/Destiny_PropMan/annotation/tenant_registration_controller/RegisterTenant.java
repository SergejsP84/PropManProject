package lv.emendatus.Destiny_PropMan.annotation.tenant_registration_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
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
        summary = "Register Tenant",
        description = "Registers a new tenant with the provided details.",
        tags = {"ACCESS AND ACCOUNT MANAGEMENT"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = TenantRegistrationDTO.class),
                        examples = @ExampleObject(
                                name = "Example Tenant Registration",
                                value = "{\n" +
                                        "  \"firstName\": \"John\",\n" +
                                        "  \"lastName\": \"Doe\",\n" +
                                        "  \"phone\": \"+123456789\",\n" +
                                        "  \"email\": \"john.doe@example.com\",\n" +
                                        "  \"iban\": \"DE89370400440532013000\",\n" +
                                        "  \"paymentCardNo\": \"1234567812345678\",\n" +
                                        "  \"cardValidityDate\": \"2024-12\",\n" +
                                        "  \"cvv\": \"123\",\n" +
                                        "  \"login\": \"johndoe\",\n" +
                                        "  \"password\": \"Password1\",\n" +
                                        "  \"reEnterPassword\": \"Password1\",\n" +
                                        "  \"preferredCurrency\": \"USD\"\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Tenant registered successfully",
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
public @interface RegisterTenant {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

