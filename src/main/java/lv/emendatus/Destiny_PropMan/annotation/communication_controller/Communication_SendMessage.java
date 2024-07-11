package lv.emendatus.Destiny_PropMan.annotation.communication_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.communication.CommunicationDTO;
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
        summary = "Send Message",
        description = "Sends a message from one user to another.",
        tags = {"TENANT_FUNCTION", "MANAGER_FUNCTION"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = CommunicationDTO.class),
                        examples = @ExampleObject(
                                name = "Example Message",
                                value = "{\n" +
                                        "  \"senderId\": 1,\n" +
                                        "  \"senderType\": \"Tenant\",\n" +
                                        "  \"receiverId\": 2,\n" +
                                        "  \"receiverType\": \"Manager\",\n" +
                                        "  \"message\": \"Hello, how are you?\",\n" +
                                        "  \"sentAt\": \"2024-06-15T10:00:00\",\n" +
                                        "  \"isRead\": false\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Message sent successfully",
                        content = @Content(
                                mediaType = "application/json"
                        )
                )
        },
        security = @SecurityRequirement(name = "Available to authorized Tenants and Managers only")
)
public @interface Communication_SendMessage {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
