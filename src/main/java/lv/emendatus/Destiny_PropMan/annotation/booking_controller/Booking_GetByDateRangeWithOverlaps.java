package lv.emendatus.Destiny_PropMan.annotation.booking_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.annotation.AliasFor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.GET)

@Operation(
        summary = "Get bookings by date range with overlaps",
        description = "Retrieves a list of bookings within the specified date range, including overlapping periods." +
                "Not intended to be available directly; users have access to dedicated endpoints for adding and managing Bookings.",
        tags = {"OTHER"},
        parameters = {
                @Parameter(
                        name = "start",
                        description = "Start date of the range",
                        example = "2024-06-01",
                        required = true
                ),
                @Parameter(
                        name = "end",
                        description = "End date of the range",
                        example = "2024-06-07",
                        required = true
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of bookings within the specified date range, including overlapping periods",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = List.class),
                                examples = @ExampleObject(
                                        name = "Example Booking List",
                                        value = "[{\n" +
                                                "  \"property\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"status\": \"AVAILABLE\",\n" +
                                                "    \"createdAt\": \"2024-01-01T00:00:00Z\",\n" +
                                                "    \"type\": \"APARTMENT\",\n" +
                                                "    \"address\": \"123 Main St\",\n" +
                                                "    \"country\": \"USA\",\n" +
                                                "    \"settlement\": \"Springfield\",\n" +
                                                "    \"sizeM2\": 100.0,\n" +
                                                "    \"description\": \"A beautiful apartment.\",\n" +
                                                "    \"rating\": 4.5,\n" +
                                                "    \"pricePerDay\": 100.0,\n" +
                                                "    \"pricePerWeek\": 600.0,\n" +
                                                "    \"pricePerMonth\": 2000.0,\n" +
                                                "    \"photos\": [\n" +
                                                "      \"photo1.jpg\",\n" +
                                                "      \"photo2.jpg\"\n" +
                                                "    ]\n" +
                                                "  },\n" +
                                                "  \"tenantId\": 1,\n" +
                                                "  \"startDate\": \"2024-06-03T14:00:00Z\",\n" +
                                                "  \"endDate\": \"2024-06-10T10:00:00Z\",\n" +
                                                "  \"isPaid\": false,\n" +
                                                "  \"status\": \"PENDING_APPROVAL\"\n" +
                                                "}]"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No bookings found within the specified date range",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"No bookings found within the specified date range\"}")
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving the bookings\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)

public @interface Booking_GetByDateRangeWithOverlaps {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}