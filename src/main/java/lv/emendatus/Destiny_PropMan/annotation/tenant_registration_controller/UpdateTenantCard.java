package lv.emendatus.Destiny_PropMan.annotation.tenant_registration_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
        summary = "Update Tenant Payment Card",
        description = "Updates the payment card details for a tenant.",
        tags = {"ACCESS AND ACCOUNT MANAGEMENT"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = CardUpdateDTO.class),
                        examples = @ExampleObject(
                                name = "Example Card Update",
                                value = "{\n" +
                                        "  \"userId\": 1,\n" +
                                        "  \"userType\": \"TENANT\",\n" +
                                        "  \"newCardNumber\": \"9876543210987654\",\n" +
                                        "  \"newValidityDate\": \"2025-05\",\n" +
                                        "  \"newCvv\": \"456\"\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Payment card updated successfully",
                        content = @Content(
                                mediaType = "application/json"
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid card update details",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"Invalid card update details\"\n" +
                                                "}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Tenant not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"Tenant not found\"\n" +
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
        security = @SecurityRequirement(name = "Available to authorized tenants")
)
public @interface UpdateTenantCard {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
