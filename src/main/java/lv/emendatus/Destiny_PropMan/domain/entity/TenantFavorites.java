package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "tenant_favorites")
public class TenantFavorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "favorites")
    private List<Long> favoritePropertyIDs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantFavorites that = (TenantFavorites) o;
        return Objects.equals(id, that.id) && Objects.equals(tenantId, that.tenantId) && Objects.equals(favoritePropertyIDs, that.favoritePropertyIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, favoritePropertyIDs);
    }

    @Override
    public String toString() {
        return "TenantFavorites{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", favoritePropertyIDs=" + favoritePropertyIDs +
                '}';
    }
}
