package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Payout;
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
        summary = "Settle Payout",
        description = "Processes and settles a payout. This endpoint is accessible only by admins.",
        tags = {"ADMIN_FUNCTION"},
        requestBody = @RequestBody(
                description = "Payout details",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Payout.class),
                        examples = @ExampleObject(value = "{\"id\": 1, \"managerId\": 2, \"bookingId\": 3, \"amount\": 100.0, \"createdAt\": \"2023-05-01T12:00:00\", \"currency\": {\"id\": 1, \"designation\": \"USD\"}}")
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Payout settled successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Payout settled successfully.")
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Failed to settle payout.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Failed to settle payout.")
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
                                examples = @ExampleObject(value = "An error occurred while settling payout: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Admins only")
        }
)
public @interface AdminFunc_SettlePayout {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

