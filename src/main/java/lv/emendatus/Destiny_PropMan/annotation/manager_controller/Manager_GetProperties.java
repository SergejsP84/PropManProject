package lv.emendatus.Destiny_PropMan.annotation.manager_controller;

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
        summary = "Get Manager Properties",
        description = "Not used in the current setup, retained for possible future use" +
                "Retrieve the properties managed by a manager using the manager's ID.",
        tags = {"OTHER"},
        parameters = @Parameter(
                name = "id",
                description = "The ID of the manager to retrieve properties for",
                example = "1",
                required = true
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Properties successfully retrieved",
                        content = @io.swagger.v3.oas.annotations.media.Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Manager not found",
                        content = @io.swagger.v3.oas.annotations.media.Content
                )
        },
        security = @SecurityRequirement(name = "Not used in the current setup")
)

public @interface Manager_GetProperties {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

