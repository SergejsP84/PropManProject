package lv.emendatus.Destiny_PropMan.annotation.communication_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
        summary = "Get Conversation Between Users",
        description = "Retrieves the conversation between two users.",
        tags = {"TENANT_FUNCTION", "MANAGER_FUNCTION"},
        parameters = {
                @Parameter(name = "user1Id", description = "ID of the first user"),
                @Parameter(name = "user2Id", description = "ID of the second user")
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Conversation retrieved successfully"
                )
        },
        security = @SecurityRequirement(name = "Available to authorized Tenants or Managers only")
)
public @interface Communication_GetConversation {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}