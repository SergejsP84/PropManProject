package lv.emendatus.Destiny_PropMan.annotation.authentication_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.LoginDTO;
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
        summary = "Login",
        description = "Authenticates a user (either a tenant or a manager) and returns the user details",
        tags = {"ACCESS AND ACCOUNT MANAGEMENT"},
        requestBody = @RequestBody(
                description = "Login credentials",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = LoginDTO.class),
                        examples = @ExampleObject(
                                name = "Login",
                                value = """
                                        {
                                            "login": "user_login",
                                            "password": "user_password",
                                            "userType": "TENANT"
                                        }
                                        """
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User authenticated successfully",
                        content = @Content(mediaType = "application/json")
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request: Invalid login credentials",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Invalid login credentials\"}")
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while processing the login\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "Generally accessible")
)
public @interface Authentication_Login {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

