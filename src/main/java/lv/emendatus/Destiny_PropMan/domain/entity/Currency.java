package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Currency designation is required")
    @Column(name = "designation")
    private String designation;

    @Column(name = "is_base_currency")
    private Boolean isBaseCurrency;

    @Column(name = "rate_to_base")
    private Double rateToBase;

    @OneToMany(mappedBy = "currency") // not sure if needed, retained for possible further use
    private Set<Bill> bills;

    @OneToMany(mappedBy = "currency") // not sure if needed, retained for possible further use
    private Set<NumericalConfig> numericalConfigs;

    @OneToMany(mappedBy = "currency") // not sure if needed, retained for possible further use
    private Set<Refund> refunds;

    @OneToMany(mappedBy = "currency") // not sure if needed, retained for possible further use
    private Set<Payout> payouts;

    @OneToMany(mappedBy = "preferredCurrency") // not sure if needed, retained for possible further use
    private Set<Tenant> tenants;

    @OneToMany(mappedBy = "currency") // not sure if needed, retained for possible further use
    private Set<TenantPayment> tenantPayments;

    public Currency(Long id, String designation) {
        this.id = id;
        this.designation = designation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(id, currency.id) && Objects.equals(designation, currency.designation) && Objects.equals(isBaseCurrency, currency.isBaseCurrency) && Objects.equals(rateToBase, currency.rateToBase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, designation, isBaseCurrency, rateToBase);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", designation='" + designation + '\'' +
                ", isBaseCurrency=" + isBaseCurrency +
                ", rateToBase=" + rateToBase +
                '}';
    }
}