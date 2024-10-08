package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    @Column(name = "added_at")
    private Timestamp addedAt;


}