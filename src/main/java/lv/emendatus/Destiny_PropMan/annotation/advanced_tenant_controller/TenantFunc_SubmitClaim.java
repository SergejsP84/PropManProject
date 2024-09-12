package lv.emendatus.Destiny_PropMan.annotation.advanced_tenant_controller;

import lv.emendatus.Destiny_PropMan.domain.dto.communication.SubmitClaimDTO;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Submit a Claim",
        description = "Allows a Tenant to submit a claim related to a specific booking. The Tenant can only submit claims for bookings they are associated with, and only within a set period after the booking has ended.",
        tags = {"TENANT_FUNCTION"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Details of the claim being submitted",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = SubmitClaimDTO.class),
                        examples = @ExampleObject(
                                value = "{\"bookingId\": 14, \"description\": \"The property was not as described, and several amenities were missing.\"}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Claim submitted successfully.",
                        content = @Content(mediaType = "application/json")
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied. The Tenant can only submit claims on their own behalf.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "A Tenant can only submit claims on their own behalf.")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Booking or Tenant not found.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "No booking found with ID: 42")
                        )
                ),
                @ApiResponse(
                        responseCode = "410",
                        description = "Claim period expired. The claim cannot be submitted.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Cannot create claim, claiming period expired for this booking")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while submitting the claim: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "TENANT only")
        }
)

public @interface TenantFunc_SubmitClaim {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
