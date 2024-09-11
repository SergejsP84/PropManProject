package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManageAmenitiesRequestDTO;
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
        summary = "Manage Property Amenities",
        description = "Updates the amenities for a specified property. This endpoint allows a manager to add or remove amenities associated with their property.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "propertyId",
                        description = "ID of the property for which amenities are being managed",
                        required = true,
                        example = "3",
                        schema = @Schema(type = "integer")
                )
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "DTO containing the property ID and a list of amenity IDs to be associated with the property",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ManageAmenitiesRequestDTO.class),
                        examples = @ExampleObject(
                                value = "{\n" +
                                        "  \"propertyId\": 3,\n" +
                                        "  \"amenityIds\": [1, 2, 4, 7]\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Amenities updated successfully.",
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
                                examples = @ExampleObject(value = "No property found with ID: 3")
                        )
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied. You do not have permission to update amenities for this property.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "You do not have permission to change the Amenities of a Property operated by someone else.")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while updating the amenities: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to the property's Manager only")
        }
)
public @interface ManagerFunc_ManageAmenities {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
