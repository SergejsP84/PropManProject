package lv.emendatus.Destiny_PropMan.domain.dto.authentication;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserResponse {
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    private String token; // Add this field for JWT

    public UserResponse(String username, Collection<? extends GrantedAuthority> authorities, String token) {
        this.username = username;
        this.authorities = authorities;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getToken() {
        return token;
    }
}