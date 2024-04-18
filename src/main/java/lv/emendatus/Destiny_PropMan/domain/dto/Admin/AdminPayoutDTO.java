package lv.emendatus.Destiny_PropMan.domain.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminPayoutDTO {
    private Long managerId;
    private Long bookingId;
    private Double amount;
    private Timestamp createdAt;
    private LocalDate dueDate;
    private String paymentDeadline;
}
