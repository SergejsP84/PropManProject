package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;
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
        summary = "Make Property Unavailable",
        description = "Allows a manager to make a property unavailable for booking for a specific period.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "propertyId",
                        description = "ID of the property to make unavailable.",
                        required = true,
                        in = ParameterIn.QUERY,
                        example = "1"
                ),
                @Parameter(
                        name = "periodStart",
                        description = "Start date of the period to make the property unavailable.",
                        required = true,
                        in = ParameterIn.QUERY,
                        example = "2024-06-01"
                ),
                @Parameter(
                        name = "periodEnd",
                        description = "End date of the period to make the property unavailable.",
                        required = true,
                        in = ParameterIn.QUERY,
                        example = "2024-06-05"
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Property made unavailable successfully."
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request: The request parameters are invalid."
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden: The manager is not authorized to perform this action."
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred."
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Managers only")
        }
)
public @interface ManagerFunc_MakePropertyUnavailable {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}