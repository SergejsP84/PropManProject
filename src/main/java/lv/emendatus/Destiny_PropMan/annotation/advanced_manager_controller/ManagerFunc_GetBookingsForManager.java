package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.BookingDTO_Reservation;
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
        summary = "Get Bookings for Manager",
        description = "Retrieves the list of bookings managed by a specific manager.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "managerId",
                        description = "ID of the manager whose bookings are to be retrieved",
                        required = true,
                        example = "1",
                        schema = @Schema(type = "integer")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Bookings retrieved successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(type = "array", implementation = BookingDTO_Reservation.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied: You do not have permission to access this resource.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "You do not have permission to access this resource.")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Manager not found.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Unable to find the specified manager!")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while retrieving the bookings: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Managers only")
        }
)
public @interface ManagerFunc_GetBookingsForManager {
}

