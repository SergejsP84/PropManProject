package lv.emendatus.Destiny_PropMan.domain.dto.Admin;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminRefundDTO {
    private Long tenantId;
    private Long bookingId;
    private Double amount;
    private Currency currency;
    private Timestamp createdAt;
    private LocalDate dueDate;
    private String paymentDeadline;
}
