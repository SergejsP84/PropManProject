package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.PathVariable;
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
        summary = "Remove Property",
        description = "Allows a manager to remove a property.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "propertyId",
                        description = "ID of the property to be removed",
                        required = true,
                        schema = @io.swagger.v3.oas.annotations.media.Schema(type = "long")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Property removed successfully."
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request: The property ID is invalid."
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden: The manager is not authorized to remove the property."
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not found: No property with the specified ID exists in the database."
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred."
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Managers only")
        }
)
public @interface ManagerFunc_RemoveProperty {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
