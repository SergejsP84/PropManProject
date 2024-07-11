package lv.emendatus.Destiny_PropMan.annotation.tenant_payment_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
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
@RequestMapping(method = RequestMethod.GET)
@Operation(
        summary = "Get a tenant payment by ID",
        description = "NOT accessible in the current setup, but retained for possible future use." +
                "Retrieves a tenant payment from the database by ID",
        tags = {"OTHER"},
        parameters = {
                @Parameter(
                        name = "id",
                        description = "The unique identifier of the tenant payment",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "string"),
                        examples = @ExampleObject(
                                name = "Example TenantPayment",
                                value = "{\n" +
                                        "  \"id\": 1,\n" +
                                        "  \"amount\": 500.0,\n" +
                                        "  \"currency\": {\n" +
                                        "    \"id\": 1,\n" +
                                        "    \"designation\": \"USD\",\n" +
                                        "  },\n" +
                                        "  \"tenant\": {\n" +
                                        "    \"id\": 1,\n" +
                                        "    \"firstName\": \"John\",\n" +
                                        "    \"lastName\": \"Doe\",\n" +
                                        "    \"email\": \"john.doe@example.com\"\n" +
                                        "  },\n" +
                                        "  \"managerId\": 1,\n" +
                                        "  \"associatedPropertyId\": 101,\n" +
                                        "  \"associatedBookingId\": 201,\n" +
                                        "  \"receivedFromTenant\": true,\n" +
                                        "  \"managerPayment\": 50.0,\n" +
                                        "  \"feePaidToManager\": false,\n" +
                                        "  \"receiptDue\": \"2025-01-01T00:00:00.000Z\"\n" +
                                        "}"
                        )
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "TenantPayment retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = TenantPayment.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "TenantPayment not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid ID",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "API should not be exposed as per the current setup")
        }
)
public @interface TenantPayment_GetByID {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
