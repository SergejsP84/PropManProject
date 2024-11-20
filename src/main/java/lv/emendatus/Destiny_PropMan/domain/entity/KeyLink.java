package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "key_links")
public class KeyLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Just your plain old entity ID here

    @Column(name = "user_id")
    private Long userId;
    // ID of the user whose key is stored in a file

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private UserType userType;
    // Type of the user whose key is stored in a file, TENANT or MANAGER

    @Column(name = "file_number")
    private Integer fileNumber;
    // "vault*.dps file number where the record is stored

}
