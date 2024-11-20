package lv.emendatus.Destiny_PropMan.annotation.leasing_history_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.LeasingHistory;
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
        summary = "Retrieve all LeasingHistories",
        description = "Fetches a list of all LeasingHistories in the database." +
        "The method is used internally and is not supposed to be accessible directly via an endpoint.",
        tags = {"OTHER"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "LeasingHistories retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = LeasingHistory.class),
                                examples = @ExampleObject(
                                        name = "Example LeasingHistory List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"tenant\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"firstName\": \"John\",\n" +
                                                "    \"lastName\": \"Doe\"\n" +
                                                "  },\n" +
                                                "  \"propertyId\": 1,\n" +
                                                "  \"startDate\": \"2023-01-01T00:00:00.000+00:00\",\n" +
                                                "  \"endDate\": \"2023-12-31T00:00:00.000+00:00\"\n" +
                                                "}, {\n" +
                                                "  \"id\": 2,\n" +
                                                "  \"tenant\": {\n" +
                                                "    \"id\": 2,\n" +
                                                "    \"firstName\": \"Jane\",\n" +
                                                "    \"lastName\": \"Smith\"\n" +
                                                "  },\n" +
                                                "  \"propertyId\": 2,\n" +
                                                "  \"startDate\": \"2022-01-01T00:00:00.000+00:00\",\n" +
                                                "  \"endDate\": \"2022-12-31T00:00:00.000+00:00\"\n" +
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
public @interface LeasingHistory_GetAll {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
