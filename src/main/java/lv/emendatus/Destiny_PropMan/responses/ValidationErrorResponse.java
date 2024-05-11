package lv.emendatus.Destiny_PropMan.responses;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Validation error response")
public class ValidationErrorResponse {
    @ApiModelProperty(notes = "One or more fields are invalid")
    private String error;
    @ApiModelProperty(notes = "This error indicates that one or more fields of the object passed as an argument are invalid. Please check the structure of the object.")
    private String message;

}
