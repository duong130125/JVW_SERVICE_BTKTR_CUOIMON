package ra.bt_ktr_cuoimon.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "customers")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Customer {
    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username", length = 100, nullable = false, unique = true)
    private String username;
    @Column(name = "full_name", length = 70)
    private String fullName;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String password;
    @Column(nullable = false, unique = true, length = 50)
    private String email;
    @Column(nullable = false, unique = true, length = 20)
    private String phone;
    @Column(name = "is_login", nullable = false)
    private Boolean isLogin;
    @Column(nullable = false)
    private Boolean status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "customer_role",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
}
