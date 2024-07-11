package lv.emendatus.Destiny_PropMan.annotation.advanced_manager_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lv.emendatus.Destiny_PropMan.domain.dto.managerial.FinancialStatementDTO;

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
        summary = "Generate Financial Statement",
        description = "Generates a financial statement for the specified manager within the given period.",
        tags = {"MANAGER_FUNCTION"},
        parameters = {
                @Parameter(
                        name = "managerId",
                        description = "ID of the manager",
                        required = true,
                        example = "1",
                        schema = @Schema(type = "integer")
                ),
                @Parameter(
                        name = "periodStart",
                        description = "Start date of the financial period",
                        required = true,
                        example = "2023-01-01",
                        schema = @Schema(type = "string", format = "date")
                ),
                @Parameter(
                        name = "periodEnd",
                        description = "End date of the financial period",
                        required = true,
                        example = "2023-12-31",
                        schema = @Schema(type = "string", format = "date")
                )
        },
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Financial statement generated successfully.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = FinancialStatementDTO.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "An error occurred while generating the financial statement: error message")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Managers only")
        }
)
public @interface ManagerFunc_GenerateFinancialStatement {

}

