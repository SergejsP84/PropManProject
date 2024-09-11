package lv.emendatus.Destiny_PropMan.annotation.confirmation_controller;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Operation(
        summary = "Confirm Tenant Email Change",
        description = "Confirms the email change for a Tenant. The Tenant must click the confirmation link sent to their new email address. If the token is valid, the email will be updated.",
        tags = {"TENANT_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "token",
                        description = "The unique confirmation token sent to the Tenant's new email address",
                        required = true,
                        schema = @Schema(type = "string"),
                        example = "abc123token"
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Email change confirmation page loaded successfully.",
                        content = @Content(mediaType = "text/html")
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid or expired token. Email change could not be confirmed.",
                        content = @Content(mediaType = "text/html")
                )
        }
)

public @interface ConfirmEmailChangeTenant {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
