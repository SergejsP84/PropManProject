package lv.emendatus.Destiny_PropMan.annotation.property_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
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
        summary = "Get a property by ID",
        description = "NOT accessible in the current setup, but retained for possible future use." +
                "Retrieves a property from the database by ID",
        tags = {"OTHER"},
        parameters = {
                @Parameter(
                        name = "id",
                        description = "The unique identifier of the property",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "string"),
                        examples = @ExampleObject(
                                name = "Example Property",
                                value = "{\n" +
                                        "  \"id\": 101,\n" +
                                        "  \"manager\": {\n" +
                                        "    \"id\": 1,\n" +
                                        "    \"managerName\": \"John Smith\"\n" +
                                        "  },\n" +
                                        "  \"status\": \"AVAILABLE\",\n" +
                                        "  \"createdAt\": \"2023-01-01T00:00:00.000Z\",\n" +
                                        "  \"type\": \"APARTMENT\",\n" +
                                        "  \"address\": \"123 Main St\",\n" +
                                        "  \"country\": \"USA\",\n" +
                                        "  \"settlement\": \"New York\",\n" +
                                        "  \"sizeM2\": 85.0,\n" +
                                        "  \"description\": \"Spacious apartment in the city center\",\n" +
                                        "  \"rating\": 4.5,\n" +
                                        "  \"pricePerDay\": 100.0,\n" +
                                        "  \"pricePerWeek\": 600.0,\n" +
                                        "  \"pricePerMonth\": 2000.0,\n" +
                                        "  \"bookings\": [],\n" +
                                        "  \"bills\": [],\n" +
                                        "  \"tenant\": {\n" +
                                        "    \"id\": 1,\n" +
                                        "    \"firstName\": \"Jane\",\n" +
                                        "    \"lastName\": \"Doe\"\n" +
                                        "  },\n" +
                                        "  \"photos\": [\"photo1.jpg\", \"photo2.jpg\"]\n" +
                                        "}"
                        )
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Property retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Property.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Property not found",
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
public @interface Property_GetByID {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
