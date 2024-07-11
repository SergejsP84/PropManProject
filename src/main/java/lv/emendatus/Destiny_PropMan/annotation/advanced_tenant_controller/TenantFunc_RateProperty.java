package lv.emendatus.Destiny_PropMan.annotation.advanced_tenant_controller;

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
        summary = "Rate a property",
        description = "Allows a tenant to rate a property based on a completed booking." +
                "Only the tenant who booked the property can rate it, and a booking can be rated only once.",
        tags = {"TENANT_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "tenant_id",
                        description = "ID of the tenant providing the rating",
                        example = "1",
                        required = true,
                        in = ParameterIn.PATH
                ),
                @Parameter(
                        name = "booking_id",
                        description = "ID of the booking being rated",
                        example = "10",
                        required = true,
                        in = ParameterIn.PATH
                ),
                @Parameter(
                        name = "rating",
                        description = "Rating value between 1 and 5",
                        example = "4",
                        required = true,
                        in = ParameterIn.PATH
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully rated the property"
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid rating value or unauthorized rating attempt"
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized user"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Tenant, booking, or property not found with the given ID"
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred"
                )
        },
        security = @SecurityRequirement(name = "Available to Tenants who made the respective bookings")
)
public @interface TenantFunc_RateProperty {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
