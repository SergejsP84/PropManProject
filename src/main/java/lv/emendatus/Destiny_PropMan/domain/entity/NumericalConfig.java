package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "numerical_config")
public class NumericalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "\"value\"") // supposed to be 1 by default, change to any value between 0 and 1 to set a discount
    private Double value;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    public NumericalConfig() {
    }

    public NumericalConfig(Long id, String name, Double value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumericalConfig that = (NumericalConfig) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, value);
    }

    @Override
    public String toString() {
        return "NumericalConfig{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
