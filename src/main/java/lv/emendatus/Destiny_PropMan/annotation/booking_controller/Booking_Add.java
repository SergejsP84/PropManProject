package lv.emendatus.Destiny_PropMan.annotation.booking_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Booking;
import lv.emendatus.Destiny_PropMan.responses.ValidationErrorResponse;
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
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Adds a new booking",
        description = "Adds a new Booking entity to the system." +
                "Not intended to be available directly; users have access to dedicated endpoints for adding and managing Bookings.",
        tags = {"OTHER"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = Booking.class),
                        examples = @ExampleObject(
                                name = "Example Booking",
                                value = "{\n" +
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
                                        "  \"startDate\": \"2024-06-01T14:00:00Z\",\n" +
                                        "  \"endDate\": \"2024-06-07T10:00:00Z\",\n" +
                                        "  \"isPaid\": false,\n" +
                                        "  \"status\": \"PENDING_APPROVAL\"\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Booking added successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Booking.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Validation error: One or more fields are invalid",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ValidationErrorResponse.class)
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

public @interface Booking_Add {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
