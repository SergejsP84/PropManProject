package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "amount")
    private double amount;
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
    @Column(name = "expense_category")
    private String expenseCategory;
    @Column(name = "due_date")
    private Timestamp dueDate;
    @Column(name = "recipient")
    private String recipient;
    @Column(name = "recipient_IBAN")
    private String recipientIBAN;
    @Column(name = "is_paid")
    private boolean isPaid;
    @Column(name = "issued_at")
    private Timestamp issuedAt;

    public Bill() {
    }

    public Bill(Long id, double amount, Currency currency, Property property, String expenseCategory, Timestamp dueDate, String recipient, String recipientIBAN, boolean isPaid, Timestamp issuedAt) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return Double.compare(bill.amount, amount) == 0 && isPaid == bill.isPaid && Objects.equals(id, bill.id) && Objects.equals(currency, bill.currency) && Objects.equals(property, bill.property) && Objects.equals(expenseCategory, bill.expenseCategory) && Objects.equals(dueDate, bill.dueDate) && Objects.equals(recipient, bill.recipient) && Objects.equals(recipientIBAN, bill.recipientIBAN) && Objects.equals(issuedAt, bill.issuedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, currency, property, expenseCategory, dueDate, recipient, recipientIBAN, isPaid, issuedAt);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", amount=" + amount +
                ", currency=" + currency +
                ", property=" + property +
                ", expenseCategory='" + expenseCategory + '\'' +
                ", dueDate=" + dueDate +
                ", recipient='" + recipient + '\'' +
                ", recipientIBAN='" + recipientIBAN + '\'' +
                ", isPaid=" + isPaid +
                ", issuedAt=" + issuedAt +
                '}';
    }
}