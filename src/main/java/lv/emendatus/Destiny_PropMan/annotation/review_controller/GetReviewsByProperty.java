package lv.emendatus.Destiny_PropMan.annotation.review_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.view.ReviewDTO;
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
        summary = "Get Reviews by Property",
        description = "Retrieve reviews for a specific property.",
        tags = {"TENANT_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "propertyId",
                        description = "ID of the property for which reviews are to be retrieved",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "integer", format = "int64")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved reviews",
                        content = @Content(
                                mediaType = "application/json",
                                array = @ArraySchema(schema = @Schema(implementation = ReviewDTO.class))
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No reviews found for the specified property"
                )
        },
        security = @SecurityRequirement(name = "Available to authorized users only")
)
public @interface GetReviewsByProperty {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}