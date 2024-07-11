package lv.emendatus.Destiny_PropMan.annotation.reservation_controler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.reservation.ReservationCancellationDTO;
import org.springframework.core.annotation.AliasFor;
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
        summary = "Cancel Reservation",
        description = "Cancels a reservation made by a tenant.",
        tags = {"TENANT_FUNCTION"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = ReservationCancellationDTO.class),
                        examples = @ExampleObject(
                                name = "Example Cancellation Request",
                                value = "{\n" +
                                        "  \"reservationId\": 1,\n" +
                                        "  \"tenantId\": 2,\n" +
                                        "  \"cancellationDateTime\": \"2024-06-15T10:00:00\",\n" +
                                        "  \"reason\": \"Double booking\"\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Reservation cancelled successfully",
                        content = @Content(
                                mediaType = "text/plain",
                                examples = @ExampleObject(
                                        value = "Reservation cancelled successfully"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Reservation not found",
                        content = @Content(
                                mediaType = "text/plain",
                                examples = @ExampleObject(
                                        value = "Reservation not found"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Cannot cancel the reservation",
                        content = @Content(
                                mediaType = "text/plain",
                                examples = @ExampleObject(
                                        value = "Cannot cancel a current booking - please request early termination instead."
                                )
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to authorized users only")
)
public @interface CancelReservation {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
