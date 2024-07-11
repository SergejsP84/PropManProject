package lv.emendatus.Destiny_PropMan.annotation.advanced_tenant_controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
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
        summary = "Process Payment",
        description = "Allows a tenant to process a payment",
        tags = {"TENANT_FUNCTION"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "The payment information",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = TenantPayment.class),
                        examples = @ExampleObject(
                                name = "Process Payment",
                                value = """
                                        {
                                            "tenant": {
                                                "id": 12
                                            },
                                            "amount": 100.00,
                                            "currency": {
                                                "id": 1,
                                                "designation": "EUR"
                                            },
                                            "managerId": 9,
                                            "associatedPropertyId": 10,
                                            "associatedBookingId": 15,
                                            "receivedFromTenant": false,
                                            "managerPayment": 80.00,
                                            "feePaidToManager": false,
                                            "receiptDue": "2024-06-15T12:00:00"
                                        }
                                        """
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Payment processed successfully",
                        content = @Content(mediaType = "application/json")
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request: Invalid payment information",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Invalid payment information\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized: Authentication credentials are missing or invalid",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found: Booking not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Booking not found\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "415",
                        description = "Unsupported Media Type: The server cannot process the request because the payload is in an unsupported format",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unsupported Media Type\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while processing the payment\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to Tenants only")
)
public @interface TenantFunc_ProcessPayment {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
