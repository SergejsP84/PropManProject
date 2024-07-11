package lv.emendatus.Destiny_PropMan.annotation.tenant_payment_controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.POST)

@Operation(
        summary = "Settle payment",
        description = "Intended for internal use in settling tenant payments.",
        tags = {"OTHER"},
        parameters = @Parameter(
                name = "id",
                description = "The ID of the payment to be settled",
                example = "1",
                required = true
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Payment successfully settled"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Payment not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"message\": \"Payment not found\"}")
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error: An unexpected error occurred",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class),
                                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"message\": \"An unexpected error occurred while settling the payment\"}")
                        )
                )
        },
        security = @SecurityRequirement(name = "API should not be exposed as per the current setup")
)

public @interface TenantPayment_Settle {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}

