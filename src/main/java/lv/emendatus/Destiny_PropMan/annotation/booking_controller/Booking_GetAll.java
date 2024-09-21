package lv.emendatus.Destiny_PropMan.annotation.booking_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
        summary = "Retrieve all bookings",
        description = "Fetches a list of all bookings in the database." +
        "The method is used internally and is not supposed to be accessible directly via an endpoint.",
        tags = {"OTHER"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Bookings retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Booking.class),
                                examples = @ExampleObject(
                                        name = "Example Booking List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"property\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"address\": \"123 Main St\"\n" +
                                                "  },\n" +
                                                "  \"tenantId\": 1,\n" +
                                                "  \"startDate\": \"2024-07-01T00:00:00.000+00:00\",\n" +
                                                "  \"endDate\": \"2024-07-15T00:00:00.000+00:00\",\n" +
                                                "  \"isPaid\": true,\n" +
                                                "  \"status\": \"CONFIRMED\"\n" +
                                                "}, {\n" +
                                                "  \"id\": 2,\n" +
                                                "  \"property\": {\n" +
                                                "    \"id\": 2,\n" +
                                                "    \"address\": \"456 Elm St\"\n" +
                                                "  },\n" +
                                                "  \"tenantId\": 2,\n" +
                                                "  \"startDate\": \"2024-08-01T00:00:00.000+00:00\",\n" +
                                                "  \"endDate\": \"2024-08-10T00:00:00.000+00:00\",\n" +
                                                "  \"isPaid\": false,\n" +
                                                "  \"status\": \"PENDING_PAYMENT\"\n" +
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
public @interface Booking_GetAll {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
