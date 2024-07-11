package lv.emendatus.Destiny_PropMan.annotation.manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
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
        summary = "Retrieve all Managers",
        description = "Fetches a list of all Managers in the database." +
        "The method is used internally and is not supposed to be accessible directly via an endpoint.",
        tags = {"OTHER"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Managers retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Manager.class),
                                examples = @ExampleObject(
                                        name = "Example Manager List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"type\": \"PRIVATE\",\n" +
                                                "  \"managerName\": \"John Doe\",\n" +
                                                "  \"description\": \"Experienced manager\",\n" +
                                                "  \"isActive\": true,\n" +
                                                "  \"joinDate\": \"2023-01-01T00:00:00.000+00:00\",\n" +
                                                "  \"login\": \"johndoe\",\n" +
                                                "  \"password\": \"Password123\",\n" +
                                                "  \"phone\": \"123-456-7890\",\n" +
                                                "  \"email\": \"johndoe@example.com\",\n" +
                                                "  \"iban\": \"DE89370400440532013000\",\n" +
                                                "  \"paymentCardNo\": \"1234567812345678\",\n" +
                                                "  \"cardValidityDate\": \"2024-12\",\n" +
                                                "  \"cvv\": \"123\",\n" +
                                                "  \"confirmationToken\": \"abc123\",\n" +
                                                "  \"expirationTime\": \"2023-01-01T00:00:00.000+00:00\",\n" +
                                                "  \"authorities\": [\"ROLE_MANAGER\"],\n" +
                                                "  \"knownIps\": [\"192.168.1.1\"]\n" +
                                                "}, {\n" +
                                                "  \"id\": 2,\n" +
                                                "  \"type\": \"CORPORATE\",\n" +
                                                "  \"managerName\": \"Jane Smith\",\n" +
                                                "  \"description\": \"Corporate manager\",\n" +
                                                "  \"isActive\": false,\n" +
                                                "  \"joinDate\": \"2022-01-01T00:00:00.000+00:00\",\n" +
                                                "  \"login\": \"janesmith\",\n" +
                                                "  \"password\": \"SecurePass123\",\n" +
                                                "  \"phone\": \"987-654-3210\",\n" +
                                                "  \"email\": \"janesmith@example.com\",\n" +
                                                "  \"iban\": \"GB33BUKB20201555555555\",\n" +
                                                "  \"paymentCardNo\": \"8765432187654321\",\n" +
                                                "  \"cardValidityDate\": \"2025-11\",\n" +
                                                "  \"cvv\": \"456\",\n" +
                                                "  \"confirmationToken\": \"def456\",\n" +
                                                "  \"expirationTime\": \"2022-01-01T00:00:00.000+00:00\",\n" +
                                                "  \"authorities\": [\"ROLE_MANAGER\"],\n" +
                                                "  \"knownIps\": [\"10.0.0.1\"]\n" +
                                                "}]"
                                )
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
                @SecurityRequirement(name = "Endpoint should not be exposed as per the current setup")
        }
)
public @interface Manager_GetAll {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
