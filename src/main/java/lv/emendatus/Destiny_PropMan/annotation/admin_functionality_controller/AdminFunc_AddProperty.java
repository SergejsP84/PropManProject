package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.PropertyAdditionDTO;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Add Property",
        description = "Adds a new property to the system. This endpoint is accessible only by admins.",
        tags = {"ADMIN_FUNCTION"},
        requestBody = @RequestBody(
                description = "Details of the property to be added",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PropertyAdditionDTO.class),
                        examples = @ExampleObject(value = "{ \"manager\": { \"id\": 1, \"name\": \"John Doe\" }, \"status\": \"AVAILABLE\", \"type\": \"HOUSE\", \"address\": \"123 Main St\", \"country\": \"USA\", \"settlement\": \"Springfield\", \"sizeM2\": 120.0, \"description\": \"A beautiful house.\", \"rating\": 4.5, \"pricePerDay\": 100.0, \"pricePerWeek\": 600.0, \"pricePerMonth\": 2400.0, \"currencyId\": 1 }")
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Property added successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Property added successfully.")
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized: Authentication is required and has failed or has not yet been provided; alternatively, the endpoint might not be accessible to this user or the entire respective user role.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "415",
                        description = "Unsupported Media Type: The request payload format is not supported",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unsupported Media Type\"}")
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
                @SecurityRequirement(name = "ROLE_ADMIN")
        }
)
public @interface AdminFunc_AddProperty {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
