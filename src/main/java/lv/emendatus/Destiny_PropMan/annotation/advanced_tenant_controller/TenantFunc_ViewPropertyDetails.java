package lv.emendatus.Destiny_PropMan.annotation.advanced_tenant_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.view.PropertiesForTenantsDTO;
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
        summary = "Get property details",
        description = "Retrieve detailed information about a property by its ID",
        tags = {"GENERAL"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Property details retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PropertiesForTenantsDTO.class),
                                examples = @ExampleObject(
                                        name = "Property Details",
                                        value = """
                                                {
                                                    "id": 1,
                                                    "address": "123 Main St",
                                                    "settlement": "Springfield",
                                                    "country": "USA",
                                                    "description": "A beautiful 2-bedroom apartment",
                                                    "rating": 4.5,
                                                    "pricePerDay": 100.0,
                                                    "pricePerWeek": 600.0,
                                                    "pricePerMonth": 2400.0,
                                                    "currency": {
                                                        "code": "USD",
                                                        "rateToBase": 1.0
                                                    },
                                                    "amenities": ["WiFi", "Parking", "Pool"],
                                                    "photos": ["photo1.jpg", "photo2.jpg"],
                                                    "available": true,
                                                    "reviews": [
                                                        {
                                                            "reviewerName": "John Doe",
                                                            "rating": 4,
                                                            "comment": "Great place!",
                                                            "date": "2023-05-01"
                                                        }
                                                    ]
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Property not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"No property found with ID: 1\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while updating manager profile: error message")
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to all users")
)
public @interface TenantFunc_ViewPropertyDetails {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
