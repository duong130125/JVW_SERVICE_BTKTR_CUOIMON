package ra.bt_ktr_cuoimon.security.principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ra.bt_ktr_cuoimon.model.entity.Customer;
import ra.bt_ktr_cuoimon.model.entity.Role;
import ra.bt_ktr_cuoimon.repository.CustomerRepository;

import java.util.Collection;
import java.util.List;

@Service
public class CustomerDetailsService implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Khong ton tai username"));
        return CustomerPrincipal.builder()
                .username(customer.getUsername())
                .password(customer.getPassword())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .status(customer.getStatus())
                .authorities(mapRoleToGrandAuthorities(customer.getRoles()))
                .build();
    }

    private Collection<? extends GrantedAuthority> mapRoleToGrandAuthorities(List<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).toList();
    }
}