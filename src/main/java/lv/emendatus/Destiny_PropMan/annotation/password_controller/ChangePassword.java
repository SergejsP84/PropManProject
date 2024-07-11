package lv.emendatus.Destiny_PropMan.annotation.password_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.authentication.PasswordChangeDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.CardUpdateDTO;
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
        summary = "Change Password",
        description = "Allows changing a Tenant's or a Manager's password.",
        tags = {"ACCESS AND ACCOUNT MANAGEMENT"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = PasswordChangeDTO.class),
                        examples = @ExampleObject(
                                name = "Example Password Change",
                                value = "{\n" +
                                        "  \"login\": \"user@example.com\",\n" +
                                        "  \"userType\": \"TENANT\",\n" +
                                        "  \"newPassword\": \"NewPass123\",\n" +
                                        "  \"reEnterNewPassword\": \"NewPass123\"\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Password changed successfully"
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Failed to change password",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"Failed to change password: Invalid password format\"\n" +
                                                "}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "User entity not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"User entoty not found\"\n" +
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
                                                "  \"error\": \"An unexpected error occurred while processing the request\"\n" +
                                                "}"
                                )
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to authorized tenants and managers")
)
public @interface ChangePassword {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
