package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.TenantDTOForManagers;
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
        summary = "View Tenant Profile by Booking ID",
        description = "Allows a Manager to view the profile of a Tenant who has made a booking for one of their properties. Only the Manager of the property associated with the booking can access this information.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "bookingId",
                        description = "ID of the booking associated with the Tenant",
                        required = true,
                        example = "8",
                        schema = @Schema(type = "integer")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Tenant profile retrieved successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = TenantDTOForManagers.class),
                                examples = @ExampleObject(value = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"phone\":\"+123456789\",\"email\":\"johndoe@example.com\",\"rating\":4.5}")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Booking or Tenant not found.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "No booking found with ID: 123 or Tenant not found for this booking.")
                        )
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied. You do not have permission to view this Tenant's profile.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "You can only view the profiles of Tenants with active Bookings for any of your Properties.")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while retrieving the Tenant profile: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to the property's Manager only")
        }
)
public @interface ManagerFunc_ViewBookingTenant {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
