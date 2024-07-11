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
        summary = "Delete an admin from the database",
        description = "Allows the Default Admin to remove any of the other Admin entities from the database, " +
                "with the sole exception of himself",
        tags = {"ACCESS AND ACCOUNT MANAGEMENT"},
        parameters = {
                @Parameter(
                        name = "login",
                        description = "The unique login of the admin",
                        required = true,
                        in = ParameterIn.QUERY,
                        schema = @Schema(type = "string"),
                        examples = {
                                @ExampleObject(
                                        name = "Example existing login",
                                        value = "admin1"
                                ),
                                @ExampleObject(
                                        name = "Example non-existing login",
                                        value = "nonexistentadmin"
                                )
                        }
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "The admin has been deleted",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Admin with login 'admin1' deleted successfully.\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "The specified admin could not be found in the database",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Admin with login 'nonexistentadmin' could not be found.\"}")
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
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Deleting other Admins is the sole privilege of the DefaultAdmin, which, in turn, is created at the first launch of the project")
        }
)

public @interface AdminAccounts_Delete {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
