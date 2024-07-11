package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.PropertyAdditionDTO;
import org.springframework.core.annotation.AliasFor;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
        summary = "Add Property",
        description = "Allows a Manager to add a new property.",
        tags = {"MANAGER_FUNCTION"},
        requestBody = @RequestBody(
                description = "Property addition details",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PropertyAdditionDTO.class)
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Property added successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request: The request body is invalid.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Invalid request body format")
                        )
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden: The manager is not authorized to add properties.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Inactive managers cannot add properties")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not found: No manager with the specified ID exists in the database.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "No manager with the specified ID exists in the database")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Failed to add property: error message\"}")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Managers only")
        }
)
public @interface ManagerFunc_AddProperty {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
