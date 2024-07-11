package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;

import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(method = RequestMethod.PATCH)
@Operation(
        summary = "Resolve Claim",
        description = "Resolves a claim by setting its status to RESOLVED and recording the resolution details. This endpoint is accessible only by admins.",
        tags = {"ADMIN_FUNCTION"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Claim resolved successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Claim resolved successfully.")
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
                        responseCode = "404",
                        description = "Not Found: No claim found with the specified ID.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"No claim found with ID: {claimId}\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Failed to resolve claim: error message\"}")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Admins only")
        }
)
public @interface AdminFunc_ResolveClaim {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
