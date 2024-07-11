package lv.emendatus.Destiny_PropMan.annotation.advanced_tenant_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.view.BookingsViewDTO;
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
        summary = "View Current Bookings",
        description = "Allows a tenant to view their current bookings",
        tags = {"TENANT_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "tenantId",
                        description = "ID of the tenant",
                        required = true,
                        example = "1"
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Current bookings retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = BookingsViewDTO.class),
                                examples = @ExampleObject(
                                        name = "Current Bookings",
                                        value = """
                                                [
                                                    {
                                                        "startDate": "2024-01-01T00:00:00.000+00:00",
                                                        "endDate": "2024-01-10T00:00:00.000+00:00",
                                                        "isPaid": true,
                                                        "status": "CONFIRMED"
                                                    },
                                                    {
                                                        "startDate": "2024-02-01T00:00:00.000+00:00",
                                                        "endDate": "2024-02-10T00:00:00.000+00:00",
                                                        "isPaid": false,
                                                        "status": "PENDING"
                                                    }
                                                ]
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request: Invalid tenant ID",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Invalid tenant ID\"}")
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
                        description = "Not Found: Tenant or bookings not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Tenant or bookings not found\"}")
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving current bookings\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "A Tenant can only view his own Bookings")
)
public @interface TenantFunc_ViewTenantsBookings {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
