package lv.emendatus.Destiny_PropMan.annotation.property_amenity_controller;
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
@RequestMapping(method = RequestMethod.GET)

@Operation(
        summary = "Get property amenities by amenity ID",
        description = "Intended for internal use in complex property display functions." +
                "Retrieves a list of property-amenity relations associated with a given amenity ID.",
        tags = {"OTHER"},
        parameters = @Parameter(
                name = "amen_id",
                description = "The ID of the amenity",
                example = "1"
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of property amenities associated with the given amenity ID",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        name = "Example Property Amenity List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"property_id\": 1,\n" +
                                                "  \"amenity_id\": 2\n" +
                                                "}, {\n" +
                                                "  \"id\": 2,\n" +
                                                "  \"property_id\": 3,\n" +
                                                "  \"amenity_id\": 2\n" +
                                                "}]"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Amenity not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"No amenity with the specified ID exists in the database\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "415",
                        description = "Unsupported Media Type: The server cannot process the request because the payload is in an unsupported format",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unsupported Media Type\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving the property amenities\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)

public @interface PropertyAmenity_GetByAmenity {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
