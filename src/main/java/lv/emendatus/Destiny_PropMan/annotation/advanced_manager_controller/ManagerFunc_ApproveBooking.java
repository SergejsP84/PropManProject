package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
        summary = "Approve Booking",
        description = "Approves a booking made by a Tenant.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "bookingId",
                        description = "ID of the booking",
                        required = true,
                        schema = @io.swagger.v3.oas.annotations.media.Schema(type = "long")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Booking approved successfully."
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request: The booking ID is invalid."
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden: The manager is not authorized to approve bookings."
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not found: No booking with the specified ID exists in the database."
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred."
                )
        },
        security = {
                @SecurityRequirement(name = "Available to the Manager of the Property that has been booked")
        }
)
public @interface ManagerFunc_ApproveBooking {
    String[] path() default {};
}

