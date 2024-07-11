package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
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
        summary = "View Unpaid Bills for Manager",
        description = "Retrieves the list of unpaid bills for a specific manager.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "managerId",
                        description = "ID of the manager",
                        required = true,
                        example = "1",
                        schema = @Schema(type = "integer")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Unpaid bills retrieved successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                array = @ArraySchema(schema = @Schema(implementation = Bill.class))
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while retrieving the unpaid bills for the manager: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Managers only")
        }
)
public @interface ManagerFunc_ViewUnpaidBillsForManager {
}