package lv.emendatus.Destiny_PropMan.annotation.tenant_payment_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
        summary = "Retrieve all TenantPayments",
        description = "Fetches a list of all TenantPayments in the database." +
        "The method is used internally and is not supposed to be accessible directly via an endpoint.",
        tags = {"OTHER"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "TenantPayments retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = TenantPayment.class),
                                examples = @ExampleObject(
                                        name = "Example TenantPayment List",
                                        value = "[{\n" +
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
                                                "  \"feePaidToManager\": true,\n" +
                                                "  \"receiptDue\": \"2023-01-01T00:00:00.000Z\"\n" +
                                                "}]"
                                )
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
public @interface TenantPayment_GetAll {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
