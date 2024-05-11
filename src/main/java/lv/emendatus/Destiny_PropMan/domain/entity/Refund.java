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
@Table(name = "refunds")
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Tenant ID is required")
    @Column(name = "tenant_id")
    private Long tenantId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Refund refund = (Refund) o;
        return Objects.equals(id, refund.id) && Objects.equals(tenantId, refund.tenantId) && Objects.equals(bookingId, refund.bookingId) && Objects.equals(amount, refund.amount) && Objects.equals(createdAt, refund.createdAt) && Objects.equals(currency, refund.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, bookingId, amount, createdAt, currency);
    }

    @Override
    public String toString() {
        return "Refund{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", bookingId=" + bookingId +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                ", currency=" + currency +
                '}';
    }
}
