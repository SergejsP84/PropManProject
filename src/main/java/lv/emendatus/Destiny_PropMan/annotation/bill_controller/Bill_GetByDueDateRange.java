package lv.emendatus.Destiny_PropMan.annotation.bill_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.annotation.AliasFor;
import org.springframework.format.annotation.DateTimeFormat;
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
        summary = "Get bills by due date range",
        description = "Intended for use in more complex functions for viewing bills for a specific property" +
                "Retrieve a list of bills for a specified with due dates within the specified date range.",
        tags = {"OTHER"},
        parameters = {
                @Parameter(
                        name = "prop_id",
                        description = "The ID of the property",
                        example = "1",
                        required = true
                ),
                @Parameter(
                        name = "start",
                        description = "The start date of the due date range",
                        example = "2024-01-01",
                        required = true,
                        schema = @Schema(type = "string", format = "date")
                ),
                @Parameter(
                        name = "end",
                        description = "The end date of the due date range",
                        example = "2024-12-31",
                        required = true,
                        schema = @Schema(type = "string", format = "date")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of bills for the specified property within the due date range",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        name = "Example Bill List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"amount\": 1500.0,\n" +
                                                "  \"currency\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"code\": \"USD\",\n" +
                                                "    \"name\": \"US Dollar\"\n" +
                                                "  },\n" +
                                                "  \"property\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"address\": \"123 Main St\"\n" +
                                                "  },\n" +
                                                "  \"expenseCategory\": \"Utilities\",\n" +
                                                "  \"dueDate\": \"2024-06-30T00:00:00.000+00:00\",\n" +
                                                "  \"recipient\": \"Electric Company\",\n" +
                                                "  \"recipientIBAN\": \"US12345678901234567890\",\n" +
                                                "  \"isPaid\": false,\n" +
                                                "  \"issuedAt\": \"2024-06-01T00:00:00.000+00:00\",\n" +
                                                "  \"addedAt\": \"2024-06-01T00:00:00.000+00:00\"\n" +
                                                "}, {\n" +
                                                "  \"id\": 2,\n" +
                                                "  \"amount\": 750.0,\n" +
                                                "  \"currency\": {\n" +
                                                "    \"id\": 2,\n" +
                                                "    \"code\": \"EUR\",\n" +
                                                "    \"name\": \"Euro\"\n" +
                                                "  },\n" +
                                                "  \"property\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"address\": \"123 Main St\"\n" +
                                                "  },\n" +
                                                "  \"expenseCategory\": \"Maintenance\",\n" +
                                                "  \"dueDate\": \"2024-07-15T00:00:00.000+00:00\",\n" +
                                                "  \"recipient\": \"Maintenance Service\",\n" +
                                                "  \"recipientIBAN\": \"EU09876543210987654321\",\n" +
                                                "  \"isPaid\": false,\n" +
                                                "  \"issuedAt\": \"2024-06-15T00:00:00.000+00:00\",\n" +
                                                "  \"addedAt\": \"2024-06-15T00:00:00.000+00:00\"\n" +
                                                "}]"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No bills found within the specified due date range",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"No bills found within the specified due date range\"}")
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving the bills\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)

public @interface Bill_GetByDueDateRange {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
