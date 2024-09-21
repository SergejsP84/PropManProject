package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Upload Photos",
        description = "Allows a manager to upload photos for a property.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "propertyId",
                        description = "ID of the property to upload photos for",
                        required = true,
                        example = "123",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(type = "long")
                ),
                @Parameter(
                        name = "files",
                        description = "Photos to be uploaded",
                        required = true,
                        schema = @io.swagger.v3.oas.annotations.media.Schema(type = "array", implementation = MultipartFile.class)
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Photos uploaded successfully."
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request: The request is invalid."
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden: The manager is not authorized to upload photos."
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
                @SecurityRequirement(name = "Available to the Manager of the respective Property")
        }
)
@PreAuthorize("hasRole('ROLE_MANAGER')")
public @interface ManagerFunc_UploadPhotos {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
