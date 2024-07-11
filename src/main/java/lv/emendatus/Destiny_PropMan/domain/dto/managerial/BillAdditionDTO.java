package lv.emendatus.Destiny_PropMan.domain.dto.managerial;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.entity.Currency;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillAdditionDTO {
    private double amount;
    private Currency currency;
    private String expenseCategory;
    private Timestamp dueDate;
    private String recipient;
    private String recipientIBAN;
    private Timestamp issuedAt;
}
