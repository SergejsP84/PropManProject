package lv.emendatus.Destiny_PropMan.annotation.message_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Message;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.ErrorResponse;
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
        summary = "Retrieve all Messages",
        description = "Fetches a list of all Messages in the database." +
        "The method is used internally and is not supposed to be accessible directly via an endpoint.",
        tags = {"OTHER"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Messages retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Message.class),
                                examples = @ExampleObject(
                                        name = "Example Message List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"content\": \"Hello, how are you?\",\n" +
                                                "  \"senderId\": 1001,\n" +
                                                "  \"receiverId\": 1002\n" +
                                                "}, {\n" +
                                                "  \"id\": 2,\n" +
                                                "  \"content\": \"I'm fine, thank you!\",\n" +
                                                "  \"senderId\": 1002,\n" +
                                                "  \"receiverId\": 1001\n" +
                                                "}]"
                                )
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
public @interface Message_GetAll {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
