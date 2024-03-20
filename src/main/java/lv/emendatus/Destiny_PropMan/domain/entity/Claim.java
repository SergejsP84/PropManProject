package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimantType;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "description")
    private String description;

    @Column(name = "claimant")
    @Enumerated(EnumType.STRING)
    private ClaimantType claimantType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ClaimStatus claimStatus;

    @Column(name = "admitted") // true if the alleged party in breach admits that the claim is justified
    private Boolean admitted;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "resolved_at")
    private Timestamp resolvedAt;

    @Column(name = "resolution")
    private String resolution;
}
