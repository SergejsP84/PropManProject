package lv.emendatus.Destiny_PropMan.domain.dto.managerial;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDiscountDTO {
    @NotNull
    private Long propertyId;

    @NotNull
    private Long managerId;

    @NotNull
    @Min(-100)
    private Double percentage;

    @NotNull
    @FutureOrPresent
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate periodStart;

    @NotNull
    @FutureOrPresent
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate periodEnd;
}
