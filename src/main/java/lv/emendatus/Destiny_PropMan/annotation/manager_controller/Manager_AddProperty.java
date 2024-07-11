package lv.emendatus.Destiny_PropMan.annotation.manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
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
        summary = "Add a new property to a manager",
        description = "Intended for internal use in more complex property addition methods, should not be exposed per se." +
                "Adds a new property to an existing manager's portfolio.",
        tags = {"OTHER"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = Property.class),
                        examples = @ExampleObject(
                                name = "Example Property",
                                value = "{\n" +
                                        "  \"manager\": {\n" +
                                        "    \"id\": 1\n" +
                                        "  },\n" +
                                        "  \"status\": \"AVAILABLE\",\n" +
                                        "  \"createdAt\": \"2024-06-06T00:00:00Z\",\n" +
                                        "  \"type\": \"APARTMENT\",\n" +
                                        "  \"address\": \"123 Main St\",\n" +
                                        "  \"country\": \"USA\",\n" +
                                        "  \"settlement\": \"Springfield\",\n" +
                                        "  \"sizeM2\": 100.0,\n" +
                                        "  \"description\": \"A beautiful property.\",\n" +
                                        "  \"rating\": 4.5,\n" +
                                        "  \"pricePerDay\": 100.0,\n" +
                                        "  \"pricePerWeek\": 600.0,\n" +
                                        "  \"pricePerMonth\": 2000.0,\n" +
                                        "  \"photos\": [\n" +
                                        "    \"photo1.jpg\",\n" +
                                        "    \"photo2.jpg\"\n" +
                                        "  ]\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Property added successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Void.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Manager not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Manager not found\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "415",
                        description = "Unsupported Media Type",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unsupported Media Type\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content
                )
        },
        security = {
                @SecurityRequirement(name = "Endpoint should not be exposed as per the current setup")
        }
)

public @interface Manager_AddProperty {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
