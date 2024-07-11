package lv.emendatus.Destiny_PropMan.annotation.manager_controller;


import io.swagger.v3.oas.annotations.Operation;
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
        summary = "Remove a property from a manager's portfolio",
        description = "Intended for internal use in more complex property removal methods, should not be exposed per se." +
                "Removes a property from an existing manager's portfolio based on their IDs.",
        tags = {"OTHER"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Property removed successfully"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Manager or property not found"
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error"
                )
        },
        security = {
                @SecurityRequirement(name = "Endpoint should not be exposed as per the current setup")
        }
)

public @interface Manager_RemoveProperty {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
