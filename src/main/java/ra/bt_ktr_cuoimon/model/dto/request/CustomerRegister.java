package ra.bt_ktr_cuoimon.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegister {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;
    private String password;
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;
    @NotBlank(message = "Email không được để trống")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
            message = "Định dạng email không hợp lệ")
    private String email;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^(03|05|07|08|09|01[2|6|8|9])[0-9]{8}$",
            message = "Số điện thoại không đúng định dạng"
    )
    private String phone;
    private Boolean isLogin;
    @NotNull(message = "Trạng thái không được để trống")
    @Size(min = 1, message = "Phải có ít nhất một vai trò")
    private List<String> roles;
}
