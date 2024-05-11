package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payouts")
public class Payout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Manager ID is required")
    @Column(name = "manager_id")
    private Long managerId;
    @NotBlank(message = "Booking ID is required")
    @Column(name = "booking_id")
    private Long bookingId;
    @NotBlank(message = "Amount is required")
    @Column(name = "amount")
    private Double amount;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;


}
