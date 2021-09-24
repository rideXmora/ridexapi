package ml.ridex.ridexapi.model.dao;

import lombok.Data;
import ml.ridex.ridexapi.enums.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

@Document
@Data
public class User implements UserDetails {
    @Id
    private String id;
    @Indexed
    private String phone;

    private Role role;

    private String password;

    private long exp;

    private Boolean enabled;

    public User(String phone, Role role, String password, long exp, Boolean enabled) {
        this.phone = phone;
        this.role = role;
        this.password = password;
        this.exp = exp;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return phone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        if (password == null || password == "")
            return false;
        if(enabled && (role == Role.DRIVER || role == Role.PASSENGER))
            return exp > Instant.now().getEpochSecond();
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
