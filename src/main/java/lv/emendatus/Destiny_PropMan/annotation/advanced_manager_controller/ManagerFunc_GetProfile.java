package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerProfileDTO;
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
        summary = "Get Manager Profile",
        description = "Fetches the profile of a manager using their ID.",
        tags = {"GENERAL"},
        parameters = {
                @Parameter(
                        name = "managerId",
                        description = "ID of the manager whose profile is to be fetched",
                        required = true,
                        example = "1",
                        schema = @Schema(type = "integer")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Manager profile fetched successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ManagerProfileDTO.class),
                                examples = @ExampleObject(value = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Manager not found.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "No manager found with ID: 1")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while fetching manager profile: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to everyone")
        }
)
public @interface ManagerFunc_GetProfile {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}