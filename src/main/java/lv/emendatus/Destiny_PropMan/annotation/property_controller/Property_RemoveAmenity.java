package lv.emendatus.Destiny_PropMan.annotation.property_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping(method = RequestMethod.DELETE)

@Operation(
        summary = "Remove an amenity from a property",
        description = "Removes a specified amenity from a property based on property ID and amenity ID.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "prop_id",
                        description = "ID of the property from which to remove the amenity",
                        example = "1",
                        required = true
                ),
                @Parameter(
                        name = "amen_id",
                        description = "ID of the amenity to remove",
                        example = "2",
                        required = true
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Amenity removed from the property successfully"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No property found with the specified ID"
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred"
                )
        },
        security = @SecurityRequirement(name = "Available to Managers of the respective Properties")
)
public @interface Property_RemoveAmenity {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}