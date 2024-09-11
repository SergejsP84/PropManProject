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
        summary = "Update Currency Exchange Rate",
        description = "Allows an Admin to update the exchange rate of a specified currency relative to the system's base currency. The base currency's rate is always 1.00 and cannot be changed.",
        tags = {"ADMIN_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "currencyId",
                        description = "ID of the currency to be updated",
                        required = true,
                        example = "3",
                        schema = @Schema(type = "integer")
                ),
                @Parameter(
                        name = "newRate",
                        description = "New exchange rate relative to the base currency",
                        required = true,
                        example = "38.42",
                        schema = @Schema(type = "number", format = "double")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Currency exchange rate updated successfully.",
                        content = @Content(mediaType = "application/json")
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Currency not found.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "No currency found with ID: 5")
                        )
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied. Only Admins can update currency exchange rates.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "You do not have permission to perform this action.")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while updating the currency exchange rate: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "ADMIN only")
        }
)

public @interface AdminFunc_UpdateCurrencyRate {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

