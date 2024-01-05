package lv.emendatus.Destiny_PropMan.domain.dto;

import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;

import java.sql.Timestamp;

public class BillDTO {
    private Long id;
    private double amount;
    private Currency currency;
    private Property property;
    private String expenseCategory;
    private Timestamp dueDate;
    private String recipient;
    private String recipientIBAN;
    private boolean isPaid;
    private Timestamp issuedAt;

    public BillDTO() {
    }

    public BillDTO(Long id, double amount, Currency currency, Property property, String expenseCategory, Timestamp dueDate, String recipient, String recipientIBAN, boolean isPaid, Timestamp issuedAt) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.property = property;
        this.expenseCategory = expenseCategory;
        this.dueDate = dueDate;
        this.recipient = recipient;
        this.recipientIBAN = recipientIBAN;
        this.isPaid = isPaid;
        this.issuedAt = issuedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipientIBAN() {
        return recipientIBAN;
    }

    public void setRecipientIBAN(String recipientIBAN) {
        this.recipientIBAN = recipientIBAN;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public Timestamp getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Timestamp issuedAt) {
        this.issuedAt = issuedAt;
    }
}
