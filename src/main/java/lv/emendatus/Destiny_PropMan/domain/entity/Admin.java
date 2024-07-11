package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @NotBlank(message = "Login is required")
    @Column(name = "login", unique = true)
    private String login;

    @NotBlank(message = "Password is required")
    @Column(name = "password")
    private String password;

    @ElementCollection(targetClass = SimpleGrantedAuthority.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "admin_authorities", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "authority")
    private Collection<? extends GrantedAuthority> authorities;

//    @Builder.Default
    @Column(name = "known_ips")
    private List<String> knownIps;

   // @NotBlank(message = "Email is required")
    @Column(name = "email")
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return Objects.equals(id, admin.id) && Objects.equals(name, admin.name) && Objects.equals(login, admin.login) && Objects.equals(password, admin.password) && Objects.equals(authorities, admin.authorities) && Objects.equals(knownIps, admin.knownIps) && Objects.equals(email, admin.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, login, password, authorities, knownIps, email);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", authorities=" + authorities +
                ", knownIps=" + knownIps +
                ", email='" + email + '\'' +
                '}';
    }
}