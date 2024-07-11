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
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.GET)

@Operation(
        summary = "Get leasing history by time period",
        description = "Intended for use in more complex tenant functions for viewing one's history of previous bookings" +
                "Retrieve a list of leasing histories within a specified time period.",
        tags = {"OTHER"},
        parameters = {
                @Parameter(
                        name = "start",
                        description = "The start date of the time period (inclusive)",
                        example = "2023-01-01",
                        required = true
                ),
                @Parameter(
                        name = "end",
                        description = "The end date of the time period (inclusive)",
                        example = "2023-12-31",
                        required = true
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of leasing histories within the specified time period",
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
                                                "  \"tenantId\": 2,\n" +
                                                "  \"propertyId\": 3,\n" +
                                                "  \"startDate\": \"2023-07-01T00:00:00\",\n" +
                                                "  \"endDate\": \"2023-12-31T00:00:00\"\n" +
                                                "}]"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No leasing histories found within the specified time period",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"No leasing histories found within the specified time period\"}")
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

public @interface LeasingHistory_GetByTimePeriod {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
