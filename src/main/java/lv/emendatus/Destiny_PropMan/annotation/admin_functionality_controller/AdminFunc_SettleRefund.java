package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Refund;
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
        summary = "Settle Refund",
        description = "Processes and settles a refund for a tenant. This endpoint is accessible only by admins.",
        tags = {"ADMIN_FUNCTION"},
        requestBody = @RequestBody(
                description = "Refund details",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Refund.class)
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Refund settled successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Refund settled successfully.")
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Failed to settle refund.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Failed to settle refund.")
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized: Authentication is required and has failed or has not yet been provided; alternatively, the endpoint might not be accessible to this user or the entire respective user role.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Unauthorized")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while settling refund: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Admins only")
        }
)
public @interface AdminFunc_SettleRefund {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
