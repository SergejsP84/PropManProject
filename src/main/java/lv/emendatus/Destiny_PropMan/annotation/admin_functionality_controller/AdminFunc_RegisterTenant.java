package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.TenantRegistrationDTO;
import org.springframework.core.annotation.AliasFor;
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
        summary = "Register a new tenant by admin",
        description = "Allows an admin to register a new tenant. Checks for unique login and email, validates payment card number, and encrypts sensitive information.",
        tags = {"ADMIN_FUNCTION"},
        requestBody = @RequestBody(
                description = "The tenant registration details",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = TenantRegistrationDTO.class),
                        examples = @ExampleObject(
                                name = "Register Tenant",
                                value = """
                                        {
                                            "firstName": "John",
                                            "lastName": "Doe",
                                            "phone": "1234567890",
                                            "email": "john.doe@example.com",
                                            "iban": "DE89370400440532013000",
                                            "login": "johndoe",
                                            "password": "SecurePass123",
                                            "reEnterPassword": "SecurePass123",
                                            "paymentCardNo": "4908474399435405",
                                            "cardValidityDate": "2025-12",
                                            "cvv": "123"
                                        }
                                        """
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Tenant registered successfully by admin.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request: Invalid input data or business logic violation.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = {
                                        @ExampleObject(
                                                name = "Invalid Payment Card Number",
                                                value = "{\"message\": \"Invalid payment card number\"}"
                                        ),
                                        @ExampleObject(
                                                name = "Passwords Do Not Match",
                                                value = "{\"message\": \"Passwords do not match\"}"
                                        ),
                                        @ExampleObject(
                                                name = "Login Already Exists",
                                                value = "{\"message\": \"Login already exists\"}"
                                        ),
                                        @ExampleObject(
                                                name = "Email Already Exists",
                                                value = "{\"message\": \"Tenant with this e-mail exists\"}"
                                        )
                                }
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An error occurred while registering the tenant.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"An error occurred while registering the tenant.\"}")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Admins only")
        }
)
public @interface AdminFunc_RegisterTenant {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
