package lv.emendatus.Destiny_PropMan.annotation.manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
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
        summary = "Get a manager by ID",
        description = "NOT accessible in the current setup, but retained for possible future use." +
                "Retrieves a manager from the database by ID",
        tags = {"OTHER"},
        parameters = {
                @Parameter(
                        name = "id",
                        description = "The unique identifier of the manager",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "string"),
                        examples = @ExampleObject(
                                name = "Example Manager",
                                value = "{\n" +
                                        "  \"id\": 1,\n" +
                                        "  \"type\": \"CORPORATE\",\n" +
                                        "  \"managerName\": \"John Smith\",\n" +
                                        "  \"description\": \"Senior Manager\",\n" +
                                        "  \"isActive\": true,\n" +
                                        "  \"joinDate\": \"2023-01-01T00:00:00.000Z\",\n" +
                                        "  \"login\": \"john.smith\",\n" +
                                        "  \"password\": \"Password123\",\n" +
                                        "  \"phone\": \"123-456-7890\",\n" +
                                        "  \"email\": \"john.smith@example.com\",\n" +
                                        "  \"iban\": \"US123456789\",\n" +
                                        "  \"paymentCardNo\": \"1234567812345678\",\n" +
                                        "  \"cardValidityDate\": \"2023-12\",\n" +
                                        "  \"cvv\": \"123\",\n" +
                                        "  \"confirmationToken\": \"abcd1234\",\n" +
                                        "  \"tokenExpirationTime\": \"2023-01-01T00:00:00.000Z\",\n" +
                                        "  \"authorities\": [\"ROLE_MANAGER\"],\n" +
                                        "  \"knownIps\": [\"192.168.1.1\"]\n" +
                                        "}"
                        )
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Manager retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Manager.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Manager not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid ID",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
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
                @SecurityRequirement(name = "API should not be exposed as per the current setup")
        }
)
public @interface Manager_GetByID {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
