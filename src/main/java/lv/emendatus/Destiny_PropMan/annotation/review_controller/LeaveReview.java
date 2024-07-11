package lv.emendatus.Destiny_PropMan.annotation.review_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Leave Review",
        description = "Allows a tenant to leave a review for a property.",
        tags = {"TENANT_FUNCTION"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReviewDTO.class)
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Review posted successfully"
                )
        },
        security = @SecurityRequirement(name = "Available to authorized Tenants only")
)
public @interface LeaveReview {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}