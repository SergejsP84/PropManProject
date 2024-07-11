package lv.emendatus.Destiny_PropMan.annotation.property_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.PUT)

@Operation(
        summary = "Update property address",
        description = "Updates the address of the property with the specified ID." +
                "Not intended for direct use, but is employed as a part of the overall property update mechanism.",
        tags = {"OTHER"},
        parameters = {
                @Parameter(
                        name = "id",
                        description = "ID of the property to update",
                        example = "1",
                        required = true
                ),
                @Parameter(
                        name = "newAddress",
                        description = "New address for the property",
                        example = "123 Main St.",
                        required = true
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Property address updated successfully"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No property found with the specified ID"
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred"
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)
public @interface Property_UpdateAddress {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
