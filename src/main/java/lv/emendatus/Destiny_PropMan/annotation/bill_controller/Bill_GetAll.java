package lv.emendatus.Destiny_PropMan.annotation.bill_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.entity.Amenity;
import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
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
        summary = "Retrieve all bills",
        description = "Fetches a list of all bills in the database." +
        "The method is used internally and is not supposed to be accessible directly via an endpoint.",
        tags = {"OTHER"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Bills retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Bill.class),
                                examples = @ExampleObject(
                                        name = "Example Bill List",
                                        value = "[{\n" +
                                                "  \"id\": 1,\n" +
                                                "  \"amount\": 1500.0,\n" +
                                                "  \"currency\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"designation\": \"USD\",\n" +
                                                "  },\n" +
                                                "  \"property\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"address\": \"123 Main St\"\n" +
                                                "  },\n" +
                                                "  \"expenseCategory\": \"Utilities\",\n" +
                                                "  \"dueDate\": \"2024-06-30T00:00:00.000+00:00\",\n" +
                                                "  \"recipient\": \"Electric Company\",\n" +
                                                "  \"recipientIBAN\": \"US12345678901234567890\",\n" +
                                                "  \"isPaid\": false,\n" +
                                                "  \"issuedAt\": \"2024-06-01T00:00:00.000+00:00\",\n" +
                                                "  \"addedAt\": \"2024-06-01T00:00:00.000+00:00\"\n" +
                                                "}, {\n" +
                                                "  \"id\": 2,\n" +
                                                "  \"amount\": 750.0,\n" +
                                                "  \"currency\": {\n" +
                                                "    \"id\": 2,\n" +
                                                "    \"designation\": \"EUR\",\n" +
                                                "  },\n" +
                                                "  \"property\": {\n" +
                                                "    \"id\": 2,\n" +
                                                "    \"address\": \"456 Elm St\"\n" +
                                                "  },\n" +
                                                "  \"expenseCategory\": \"Maintenance\",\n" +
                                                "  \"dueDate\": \"2024-07-15T00:00:00.000+00:00\",\n" +
                                                "  \"recipient\": \"Maintenance Service\",\n" +
                                                "  \"recipientIBAN\": \"EU09876543210987654321\",\n" +
                                                "  \"isPaid\": true,\n" +
                                                "  \"issuedAt\": \"2024-06-15T00:00:00.000+00:00\",\n" +
                                                "  \"addedAt\": \"2024-06-15T00:00:00.000+00:00\"\n" +
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
public @interface Bill_GetAll {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
