package lv.emendatus.Destiny_PropMan.annotation.tenant_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
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
@RequestMapping(method = RequestMethod.GET)
@Operation(
        summary = "Get a tenant by ID",
        description = "NOT accessible in the current setup, but retained for possible future use." +
                "Retrieves a tenant from the database by ID",
        tags = {"OTHER"},
        parameters = {
                @Parameter(
                        name = "id",
                        description = "The unique identifier of the tenant",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "string"),
                        examples = @ExampleObject(
                                name = "Example Tenant",
                                value = "{\n" +
                                        "  \"id\": 1,\n" +
                                        "  \"firstName\": \"Jane\",\n" +
                                        "  \"lastName\": \"Doe\",\n" +
                                        "  \"currentProperty\": {\n" +
                                        "    \"id\": 101,\n" +
                                        "    \"address\": \"123 Main St\"\n" +
                                        "  },\n" +
                                        "  \"isActive\": true,\n" +
                                        "  \"phone\": \"123-456-7890\",\n" +
                                        "  \"email\": \"jane.doe@example.com\",\n" +
                                        "  \"iban\": \"US123456789\",\n" +
                                        "  \"paymentCardNo\": \"1234567812345678\",\n" +
                                        "  \"cardValidityDate\": \"2023-12\",\n" +
                                        "  \"cvv\": \"123\",\n" +
                                        "  \"rating\": 4.7,\n" +
                                        "  \"login\": \"jane.doe\",\n" +
                                        "  \"password\": \"Password123\",\n" +
                                        "  \"leasingHistories\": [],\n" +
                                        "  \"tenantPayments\": [],\n" +
                                        "  \"confirmationToken\": \"abcd1234\",\n" +
                                        "  \"tokenExpirationTime\": \"2023-01-01T00:00:00.000Z\",\n" +
                                        "  \"preferredCurrency\": {\n" +
                                        "    \"id\": 1,\n" +
                                        "    \"code\": \"USD\",\n" +
                                        "    \"name\": \"United States Dollar\"\n" +
                                        "  },\n" +
                                        "  \"authorities\": [\"ROLE_TENANT\"],\n" +
                                        "  \"knownIps\": [\"192.168.1.1\"]\n" +
                                        "}"
                        )
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Tenant retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Tenant.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Tenant not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid ID",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
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
                @SecurityRequirement(name = "API should not be exposed as per the current setup")
        }
)
public @interface Tenant_GetByID {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
