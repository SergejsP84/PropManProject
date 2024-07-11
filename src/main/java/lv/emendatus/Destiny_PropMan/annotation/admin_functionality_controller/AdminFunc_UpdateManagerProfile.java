package lv.emendatus.Destiny_PropMan.annotation.admin_functionality_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lv.emendatus.Destiny_PropMan.domain.dto.managerial.ManagerProfileDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.profile.TenantDTO_Profile;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.PUT)
@Operation(
        summary = "Update Manager Information",
        description = "Updates the information of a manager specified by managerId. This endpoint is accessible only by admins.",
        tags = {"ADMIN_FUNCTION"},
        parameters = {
                @Parameter(name = "managerId", description = "ID of the manager to be updated", required = true, in = ParameterIn.PATH)
        },
        requestBody = @RequestBody(
                description = "Updated manager information",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ManagerProfileDTO.class),
                        examples = {
                                @ExampleObject(name = "Updated Manager Information",
                                        value = """
                                                {
                                                    "id": 1,
                                                    "managerName": "Wendy Testaburger",
                                                    "description": "Manager with attitude",
                                                    "phone": "+1234567890",
                                                    "email": "tast.a@burger.com",
                                                    "iban": "LV54560400880367019510",
                                                    "paymentCardNo": "4903474399435405"
                                                }"""
                                )
                        }
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Manager information updated successfully by admin.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "Manager information updated successfully by admin.")
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Invalid manager information.\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized: Authentication is required and has failed or has not yet been provided; alternatively, the endpoint might not be accessible to this user or the entire respective user role.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unauthorized\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found: No manager with the specified ID exists.",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"No manager found with ID: 1\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "415",
                        description = "Unsupported Media Type: The request payload format is not supported",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"Unsupported Media Type\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"An error occurred while updating manager information.\"}")
                        )
                )
        },
        security = {
                @SecurityRequirement(name = "Available to Admins only")
        }
)

public @interface AdminFunc_UpdateManagerProfile {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
