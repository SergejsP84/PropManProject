package lv.emendatus.Destiny_PropMan.annotation.confirmation_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
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
        summary = "Confirm Registration",
        description = "Confirms the registration of a tenant or manager using a confirmation token.",
        tags = {"ACCESS AND ACCOUNT MANAGEMENT"},
        parameters = {
                @Parameter(
                        name = "token",
                        description = "The confirmation token received via email",
                        required = true,
                        example = "123456abcdef"
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Account has been successfully activated",
                        content = @Content(mediaType = "text/html")
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid or expired token",
                        content = @Content(
                                mediaType = "text/html",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "<html><body><h1>Invalid or expired token. Please try again.</h1></body></html>")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Token not found",
                        content = @Content(
                                mediaType = "text/html",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "<html><body><h1>Token not found. Please try again.</h1></body></html>")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "text/html",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "<html><body><h1>An unexpected error occurred while processing the confirmation.</h1></body></html>")
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to Managers and Tenants undergoing the registration process")
)
public @interface ConfirmRegistration {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

