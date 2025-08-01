package ra.bt_ktr_cuoimon.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.bt_ktr_cuoimon.model.dto.request.CustomerLogin;
import ra.bt_ktr_cuoimon.model.dto.request.CustomerRegister;
import ra.bt_ktr_cuoimon.model.dto.response.JWTResponse;
import ra.bt_ktr_cuoimon.model.entity.BlacklistedToken;
import ra.bt_ktr_cuoimon.model.entity.Customer;
import ra.bt_ktr_cuoimon.model.entity.Role;
import ra.bt_ktr_cuoimon.repository.BlacklistedTokenRepository;
import ra.bt_ktr_cuoimon.repository.CustomerRepository;
import ra.bt_ktr_cuoimon.repository.RoleRepository;
import ra.bt_ktr_cuoimon.security.jwt.JWTProvider;
import ra.bt_ktr_cuoimon.security.principal.CustomerPrincipal;
import ra.bt_ktr_cuoimon.service.CustomerService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JWTProvider jwtProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Override
    public Customer register(CustomerRegister customerRegister) {
        if (customerRepository.existsByUsername(customerRegister.getUsername())) {
            throw new IllegalArgumentException("Tên tài khoản đã tồn tại");
        }
        if (customerRepository.existsByEmail(customerRegister.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if (customerRepository.existsByPhone(customerRegister.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }

        Customer customer = Customer.builder()
                .username(customerRegister.getUsername())
                .password(passwordEncoder.encode(customerRegister.getPassword()))
                .fullName(customerRegister.getFullName())
                .email(customerRegister.getEmail())
                .phone(customerRegister.getPhone())
                .isLogin(false)
                .status(true)
                .roles(mapRoleStringToRole(customerRegister.getRoles()))
                .build();
        return customerRepository.save(customer);
    }

    @Override
    public JWTResponse login(CustomerLogin customerLogin) {
        Authentication authentication = null;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customerLogin.getUsername(),customerLogin.getPassword()));
        }catch(AuthenticationException e){
            throw new IllegalArgumentException("Sai username hoặc password!");
        }

        CustomerPrincipal customerDetails = (CustomerPrincipal) authentication.getPrincipal();
        String token = jwtProvider.generateToken(customerDetails.getUsername());

        return JWTResponse.builder()
                .username(customerDetails.getUsername())
                .fullName(customerDetails.getFullName())
                .status(customerDetails.isEnabled())
                .email(customerDetails.getEmail())
                .phone(customerDetails.getPhone())
                .isLogin(true)
                .authorities(customerDetails.getAuthorities())
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token không được để trống");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (blacklistedTokenRepository.findByToken(token).isPresent()) {
            throw new IllegalArgumentException("Token đã bị thu hồi trước đó");
        }
        if (!jwtProvider.validateToken(token)) {
            throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn");
        }
        blacklistedTokenRepository.save(BlacklistedToken.builder().token(token).build());
    }

    private List<Role> mapRoleStringToRole(List<String> roles) {
        List<Role> roleList = new ArrayList<>();

        if(roles!=null && !roles.isEmpty()){
            roles.forEach(role->{
                switch (role){
                    case "ROLE_ADMIN":
                        roleList.add(roleRepository.findByRoleName(role).orElseThrow(()-> new NoSuchElementException("Khong ton tai ROLE_ADMIN")));
                        break;
                    case "ROLE_CUSTOMER":
                        roleList.add(roleRepository.findByRoleName(role).orElseThrow(()-> new NoSuchElementException("Khong ton tai ROLE_CUSTOMER")));
                        break;
                    case "ROLE_MODERATOR":
                        roleList.add(roleRepository.findByRoleName(role).orElseThrow(()-> new NoSuchElementException("Khong ton tai ROLE_MODERATOR")));
                        break;
                    default:
                        roleList.add(roleRepository.findByRoleName("ROLE_CUSTOMER").orElseThrow(()-> new NoSuchElementException("Khong ton tai ROLE_CUSTOMER")));
                }
            });
        }else{
            roleList.add(roleRepository.findByRoleName("ROLE_CUSTOMER").orElseThrow(()-> new NoSuchElementException("Khong ton tai ROLE_CUSTOMER")));
        }
        return roleList;
    }
}
