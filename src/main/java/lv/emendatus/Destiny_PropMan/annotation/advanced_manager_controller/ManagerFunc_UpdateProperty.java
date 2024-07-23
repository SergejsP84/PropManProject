package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.PropertyUpdateDTO;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.PUT)
@Operation(
        summary = "Update Manager Profile",
        description = "Updates the profile of a manager using their ID. This endpoint is accessible only by the specific manager being thus updated.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "propertyId",
                        description = "ID of the property to be updated",
                        required = true,
                        example = "1",
                        schema = @Schema(type = "integer")
                )
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated property details",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PropertyUpdateDTO.class),
                        examples = @ExampleObject(value = "{\"description\":\"Spacious apartment with sea view\",\"type\":\"Apartment\",\"status\":\"Available\",\"sizeM2\":85,\"address\":\"123 Beach St\",\"settlement\":\"Sunnyville\",\"country\":\"Countryland\",\"pricePerDay\":100,\"pricePerWeek\":600,\"pricePerMonth\":2000}")
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Property updated successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Void.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Property not found.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "No property found with ID: 1")
                        )
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied. You do not have permission to update this property.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "You do not have permission to change the details of a Property operated by someone else.")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while updating the property: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to the property's Manager only")
        }
)
public @interface ManagerFunc_UpdateProperty {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}