package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
        summary = "Add an amenity to the database",
        description = "Adds a new amenity to the database. This action is only allowed for administrators.",
        tags = {"ADMIN_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "amenityDescription",
                        description = "Description of the amenity to be added",
                        example = "Swimming Pool",
                        required = true,
                        in = ParameterIn.QUERY
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully added the amenity"
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden: Only administrators can add amenities"
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred"
                )
        },
        security = @SecurityRequirement(name = "Available to Admins only")
)
public @interface AdminFunc_AddAmenityToDatabase {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
