package lv.emendatus.Destiny_PropMan.annotation.bill_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping(method = RequestMethod.PATCH)

@Operation(
        summary = "Toggle bill payment status",
        description = "Toggles the payment status of a bill. If the bill is marked as paid, it will be marked as unpaid and vice versa.",
        tags = {"MANAGER_FUNCTION"},
        parameters = @Parameter(
                name = "billId",
                description = "The ID of the bill",
                example = "1",
                required = true
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Bill payment status toggled successfully"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Bill not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Bill with the specified ID could not be found\"}")
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while toggling the bill payment status\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to the manager of the property that the bill in question pertains to.")
)

public @interface Bill_TogglePaymentStatus {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
