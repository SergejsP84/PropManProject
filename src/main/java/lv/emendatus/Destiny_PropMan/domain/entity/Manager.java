package lv.emendatus.Destiny_PropMan.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "managers")
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ManagerType type;
    @Column(name = "manager_name")
    private String managerName;
//    @Column(name = "description", length = 1084)
    @Column(name = "description")
    private String description;
    @Column(name = "is_active")
    private boolean isActive;
    @Column(name = "join_date")
    private Timestamp joinDate;
    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String password;
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Property> properties;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return isActive == manager.isActive && Objects.equals(id, manager.id) && Objects.equals(type, manager.type) && Objects.equals(managerName, manager.managerName) && Objects.equals(description, manager.description) && Objects.equals(joinDate, manager.joinDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, managerName, description, isActive, joinDate);
    }

    @Override
    public String toString() {
        return "Manager{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", managerName='" + managerName + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", joinDate=" + joinDate +
                ", login=" + login +
                '}';
    }
}
