package lv.emendatus.Destiny_PropMan.annotation.reservation_controler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ConfirmationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationRequestDTO;
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
        summary = "Make Reservation",
        description = "Makes a reservation for a property.",
        tags = {"TENANT_FUNCTION"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = ReservationRequestDTO.class),
                        examples = @ExampleObject(
                                name = "Example Reservation Request",
                                value = "{\n" +
                                        "  \"propertyId\": 1,\n" +
                                        "  \"tenantId\": 2,\n" +
                                        "  \"startDate\": \"2024-09-15T10:00:00.000Z\",\n" +
                                        "  \"endDate\": \"2024-09-20T10:00:00.000Z\"\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Reservation made successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ConfirmationDTO.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"message\": \"Successfully booked property 1 for the period of 2024-06-15 through 2024-06-20!\"\n" +
                                                "}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid reservation request",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"Invalid reservation request\"\n" +
                                                "}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "409",
                        description = "Conflict - property not available",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"Property not available\"\n" +
                                                "}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"An unexpected error occurred while processing the reservation\"\n" +
                                                "}"
                                )
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to authorized Tenants only")
)
public @interface MakeReservation {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
