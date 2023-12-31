package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

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
    @Column(name = "description", length = 1084)
    private String description;
    @Column(name = "is_active")
    private boolean isActive;
    @Column(name = "join_date")
    private Timestamp joinDate;
    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String password;
    @OneToMany(mappedBy = "manager") // the variable name from the Property class
    private Set<Property> properties;

    public Manager() {
    }

    public Manager(Long id, ManagerType type, String managerName, String description, boolean isActive, Timestamp joinDate, String login, String password) {
        this.id = id;
        this.type = type;
        this.managerName = managerName;
        this.description = description;
        this.isActive = isActive;
        this.joinDate = joinDate;
        this.login = login;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ManagerType getType() {
        return type;
    }

    public void setType(ManagerType type) {
        this.type = type;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Timestamp joinDate) {
        this.joinDate = joinDate;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public void setProperties(Set<Property> properties) {
        this.properties = properties;
    }

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
