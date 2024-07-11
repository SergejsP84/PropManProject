 package lv.emendatus.Destiny_PropMan.annotation.tenant_controller;

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
        summary = "Get tenants by first or last name",
        description = "Retrieve a list of tenants by searching for a given name in their first or last names."
        + "The method is used internally and is not supposed to be accessible directly via an endpoint.",
        tags = {"OTHER"},
        parameters = @Parameter(
                name = "name",
                description = "The name to search for in tenant's first or last names",
                example = "John"
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of tenants matching the search criteria",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        name = "Example Tenant List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"firstName\": \"John\",\n" +
                                                "  \"lastName\": \"Doe\",\n" +
                                                "  \"currentProperty\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"address\": \"123 Main St\",\n" +
                                                "    \"settlement\": \"Anytown\",\n" +
                                                "    \"country\": \"USA\",\n" +
                                                "  },\n" +
                                                "  \"isActive\": true,\n" +
                                                "  \"phone\": \"555-1234\",\n" +
                                                "  \"email\": \"john.doe@example.com\",\n" +
                                                "  \"iban\": \"DE89 3704 0044 0532 0130 00\",\n" +
                                                "  \"paymentCardNo\": \"1234567890123456\",\n" +
                                                "  \"cardValidityDate\": \"2025-12\",\n" +
                                                "  \"cvv\": \"123\",\n" +
                                                "  \"rating\": 4.5,\n" +
                                                "  \"login\": \"johndoe\",\n" +
                                                "  \"password\": \"Password1\",\n" +
                                                "  \"leasingHistories\": [],\n" +
                                                "  \"tenantPayments\": [],\n" +
                                                "  \"confirmationToken\": \"abc123\",\n" +
                                                "  \"expirationTime\": \"2024-01-01T00:00:00\",\n" +
                                                "  \"preferredCurrency\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"designation\": \"USD\",\n" +
                                                "  },\n" +
                                                "  \"authorities\": [{\n" +
                                                "    \"authority\": \"ROLE_TENANT\"\n" +
                                                "  }],\n" +
                                                "  \"knownIps\": [\"192.168.1.1\"]\n" +
                                                "}]"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request: Invalid input data",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No tenants found matching the search criteria",
                        content = @Content
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
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving the booking history\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)

public @interface Tenant_GetByName {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
