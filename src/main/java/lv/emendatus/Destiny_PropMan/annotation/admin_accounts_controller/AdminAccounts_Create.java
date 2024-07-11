package lv.emendatus.Destiny_PropMan.annotation.admin_accounts_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.registration.AdminRegistrationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Admin;
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
        summary = "Creates another Admin",
        description = "Checks whether any of the user entities in the database (Tenants, Managers, and Admins) already has the specified login. If not, it creates another Admin user entity and saves it in the database." +
        "This function is only available to the DefaultAdmin.",
        tags = {"ACCESS AND ACCOUNT MANAGEMENT"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "The currency being added",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AdminRegistrationDTO.class),
                        examples = {
                                @ExampleObject(name = "Added Admin user entity",
                                        value = """
                                                {
                                                                "name": "Kenny McCormick",
                                                                "login": "immortal",
                                                                "password": "Cheat1ngD3ath",
                                                                "email": "kenny@spark.com"
                                                }"""
                                )
                        }
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "A new Admin user has been added",
                        content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Admin.class)
                )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = {
                                        @ExampleObject(
                                                name = "Login Exists",
                                                value = "{\"message\": \"This login already exists, please select a different one.\"}"
                                        ),
                                        @ExampleObject(
                                                name = "Password Validation Error",
                                                value = "{\"password\": \"Password must be at least 8 characters long\"}"
                                        ),
                                        @ExampleObject(
                                                name = "Password Pattern Error",
                                                value = "{\"password\": \"Password contain at least one uppercase letter, at least one lowercase letter, and at least one digit\"}"
                                        )}
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized: Authentication is required and has failed or has not yet been provided; alternatively, the endpoint might not be accessible to this user or the entire respective user role.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "415",
                        description = "Unsupported Media Type: The request payload format is not supported",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unsupported Media Type\"}")
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
                @SecurityRequirement(name = "Adding other Admins is the sole privilege of the DefaultAdmin, which, in turn, is created at the first launch of the project")
        }
)
public @interface AdminAccounts_Create {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}


