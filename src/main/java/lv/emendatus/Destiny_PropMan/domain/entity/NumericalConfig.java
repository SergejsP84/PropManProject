package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.NumConfigType;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "numerical_config")
public class NumericalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "\"value\"") // supposed to be 1 by default, change to any value between 0 and 1 to set a discount
    // the comment above only applies to discounts, other uses can be found.
    private Double value;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NumConfigType type;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

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
