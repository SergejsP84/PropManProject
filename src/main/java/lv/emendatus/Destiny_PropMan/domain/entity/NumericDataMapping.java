package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.util.SecretKeyMapConverter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "numerical_data_mapping")
public class NumericDataMapping implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "map")
    @Convert(converter = SecretKeyMapConverter.class)
    private Map<Long, Map<UserType, Map<Boolean, SecretKey>>> map;
    // Level 1 Long key: user entity ID
    // Level 2 UserType key: type of the user
    // Level 3 Boolean key: true for a Card Number, false for a CVV code
    // SecretKeySpec value: secret key value


    @Override
    public String toString() {
        return "NumericDataMapping{" +
                "id=" + id +
                ", map=" + map +
                '}';
    }
}
