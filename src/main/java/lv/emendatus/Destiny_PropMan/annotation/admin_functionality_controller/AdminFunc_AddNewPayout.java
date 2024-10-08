package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;

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
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Add New Payout",
        description = "Creates a new payout for a manager based on a booking, amount, and currency. This endpoint is accessible only by admins.",
        tags = {"ADMIN_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "bookingId",
                        description = "ID of the booking associated with the payout",
                        required = true,
                        example = "1",
                        schema = @Schema(type = "integer")
                ),
                @Parameter(
                        name = "managerId",
                        description = "ID of the manager who will receive the payout",
                        required = true,
                        example = "1",
                        schema = @Schema(type = "integer")
                ),
                @Parameter(
                        name = "amount",
                        description = "Amount of the payout",
                        required = true,
                        example = "100.0",
                        schema = @Schema(type = "number", format = "double")
                ),
                @Parameter(
                        name = "currencyId",
                        description = "ID of the currency for the payout",
                        required = true,
                        example = "1",
                        schema = @Schema(type = "integer")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Payout added successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Payout added successfully.")
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized: Authentication is required and has failed or has not yet been provided; alternatively, the endpoint might not be accessible to this user or the entire respective user role.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Unauthorized")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while adding payout: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Admins only")
        }
)
public @interface AdminFunc_AddNewPayout {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
