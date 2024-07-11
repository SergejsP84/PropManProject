package lv.emendatus.Destiny_PropMan.annotation.tenant_payment_controller;

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
        summary = "Get payments by manager",
        description = "Intended for internal use in more complex tenant payment functionality" +
        "Retrieves a list of payments due to or received by a specified manager.",
        tags = {"OTHER"},
        parameters = @Parameter(
                name = "man_id",
                description = "The ID of the manager",
                example = "1",
                required = true
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of payments due to or received by a specified manager",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        name = "Example Tenant Payment List",
                                        value = "[{\n" +
                                                "  \"amount\": 500.0,\n" +
                                                "  \"currency\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"designation\": \"USD\",\n" +
                                                "  },\n" +
                                                "  \"tenant\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"firstName\": \"John\",\n" +
                                                "    \"lastName\": \"Doe\",\n" +
                                                "    \"email\": \"john.doe@example.com\"\n" +
                                                "  },\n" +
                                                "  \"managerId\": 1,\n" +
                                                "  \"associatedPropertyId\": 101,\n" +
                                                "  \"associatedBookingId\": 201,\n" +
                                                "  \"receivedFromTenant\": true,\n" +
                                                "  \"managerPayment\": 50.0,\n" +
                                                "  \"feePaidToManager\": true,\n" +
                                                "  \"receiptDue\": \"2023-01-01T00:00:00.000Z\"\n" +
                                                "}]"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No payments found for the specified manager or manager not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"No payments found for the specified manager\"}")
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving the payments\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)

public @interface TenantPayment_GetByManager {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
