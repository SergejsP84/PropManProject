package lv.emendatus.Destiny_PropMan.domain.dto.authentication;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserResponse {
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    public UserResponse(String username, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}