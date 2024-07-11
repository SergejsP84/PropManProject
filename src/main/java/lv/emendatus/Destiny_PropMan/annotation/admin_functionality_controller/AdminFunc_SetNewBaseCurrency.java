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
        summary = "Set New Base Currency",
        description = "Sets a new base currency and updates the exchange rates for other currencies. This endpoint is accessible only by admins.",
        tags = {"ADMIN_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "newBaseCurrencyId",
                        description = "ID of the new base currency",
                        required = true,
                        example = "1",
                        schema = @Schema(type = "integer")
                ),
                @Parameter(
                        name = "ratesForOtherCurrencies",
                        description = "List of exchange rates for other currencies relative to the new base currency",
                        required = true,
                        schema = @Schema(type = "array", implementation = Double.class)
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Base currency and exchange rates updated successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Base currency and exchange rates updated successfully.")
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
                                examples = @ExampleObject(value = "An error occurred while setting new base currency: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Admins only")
        }
)
public @interface AdminFunc_SetNewBaseCurrency {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
