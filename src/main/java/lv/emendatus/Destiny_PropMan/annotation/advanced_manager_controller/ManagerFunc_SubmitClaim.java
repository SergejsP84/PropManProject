package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;
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
        summary = "Submit Claim for a Booking",
        description = "Allows a Manager to submit a claim for a specific booking.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "bookingId",
                        description = "ID of the booking the claim is being filed for",
                        required = true,
                        schema = @Schema(type = "integer")
                ),
                @Parameter(
                        name = "description",
                        description = "Description of the claim being submitted",
                        required = true,
                        schema = @Schema(type = "string")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Claim submitted successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request: The request parameters are invalid.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Invalid booking ID or description")
                        )
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden: The manager is not authorized to submit a claim for this booking.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Manager not authorized to submit a claim for this booking")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not found: The booking does not exist in the database.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "No booking found with the specified ID")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Failed to submit claim: error message\"}")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Managers only")
        }
)
public @interface ManagerFunc_SubmitClaim {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
