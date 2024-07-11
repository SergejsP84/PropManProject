package lv.emendatus.Destiny_PropMan.annotation.authentication_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.ManagerRegistrationDTO;
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
        description = "Registers a new manager",
        tags = {"ACCESS AND ACCOUNT MANAGEMENT"},
        requestBody = @RequestBody(
                description = "Manager registration information",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ManagerRegistrationDTO.class),
                        examples = @ExampleObject(
                                name = "Manager Registration",
                                value = """
                                        {
                                            "type": "PRIVATE",
                                            "managerName": "John Doe",
                                            "description": "Property manager",
                                            "phone": "123456789",
                                            "email": "john.doe@example.com",
                                            "iban": "IBAN123456789",
                                            "paymentCardNo": "4908474399435405",
                                            "cardValidityDate": "2025-12",
                                            "cvv": "123",
                                            "login": "john_doe",
                                            "password": "Password123",
                                            "reEnterPassword": "Password123"
                                        }
                                        """
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Manager registered successfully",
                        content = @Content(mediaType = "application/json")
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request: Invalid registration information",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Invalid registration information\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized: Authentication credentials are missing or invalid",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while processing the registration\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
public @interface Authentication_RegisterManager {
}
