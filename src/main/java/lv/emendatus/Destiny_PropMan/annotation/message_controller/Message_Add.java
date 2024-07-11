package lv.emendatus.Destiny_PropMan.annotation.message_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Message;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.ResponseEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.POST)
@Operation(
        summary = "Adds a new message",
        description = "Adds a new message entity to the system."
        + "Not intended to be available directly; conversation is facilitated by dedicated mechanisms",
        tags = {"OTHER"},
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        schema = @Schema(implementation = Message.class),
                        examples = @ExampleObject(
                                name = "Example Message",
                                value = "{\n" +
                                        "  \"content\": \"Hello, world!\",\n" +
                                        "  \"senderId\": 123,\n" +
                                        "  \"receiverId\": 456\n" +
                                        "}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Message added successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ResponseEntity.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Endpoint should not be exposed as per the current setup")
        }
)

public @interface Message_Add {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
