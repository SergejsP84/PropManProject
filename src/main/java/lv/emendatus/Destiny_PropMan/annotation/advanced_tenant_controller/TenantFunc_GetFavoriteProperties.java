package lv.emendatus.Destiny_PropMan.annotation.advanced_tenant_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.view.FavoritePropertyDTO_Profile;
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
        summary = "Get Favorite Properties",
        description = "Allows a tenant to retrieve their list of favorite properties",
        tags = {"TENANT_FUNCTION"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of favorite properties retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = FavoritePropertyDTO_Profile[].class),
                                examples = @ExampleObject(
                                        name = "Favorite Properties",
                                        value = """
                                                [
                                                    {
                                                        "propertyId": 1,
                                                        "address": "123 Main St",
                                                        "settlement": "Springfield",
                                                        "country": "USA",
                                                        "rating": 4.5
                                                    },
                                                    {
                                                        "propertyId": 2,
                                                        "address": "456 Elm St",
                                                        "settlement": "Shelbyville",
                                                        "country": "USA",
                                                        "rating": 4.0
                                                    }
                                                ]
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request: Invalid input data",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Invalid input data\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized: Authentication credentials are missing or invalid",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found: Tenant not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Tenant not found\"}")
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving favorite properties\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "A Tenant can only view his own favorite Properties")
)
public @interface TenantFunc_GetFavoriteProperties {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

