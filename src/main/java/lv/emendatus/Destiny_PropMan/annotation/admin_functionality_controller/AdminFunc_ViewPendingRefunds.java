package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.Admin.AdminRefundDTO;
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
        summary = "View Pending Refunds",
        description = "Retrieves a list of all pending refunds, including details such as the amount, booking ID, tenant ID, creation date, currency, due date, and payment deadline status. This endpoint is accessible only by admins.",
        tags = {"ADMIN_FUNCTION"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Pending refunds retrieved successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                array = @ArraySchema(schema = @Schema(implementation = AdminRefundDTO.class)),
                                examples = @ExampleObject(value = "[{\"refundId\": 1, \"amount\": 50.0, \"bookingId\": 2, \"managerId\": 3, \"createdAt\": \"2023-05-01T12:00:00\", \"currency\": \"USD\", \"status\": \"Pending\"}]")
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized: Authentication is required and has failed or has not yet been provided; alternatively, the endpoint might not be accessible to this user or the entire respective user role.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Failed to retrieve pending refunds: error message\"}")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Admins only")
        }
)
public @interface AdminFunc_ViewPendingRefunds {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
