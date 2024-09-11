package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimStatus;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.ClaimantType;

import java.sql.Timestamp;
import java.util.Objects;

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

    @NotNull(message = "Booking ID is required")
    @Column(name = "booking_id")
    private Long bookingId;

    @NotNull(message = "Description is required")
    @Column(name = "description", length = 9000)
    private String description;

    @NotNull(message = "Claimant type is required")
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

    @Column(name = "resolution", length = 9000)
    private String resolution;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Claim claim = (Claim) o;
        return Objects.equals(id, claim.id) && Objects.equals(bookingId, claim.bookingId) && Objects.equals(description, claim.description) && claimantType == claim.claimantType && claimStatus == claim.claimStatus && Objects.equals(admitted, claim.admitted) && Objects.equals(createdAt, claim.createdAt) && Objects.equals(resolvedAt, claim.resolvedAt) && Objects.equals(resolution, claim.resolution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookingId, description, claimantType, claimStatus, admitted, createdAt, resolvedAt, resolution);
    }

    @Override
    public String toString() {
        return "Claim{" +
                "id=" + id +
                ", bookingId=" + bookingId +
                ", description='" + description + '\'' +
                ", claimantType=" + claimantType +
                ", claimStatus=" + claimStatus +
                ", admitted=" + admitted +
                ", createdAt=" + createdAt +
                ", resolvedAt=" + resolvedAt +
                ", resolution='" + resolution + '\'' +
                '}';
    }
}
