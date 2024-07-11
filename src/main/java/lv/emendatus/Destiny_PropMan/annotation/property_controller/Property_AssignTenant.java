package lv.emendatus.Destiny_PropMan.annotation.property_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
@RequestMapping(method = RequestMethod.POST)

@Operation(
        summary = "Assign a tenant to a property",
        description = "Assigns a tenant to a property based on the property ID and tenant ID." +
                "Not intended for direct use, but is employed as a part of the overall property management mechanism.",
        tags = {"OTHER"},
        parameters = {
                @Parameter(
                        name = "prop_id",
                        description = "ID of the property to assign the tenant to",
                        example = "1",
                        required = true,
                        in = ParameterIn.PATH
                ),
                @Parameter(
                        name = "ten_id",
                        description = "ID of the tenant to assign to the property",
                        example = "10",
                        required = true,
                        in = ParameterIn.PATH
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Tenant assigned to property successfully"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No property or tenant found with the specified IDs"
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred"
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)
public @interface Property_AssignTenant {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
