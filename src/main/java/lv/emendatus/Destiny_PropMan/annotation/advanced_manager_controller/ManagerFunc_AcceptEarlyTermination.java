package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

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
        summary = "Accept Early Termination Request",
        description = "Allows a manager to accept an early termination request.",
        tags = {"MANAGER_FUNCTION"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Early termination request accepted successfully."
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
                        responseCode = "404",
                        description = "Not found: The early termination request or associated booking was not found."
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
public @interface ManagerFunc_AcceptEarlyTermination {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
