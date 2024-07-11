package lv.emendatus.Destiny_PropMan.annotation.booking_controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
        summary = "Get booking price",
        description = "Calculates and retrieves the total price of a booking." +
                "Not intended to be available directly; users have access to dedicated endpoints for adding and managing Bookings.",
        tags = {"OTHER"},
        parameters = @Parameter(
                name = "booking_id",
                description = "ID of the booking",
                example = "1",
                required = true
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Total price of the booking calculated successfully"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No booking found with the specified ID",
                        content = @io.swagger.v3.oas.annotations.media.Content(
                                mediaType = "application/json",
                                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = String.class),
                                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"message\": \"No booking found with the specified ID\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred",
                        content = @io.swagger.v3.oas.annotations.media.Content(
                                mediaType = "application/json",
                                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = String.class),
                                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"message\": \"An unexpected error occurred while calculating the booking price\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)

public @interface Booking_GetPrice {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
