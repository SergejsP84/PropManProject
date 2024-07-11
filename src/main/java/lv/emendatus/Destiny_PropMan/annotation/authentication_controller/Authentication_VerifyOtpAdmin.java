package lv.emendatus.Destiny_PropMan.annotation.authentication_controller;

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
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Verify OTP for Admin",
        description = "Verifies the One-Time Password (OTP) for an admin and logs the admin in if the OTP is valid.",
        tags = {"ACCESS AND ACCOUNT MANAGEMENT"},
        parameters = {
                @Parameter(
                        name = "otp",
                        description = "The OTP submitted by the admin",
                        required = true,
                        example = "123456"
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "OTP verification successful. Admin logged in.",
                        content = @Content(mediaType = "application/json")
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid OTP. Please try again.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Invalid OTP. Please try again.\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while processing the OTP verification\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to Admins")
)
public @interface Authentication_VerifyOtpAdmin {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}