package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Entity
@Table(name = "property_discounts")
public class PropertyDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Property property;
    @NotBlank(message = "Percentage is required")
    @Column(name = "percentage")
    private Double percentage; // -100 to infinity, - for discount, + for surcharge
    @NotBlank(message = "Discount period start must be specified")
    @Column(name = "period_start")
    private LocalDate periodStart;
    @NotBlank(message = "Discount period end must be specified")
    @Column(name = "period_end")
    private LocalDate periodEnd;
    @Column(name = "created_at")
    private Timestamp createdAt;
}
