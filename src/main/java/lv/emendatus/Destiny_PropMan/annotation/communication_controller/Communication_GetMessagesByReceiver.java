package lv.emendatus.Destiny_PropMan.annotation.communication_controller;

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
@RequestMapping(method = RequestMethod.GET)
@Operation(
        summary = "Get Messages by Receiver",
        description = "Retrieves messages received by a specific user.",
        tags = {"TENANT_FUNCTION", "MANAGER_FUNCTION"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Messages retrieved successfully"
                )
        },
        security = @SecurityRequirement(name = "Available to authorized users only")
)
public @interface Communication_GetMessagesByReceiver {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
