package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.access.prepost.PreAuthorize;
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
        summary = "Remove Photo",
        description = "Allows a manager to remove a photo from a property.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "propertyId",
                        description = "ID of the property from which to remove the photo",
                        required = true,
                        example = "123",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(type = "long")
                ),
                @Parameter(
                        name = "photoUrl",
                        description = "URL of the photo to be removed",
                        required = true,
                        example = "/photos/123/photo1.jpg",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Photo removed successfully."
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request: The request is invalid."
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden: The manager is not authorized to remove the photo."
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not found: The specified property or photo does not exist in the database."
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred."
                )
        },
        security = {
                @SecurityRequirement(name = "Available to the Manager of the specific Property")
        }
)
@PreAuthorize("hasRole('ROLE_MANAGER')")
public @interface ManagerFunc_RemovePhoto {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
