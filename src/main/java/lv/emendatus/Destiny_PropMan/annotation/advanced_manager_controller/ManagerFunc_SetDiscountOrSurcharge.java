package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.PropertyDiscountDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyDiscount;
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
        summary = "Set Discount or Surcharge",
        description = "Allows a manager to set a discount or surcharge for a property.",
        tags = {"MANAGER_FUNCTION"},
        requestBody = @RequestBody(
                description = "Discount or surcharge details",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PropertyDiscountDTO.class)
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Discount or surcharge set successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PropertyDiscount.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request: The request body is invalid.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden: Cannot set discounts or surcharges for properties operated by a different manager.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not found: No property with the specified ID exists in the database.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Managers only")
        }
)
public @interface ManagerFunc_SetDiscountOrSurcharge {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
