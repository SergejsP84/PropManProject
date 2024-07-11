package lv.emendatus.Destiny_PropMan.annotation.advanced_tenant_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping(method = RequestMethod.GET)
@Operation(
        summary = "Get tenant profile",
        description = "Retrieve profile information for a specific tenant by their ID",
        tags = {"TENANT_FUNCTION"},
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Tenant profile retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = TenantDTO_Profile.class),
                                examples = @ExampleObject(
                                        name = "Tenant Profile",
                                        value = """
                                                {
                                                    "tenantId": 1,
                                                    "firstName": "John",
                                                    "lastName": "Doe",
                                                    "phone": "1234567890",
                                                    "email": "johndoe@example.com",
                                                    "iban": "FR7630006000011234567890189",
                                                    "paymentCardNo": "4908474399435405",
                                                    "rating": 4.5
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Tenant not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"No tenant found with ID: 1\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred while retrieving tenant profile\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "Tenants can only view their own profiles")
)
public @interface TenantFunc_GetProfile {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}