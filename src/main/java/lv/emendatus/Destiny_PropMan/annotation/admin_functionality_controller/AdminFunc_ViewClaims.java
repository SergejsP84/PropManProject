package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Claim;
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
        summary = "View all Claims",
        description = "Allows an Admin to view all claims in the system. This operation is restricted to users with the 'ADMIN' authority.",
        tags = {"ADMIN_FUNCTION"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of claims retrieved successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Claim.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while retrieving the claims: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Admin Authentication")
        }
)
public @interface AdminFunc_ViewClaims {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
