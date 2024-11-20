package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.Admin.SetNumConfigDTO;
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
        summary = "Set Numerical Configurations",
        description = "Updates the numerical configurations for the system. This endpoint is accessible only by admins.",
        tags = {"ADMIN_FUNCTION"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = SetNumConfigDTO.class),
                        examples = @ExampleObject(
                                name = "Example DTO",
                                value = "{\n" +
                                        "  \"refundPaymentPeriodDays\": 15,\n" +
                                        "  \"payoutPaymentPeriodDays\": 20,\n" +
                                        "  \"claimPeriodDays\": 7,\n" +
                                        "  \"paymentPeriodDays\": 8,\n" +
                                        "  \"earlyTerminationPenalty\": 0,\n" +
                                        "  \"systemInterestRate\": 10,\n" +
                                        "  \"lateCancellationPeriodInDays\": 10,\n" +
                                        "  \"urgentCancellationPeriodInDays\": 3,\n" +
                                        "  \"urgentCancellationPenaltyPercentage\": 50,\n" +
                                        "  \"lateCancellationPenaltyPercentage\": 25,\n" +
                                        "  \"regularCancellationPenaltyPercentage\": 0\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Numerical configurations updated successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Numerical configurations updated successfully.")
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
                                examples = @ExampleObject(value = "An error occurred while updating numerical configurations: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Admins only")
        }
)
public @interface AdminFunc_SetNumericalConfigs {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
