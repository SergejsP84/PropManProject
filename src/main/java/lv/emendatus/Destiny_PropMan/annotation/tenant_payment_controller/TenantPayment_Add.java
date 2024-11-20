package lv.emendatus.Destiny_PropMan.annotation.tenant_payment_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.ResponseEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Adds a new tenant payment",
        description = "Adds a new TenantPayment entity to the system." +
                "Not intended to be available directly; TenantPayments are generated whenevere a Tenant makes or alter a Booking.",
        tags = {"OTHER"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = TenantPayment.class),
                        examples = @ExampleObject(
                                name = "Example Tenant Payment",
                                value = "{\n" +
                                        "  \"amount\": 100.50,\n" +
                                        "  \"currency\": {\n" +
                                        "    \"id\": 1,\n" +
                                        "    \"designation\": \"USD\",\n" +
                                        "    \"isBaseCurrency\": true,\n" +
                                        "    \"rateToBase\": 1.0\n" +
                                        "  },\n" +
                                        "  \"tenant\": {\n" +
                                        "    \"id\": 1,\n" +
                                        "    \"firstName\": \"John\",\n" +
                                        "    \"lastName\": \"Doe\",\n" +
                                        "    \"currentProperty\": null,\n" +
                                        "    \"isActive\": true,\n" +
                                        "    \"phone\": \"123456789\",\n" +
                                        "    \"email\": \"john.doe@example.com\",\n" +
                                        "    \"iban\": \"DE89370400440532013000\",\n" +
                                        "    \"paymentCardNo\": \"1234567812345678\",\n" +
                                        "    \"cardValidityDate\": \"2023-12\",\n" +
                                        "    \"cvv\": \"123\",\n" +
                                        "    \"rating\": 4.5,\n" +
                                        "    \"login\": \"johndoe\",\n" +
                                        "    \"password\": \"Password123\",\n" +
                                        "    \"leasingHistories\": [],\n" +
                                        "    \"tenantPayments\": [],\n" +
                                        "    \"confirmationToken\": null,\n" +
                                        "    \"expirationTime\": null,\n" +
                                        "    \"preferredCurrency\": null\n" +
                                        "  },\n" +
                                        "  \"managerId\": 1,\n" +
                                        "  \"associatedPropertyId\": 1,\n" +
                                        "  \"associatedBookingId\": null,\n" +
                                        "  \"receivedFromTenant\": true,\n" +
                                        "  \"managerPayment\": 0,\n" +
                                        "  \"feePaidToManager\": false,\n" +
                                        "  \"receiptDue\": \"2024-06-10T00:00:00Z\"\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Tenant payment added successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ResponseEntity.class)
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
                @SecurityRequirement(name = "Endpoint should not be exposed as per the current setup")
        }
)

public @interface TenantPayment_Add {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

