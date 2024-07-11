package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.responses.ValidationErrorResponse;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.PUT)
@Operation(
        summary = "Toggle a Manager's status",
        description = "This method allows an Admin to toggle a Manager's status, thus rendering the Manager inactive or active, opposite to the current state." +
                "Inactivating a Manager renders this Manager's Properties BLOCKED and unavailable for rent until the Manager is reactivated, but does not cancel any current Bookings, and does nto prevent the Manager from acting on Claims and receiving Payouts.",
        tags = {"ADMIN_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "id",
                        description = "The unique identifier of the Manager",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "string"),
                        examples = {
                                @ExampleObject(
                                        name = "Example request with correct Id",
                                        value = "17"
                                ),
                                @ExampleObject(
                                        name = "Example request with non-exist Id",
                                        value = "0"
                                ),
                                @ExampleObject(
                                        name = "Example request with invalid Id",
                                        value = "-1"
                                )
                        }
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Manager's status toggled",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Currency.class)
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
                @SecurityRequirement(name = "Available to Admins only")
        }
)

public @interface AdminFunc_ToggleManagerStatus {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
