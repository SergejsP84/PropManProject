package lv.emendatus.Destiny_PropMan.annotation.leasing_history_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping(method = RequestMethod.GET)

@Operation(
        summary = "Get leasing history by tenant",
        description = "Intended for use in more complex functions for viewing leasing histories for a specific tenant" +
                "Retrieve a list of leasing histories for a specified tenant.",
        tags = {"OTHER"},
        parameters = @Parameter(
                name = "ten_id",
                description = "The ID of the tenant",
                example = "1",
                required = true
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of leasing histories for the specified tenant",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        name = "Example Leasing History List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"tenantId\": 1,\n" +
                                                "  \"propertyId\": 2,\n" +
                                                "  \"startDate\": \"2023-01-15T00:00:00\",\n" +
                                                "  \"endDate\": \"2023-06-15T00:00:00\"\n" +
                                                "}, {\n" +
                                                "  \"id\": 2,\n" +
                                                "  \"tenantId\": 1,\n" +
                                                "  \"propertyId\": 3,\n" +
                                                "  \"startDate\": \"2023-07-01T00:00:00\",\n" +
                                                "  \"endDate\": \"2023-12-31T00:00:00\"\n" +
                                                "}]"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No leasing histories found for the specified tenant",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"No leasing histories found for the specified tenant\"}")
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving the leasing history\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)

public @interface LeasingHistory_GetByTenant {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
