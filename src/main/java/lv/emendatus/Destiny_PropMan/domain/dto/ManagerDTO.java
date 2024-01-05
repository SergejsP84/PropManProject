package lv.emendatus.Destiny_PropMan.domain.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ManagerType;

import java.sql.Timestamp;
import java.util.Set;

public class ManagerDTO {
    private Long id;
    @Enumerated(EnumType.STRING)
    private ManagerType type;
    private String managerName;
    private String description;
    private boolean isActive;
    private Timestamp joinDate;
    private String login;
    private String password;
    private Set<Property> properties;

    public ManagerDTO() {
    }

    public ManagerDTO(Long id, ManagerType type, String managerName, String description, boolean isActive, Timestamp joinDate, String login, String password, Set<Property> properties) {
        this.id = id;
        this.type = type;
        this.managerName = managerName;
        this.description = description;
        this.isActive = isActive;
        this.joinDate = joinDate;
        this.login = login;
        this.password = password;
        this.properties = properties;
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
}
