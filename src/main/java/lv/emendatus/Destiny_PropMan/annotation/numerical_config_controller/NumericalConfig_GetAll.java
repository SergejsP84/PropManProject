package lv.emendatus.Destiny_PropMan.annotation.numerical_config_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;
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
        summary = "Retrieve all NumericalConfigs",
        description = "Fetches a list of all NumericalConfigs in the database." +
        "The method is used internally and is not supposed to be accessible directly via an endpoint.",
        tags = {"OTHER"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "NumericalConfigs retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = NumericalConfig.class),
                                examples = @ExampleObject(
                                        name = "Example NumericalConfig List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"name\": \"Discount Rate\",\n" +
                                                "  \"value\": 0.15,\n" +
                                                "  \"type\": \"DISCOUNT\",\n" +
                                                "  \"currency\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"code\": \"USD\",\n" +
                                                "    \"name\": \"United States Dollar\"\n" +
                                                "  }\n" +
                                                "}, {\n" +
                                                "  \"id\": 2,\n" +
                                                "  \"name\": \"Service Fee\",\n" +
                                                "  \"value\": 5.0,\n" +
                                                "  \"type\": \"SYSTEM_SETTING\",\n" +
                                                "  \"currency\": {\n" +
                                                "    \"id\": 2,\n" +
                                                "    \"code\": \"EUR\",\n" +
                                                "    \"name\": \"Euro\"\n" +
                                                "  }\n" +
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
public @interface NumericalConfig_GetAll {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
