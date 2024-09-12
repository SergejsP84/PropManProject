package lv.emendatus.Destiny_PropMan.annotation.admin_accounts_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.ErrorResponse;
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
        summary = "Delete an admin",
        description = "Deletes the specified admin if they exist. Only 'DefaultAdmin' is authorized to perform this operation.",
        tags = {"ADMIN_FUNCTION"},
        parameters = @Parameter(
                name = "login",
                description = "The login of the admin to delete",
                required = true,
                schema = @Schema(type = "string", example = "AnotherAdmin")
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Admin deleted successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Admin with login 'admin1' deleted successfully.\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Admin not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Admin with login 'nonexistentadmin' could not be found.\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"An error occurred.\"}")
                        )
                )
        }
)
public @interface AdminAccounts_Delete {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
