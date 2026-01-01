package _v3r.project.common.auth.model;

import _v3r.project.user.domain.User;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String loginId;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getUsername() {
        return loginId;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    public static CustomUserDetails from(User user) {
        return new CustomUserDetails(
                user.getUserId(),
                user.getLoginId(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("USER"))
        );
    }

}

