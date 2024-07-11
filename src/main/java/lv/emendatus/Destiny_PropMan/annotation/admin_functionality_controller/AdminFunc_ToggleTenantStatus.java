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
        summary = "Toggle a Tenant's status",
        description = "This method allows an Admin to toggle a Tenant's status, thus rendering the Tenant inactive or active, opposite to the current state." +
                "Inactivating a Tenant prevents the Tenant from making any other Bookings, but does not cancel his/her current Bookings, if any, and does not preclude the making of TenantPayments.",
        tags = {"ADMIN_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "id",
                        description = "The unique identifier of the tenant",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "string"),
                        examples = {
                                @ExampleObject(
                                        name = "Example request with correct Id",
                                        value = "9"
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
                        description = "Tenant's status toggled",
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

public @interface AdminFunc_ToggleTenantStatus {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
