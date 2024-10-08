package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "property_ratings")
public class PropertyRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Tenant ID is required")
    @Column(name = "tenant_id")
    private Long tenantId;

    @NotNull(message = "Property ID is required")
    @Column(name = "property_id")
    private Long propertyId;

    @NotNull(message = "Booking ID is required")
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "rating")
    @Min(1)
    @Max(5)
    private Integer rating;
}
