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
@RequestMapping(method = RequestMethod.PUT)

@Operation(
        summary = "Unlock a property",
        description = "Unlocks a property by removing all associated locks and deleting the corresponding booking stubs. " +
                "This action is only allowed for the manager assigned to the property.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "propertyId",
                        description = "ID of the property to be unlocked",
                        example = "1",
                        required = true,
                        in = ParameterIn.PATH
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully unlocked the property"
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden: Only the assigned manager can unlock the property"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Property not found with the given ID"
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred"
                )
        },
        security = @SecurityRequirement(name = "Available to the respective Property's Manager")
)
public @interface ManagerFunc_UnlockProperty {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
