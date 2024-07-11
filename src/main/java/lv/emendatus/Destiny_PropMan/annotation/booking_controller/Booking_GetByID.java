package lv.emendatus.Destiny_PropMan.annotation.booking_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
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
        summary = "Get a booking by ID",
        description = "NOT accessible in the current setup, but retained for possible future use." +
                "Retrieves a booking from the database by ID",
        tags = {"OTHER"},
        parameters = {
                @Parameter(
                        name = "id",
                        description = "The unique identifier of the booking",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "string"),
                        examples = @ExampleObject(
                                name = "Example Booking",
                                value = "{\n" +
                                        "  \"id\": 1,\n" +
                                        "  \"property\": {\n" +
                                        "    \"id\": 101,\n" +
                                        "    \"address\": \"123 Main St\"\n" +
                                        "  },\n" +
                                        "  \"tenantId\": 1,\n" +
                                        "  \"startDate\": \"2023-01-01T00:00:00.000Z\",\n" +
                                        "  \"endDate\": \"2023-01-07T00:00:00.000Z\",\n" +
                                        "  \"isPaid\": true,\n" +
                                        "  \"status\": \"CONFIRMED\"\n" +
                                        "}"
                        )
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Booking retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Booking.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Booking not found",
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
public @interface Booking_GetByID {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
