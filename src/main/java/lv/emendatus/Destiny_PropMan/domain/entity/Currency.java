package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "designation")
    private String designation;

    @OneToMany(mappedBy = "currency")
    private Set<Bill> bills;

    @OneToMany(mappedBy = "currency")
    private Set<NumericalConfig> numericalConfigs;

    public Currency() {
    }


    public Currency(Long id, String designation) {
        this.id = id;
        this.designation = designation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(id, currency.id) && Objects.equals(designation, currency.designation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, designation);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", designation='" + designation + '\'' +
                '}';
    }
}