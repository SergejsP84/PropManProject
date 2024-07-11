package lv.emendatus.Destiny_PropMan.annotation.advanced_tenant_controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Request Early Termination",
        description = "Allows a tenant to request early termination of a booking",
        tags = {"TENANT_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "bookingId",
                        description = "ID of the booking to be terminated early",
                        required = true,
                        example = "123"
                ),
                @Parameter(
                        name = "terminationDate",
                        description = "Requested date for early termination",
                        required = true,
                        example = "2024-06-15T12:00:00"
                ),
                @Parameter(
                        name = "comment",
                        description = "Comment for the early termination request",
                        required = false,
                        example = "Need to leave early due to unforeseen circumstances."
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Early termination request submitted successfully",
                        content = @Content(mediaType = "application/json")
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request: Invalid termination date or booking ID",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Invalid termination date or booking ID\"}")
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
                        description = "Not Found: Tenant or booking not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Tenant or booking not found\"}")
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while processing the early termination request\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to Tenants only")
)
public @interface TenantFunc_RequestEarlyTermination {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

