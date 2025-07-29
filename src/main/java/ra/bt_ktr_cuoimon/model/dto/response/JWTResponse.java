package ra.bt_ktr_cuoimon.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JWTResponse {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Boolean isLogin;
    private Boolean status;
    private Collection<? extends GrantedAuthority> authorities;
    private String token;
}
