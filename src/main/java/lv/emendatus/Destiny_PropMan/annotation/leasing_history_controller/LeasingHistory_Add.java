package lv.emendatus.Destiny_PropMan.annotation.leasing_history_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
import lv.emendatus.Destiny_PropMan.responses.ValidationErrorResponse;
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
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Adds a new leasing history",
        description = "Adds a new LeasingHistory entity to the system." +
                "Not intended to be available directly; serves as a part of various user functionality mechanisms instead.",
        tags = {"OTHER"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = LeasingHistory.class),
                        examples = @ExampleObject(
                                name = "Example LeasingHistory",
                                value = "{\n" +
                                        "  \"tenant\": {\n" +
                                        "    \"id\": 1,\n" +
                                        "    \"firstName\": \"John\",\n" +
                                        "    \"lastName\": \"Doe\",\n" +
                                        "    \"currentProperty\": null,\n" +
                                        "    \"isActive\": true,\n" +
                                        "    \"phone\": \"1234567890\",\n" +
                                        "    \"email\": \"john.doe@example.com\",\n" +
                                        "    \"iban\": \"DE89370400440532013000\",\n" +
                                        "    \"paymentCardNo\": \"1234567812345678\",\n" +
                                        "    \"cardValidityDate\": \"2024-12\",\n" +
                                        "    \"cvv\": \"123\",\n" +
                                        "    \"rating\": 4.5,\n" +
                                        "    \"login\": \"johndoe\",\n" +
                                        "    \"password\": \"P@ssw0rd\",\n" +
                                        "    \"confirmationToken\": \"\",\n" +
                                        "    \"expirationTime\": \"2024-12-31T23:59:59\",\n" +
                                        "    \"preferredCurrency\": {\n" +
                                        "      \"id\": 1,\n" +
                                        "      \"designation\": \"USD\",\n" +
                                        "      \"isBaseCurrency\": true,\n" +
                                        "      \"rateToBase\": 1.0\n" +
                                        "    }\n" +
                                        "  },\n" +
                                        "  \"propertyId\": 1,\n" +
                                        "  \"startDate\": \"2024-01-01T00:00:00\",\n" +
                                        "  \"endDate\": \"2024-12-31T23:59:59\"\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Leasing history added successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = LeasingHistory.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Validation error: One or more fields are invalid",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ValidationErrorResponse.class)
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

public @interface LeasingHistory_Add {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

