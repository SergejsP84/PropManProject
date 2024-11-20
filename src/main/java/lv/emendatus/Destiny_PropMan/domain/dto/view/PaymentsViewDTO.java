package lv.emendatus.Destiny_PropMan.domain.dto.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentsViewDTO {
    private Double amount; // from the TenantPayment class
    private String currency;
    private String description; // from the Property class
    private String address; // from the Property class
    private String settlement; // from the Property class
    private String country; // from the Property class
    private Timestamp receiptDue; // from the TenantPayment class
}
