package lv.emendatus.Destiny_PropMan.domain.dto;

import jakarta.persistence.*;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.BookingStatus;

import java.sql.Timestamp;

public class BookingDTO {
    private Long id;
    private Property property;
    private Long tenantId;
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean isPaid;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public BookingDTO() {
    }

    public BookingDTO(Long id, Property property, Long tenantId, Timestamp startDate, Timestamp endDate, boolean isPaid, BookingStatus status) {
        this.id = id;
        this.property = property;
        this.tenantId = tenantId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
