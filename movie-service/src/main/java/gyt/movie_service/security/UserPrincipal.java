package gyt.movie_service.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements Principal {
    private String uuid;
    private String email;
    private String fullName;
    private String role;

    public UserPrincipal(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
        this.role = "USER";
    }

    @Override
    public String getName() {
        return email;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
