package lv.emendatus.Destiny_PropMan.annotation.advanced_tenant_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.BookingHistoryDTO;
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
        summary = "View Booking History",
        description = "Allows a tenant to view their booking history",
        tags = {"TENANT_FUNCTION"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Booking history retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = BookingHistoryDTO.class),
                                examples = @ExampleObject(
                                        name = "Booking History",
                                        value = """
                                                {
                                                    "tenantId": 1,
                                                    "leasingHistory": [
                                                        {
                                                            "propertyId": 101,
                                                            "address": "123 Main St",
                                                            "settlement": "Somewhere",
                                                            "country": "USA",
                                                            "startDate": "2022-01-01T00:00:00.000Z",
                                                            "endDate": "2022-12-31T00:00:00.000Z"
                                                        },
                                                        {
                                                            "propertyId": 102,
                                                            "address": "456 Oak St",
                                                            "settlement": "Anywhere",
                                                            "country": "USA",
                                                            "startDate": "2023-01-01T00:00:00.000Z",
                                                            "endDate": "2023-12-31T00:00:00.000Z"
                                                        }
                                                    ]
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request: Invalid input data",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
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
                        description = "Not Found: Tenant not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Tenant with ID not found\"}")
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving the booking history\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "A Tenant can only view his own booking history")
)
public @interface TenantFunc_ViewBookingHistory {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
