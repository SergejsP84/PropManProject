package lv.emendatus.Destiny_PropMan.annotation.amenity_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
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

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Adds a new amenity",
        description = "Adds a new Amenity type to the system." +
                "Intended for use by Admins.",
        tags = {"ADMIN_FUNCTION"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "The amenity being added",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Amenity.class),
                        examples = {
                                @ExampleObject(name = "Added Amenity entity",
                                        value = """
                                                {
                                                                "description": "jacuzzi"
                                                }"""
                                )
                        }
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Amenity added successfully",
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

public @interface Amenity_Add {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
