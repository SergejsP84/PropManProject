package lv.emendatus.Destiny_PropMan.annotation.bill_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
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
        summary = "Adds a new bill",
        description = "Adds a new Bill entity to the system." +
                "Not intended to be available directly; Managers have access to dedicated endpoints for adding and removing Bills pertaining to their Properties.",
        tags = {"OTHER"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = Bill.class),
                        examples = @ExampleObject(
                                name = "Example Bill",
                                value = "{\n" +
                                        "  \"amount\": 100.50,\n" +
                                        "  \"currency\": {\n" +
                                        "    \"id\": 1,\n" +
                                        "    \"designation\": \"USD\",\n" +
                                        "    \"isBaseCurrency\": true,\n" +
                                        "    \"rateToBase\": 1.0\n" +
                                        "  },\n" +
                                        "  \"property\": {\n" +
                                        "    \"id\": 1,\n" +
                                        "    \"status\": \"AVAILABLE\",\n" +
                                        "    \"createdAt\": \"2024-01-01T00:00:00Z\",\n" +
                                        "    \"type\": \"APARTMENT\",\n" +
                                        "    \"address\": \"123 Main St\",\n" +
                                        "    \"country\": \"USA\",\n" +
                                        "    \"settlement\": \"Springfield\",\n" +
                                        "    \"sizeM2\": 100.0,\n" +
                                        "    \"description\": \"A beautiful apartment.\",\n" +
                                        "    \"rating\": 4.5,\n" +
                                        "    \"pricePerDay\": 100.0,\n" +
                                        "    \"pricePerWeek\": 600.0,\n" +
                                        "    \"pricePerMonth\": 2000.0,\n" +
                                        "    \"photos\": [\n" +
                                        "      \"photo1.jpg\",\n" +
                                        "      \"photo2.jpg\"\n" +
                                        "    ]\n" +
                                        "  },\n" +
                                        "  \"expenseCategory\": \"Utilities\",\n" +
                                        "  \"dueDate\": \"2024-12-31T23:59:59Z\",\n" +
                                        "  \"recipient\": \"Utility Company\",\n" +
                                        "  \"recipientIBAN\": \"DE89370400440532013000\",\n" +
                                        "  \"isPaid\": false,\n" +
                                        "  \"issuedAt\": \"2024-01-01T00:00:00Z\",\n" +
                                        "  \"addedAt\": \"2024-01-01T00:00:00Z\"\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Bill added successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Currency.class)
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

public @interface Bill_Add {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
