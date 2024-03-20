package lv.emendatus.Destiny_PropMan.domain.dto.Admin;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminTenantPaymentDTO {
    private Long id;
    private boolean receivedFromTenant;
    private boolean feePaidToManager;
    private Timestamp receiptDue;
}
