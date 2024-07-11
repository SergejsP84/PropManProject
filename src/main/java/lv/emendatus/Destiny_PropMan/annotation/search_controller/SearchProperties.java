package lv.emendatus.Destiny_PropMan.annotation.search_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.search.PropertySearchResultDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.search.SearchCriteria;
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
        summary = "Search Properties",
        description = "Searches for properties based on various criteria such as location, price range, size, and amenities.",
        tags = {"GENERAL"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = SearchCriteria.class),
                        examples = @ExampleObject(
                                name = "Example Search Criteria",
                                value = "{\n" +
                                        "  \"location\": \"New York\",\n" +
                                        "  \"startDate\": \"2024-07-01\",\n" +
                                        "  \"endDate\": \"2024-07-10\",\n" +
                                        "  \"minPrice\": 100.0,\n" +
                                        "  \"maxPrice\": 500.0,\n" +
                                        "  \"currency\": \"USD\",\n" +
                                        "  \"minSizeM2\": 50.0,\n" +
                                        "  \"maxSizeM2\": 200.0,\n" +
                                        "  \"rating\": 4.0,\n" +
                                        "  \"amenityIds\": [1, 2, 3],\n" +
                                        "  \"type\": \"APARTMENT\"\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Properties matching the search criteria",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PropertySearchResultDTO.class),
                                examples = @ExampleObject(
                                        name = "Example Response",
                                        value = "[\n" +
                                                "  {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"type\": \"APARTMENT\",\n" +
                                                "    \"address\": \"123 Main St, New York, NY\",\n" +
                                                "    \"country\": \"USA\",\n" +
                                                "    \"settlement\": \"New York\",\n" +
                                                "    \"sizeM2\": 75.0,\n" +
                                                "    \"description\": \"A beautiful apartment in the heart of the city\",\n" +
                                                "    \"rating\": 4.7,\n" +
                                                "    \"pricePerDay\": 150.0,\n" +
                                                "    \"pricePerWeek\": 1000.0,\n" +
                                                "    \"pricePerMonth\": 4000.0\n" +
                                                "  }\n" +
                                                "]"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid search criteria",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"Invalid search criteria\"\n" +
                                                "}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = "{\n" +
                                                "  \"error\": \"An unexpected error occurred while processing the search\"\n" +
                                                "}"
                                )
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to all users, whether authorized or not")
)
public @interface SearchProperties {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

