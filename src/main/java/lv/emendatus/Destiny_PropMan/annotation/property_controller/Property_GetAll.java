package lv.emendatus.Destiny_PropMan.annotation.property_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.PropertyAmenity;
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
        summary = "Retrieve all Properties",
        description = "Fetches a list of all Properties in the database." +
        "The method is used internally and is not supposed to be accessible directly via an endpoint.",
        tags = {"OTHER"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Properties retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Property.class),
                                examples = @ExampleObject(
                                        name = "Example Property List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"manager\": {\n" +
                                                "    \"id\": 101,\n" +
                                                "    \"type\": \"CORPORATE\",\n" +
                                                "    \"managerName\": \"John Doe\",\n" +
                                                "    \"description\": \"Manager Description\",\n" +
                                                "    \"isActive\": true,\n" +
                                                "    \"joinDate\": \"2023-01-01T00:00:00.000Z\",\n" +
                                                "    \"login\": \"johndoe\",\n" +
                                                "    \"password\": \"password123\",\n" +
                                                "    \"properties\": [],\n" +
                                                "    \"phone\": \"123-456-7890\",\n" +
                                                "    \"email\": \"john.doe@example.com\",\n" +
                                                "    \"iban\": \"DE89370400440532013000\",\n" +
                                                "    \"paymentCardNo\": \"1234567891011121\",\n" +
                                                "    \"cardValidityDate\": \"2025-12\",\n" +
                                                "    \"cvv\": \"123\",\n" +
                                                "    \"confirmationToken\": \"token\",\n" +
                                                "    \"expirationTime\": \"2024-01-01T00:00:00.000Z\",\n" +
                                                "    \"authorities\": [],\n" +
                                                "    \"knownIps\": []\n" +
                                                "  },\n" +
                                                "  \"status\": \"AVAILABLE\",\n" +
                                                "  \"createdAt\": \"2023-01-01T00:00:00.000Z\",\n" +
                                                "  \"type\": \"APARTMENT\",\n" +
                                                "  \"address\": \"123 Main St\",\n" +
                                                "  \"country\": \"Country\",\n" +
                                                "  \"settlement\": \"Settlement\",\n" +
                                                "  \"sizeM2\": 100.0,\n" +
                                                "  \"description\": \"Property Description\",\n" +
                                                "  \"rating\": 4.5,\n" +
                                                "  \"pricePerDay\": 100.0,\n" +
                                                "  \"pricePerWeek\": 600.0,\n" +
                                                "  \"pricePerMonth\": 2000.0,\n" +
                                                "  \"bookings\": [],\n" +
                                                "  \"bills\": [],\n" +
                                                "  \"tenant\": {\n" +
                                                "    \"id\": 201,\n" +
                                                "    \"firstName\": \"Jane\",\n" +
                                                "    \"lastName\": \"Doe\",\n" +
                                                "    \"currentProperty\": null,\n" +
                                                "    \"isActive\": true,\n" +
                                                "    \"phone\": \"987-654-3210\",\n" +
                                                "    \"email\": \"jane.doe@example.com\",\n" +
                                                "    \"iban\": \"DE89370400440532013001\",\n" +
                                                "    \"paymentCardNo\": \"4321876510912112\",\n" +
                                                "    \"cardValidityDate\": \"2025-12\",\n" +
                                                "    \"cvv\": \"321\",\n" +
                                                "    \"rating\": 4.7,\n" +
                                                "    \"login\": \"janedoe\",\n" +
                                                "    \"password\": \"Password321\",\n" +
                                                "    \"leasingHistories\": [],\n" +
                                                "    \"tenantPayments\": [],\n" +
                                                "    \"confirmationToken\": \"token\",\n" +
                                                "    \"expirationTime\": \"2024-01-01T00:00:00.000Z\",\n" +
                                                "    \"preferredCurrency\": null\n" +
                                                "  },\n" +
                                                "  \"photos\": []\n" +
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
public @interface Property_GetAll {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
