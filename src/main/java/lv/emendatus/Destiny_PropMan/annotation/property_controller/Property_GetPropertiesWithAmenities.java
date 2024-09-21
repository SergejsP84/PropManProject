package lv.emendatus.Destiny_PropMan.annotation.property_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
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
        summary = "Get properties with amenities",
        description = "Retrieves a list of properties with the specified amenities." +
                "Not intended for direct use, but is employed as a part of the overall property search mechanism.",
        tags = {"OTHER"},
        parameters = {
                @Parameter(
                        name = "amenityIds",
                        description = "List of amenity IDs",
                        example = "[1, 2, 3]",
                        required = true
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Properties retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Property.class),
                                examples = @ExampleObject(
                                        name = "Example Property Set",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"manager\": {\n" +
                                                "    \"id\": 101,\n" +
                                                "    \"type\": \"PRIVATE\",\n" +
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
                                                "  \"country\": \"USA\",\n" +
                                                "  \"settlement\": \"New York\",\n" +
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
                        responseCode = "404",
                        description = "No properties found with the specified amenities"
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred"
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)
public @interface Property_GetPropertiesWithAmenities {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
