package ml.ridex.ridexapi.model.dao;

import lombok.Data;
import ml.ridex.ridexapi.enums.Role;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class User implements UserDetails {
    @Id
    private String id;

    private String phone;

    private String password;

    private List<Role> roles;

    private Collection<? extends GrantedAuthority> authorities;

    private long exp;

    private Boolean enabled;

    public User(String phone, String password, List<Role> roles, long exp, Boolean enabled) {
        this.phone = phone;
        this.password = password;
        this.roles = roles;
        this.exp = exp;
        this.enabled = enabled;
        this.authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
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
//        if(password == null || password =="")
//            return false;
//        if(enabled && (role == Role.DRIVER || role == Role.PASSENGER))
//            return exp > Instant.now().getEpochSecond();
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
