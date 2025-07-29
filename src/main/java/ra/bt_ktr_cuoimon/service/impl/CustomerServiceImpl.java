package ra.bt_ktr_cuoimon.service.impl;

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
import ra.bt_ktr_cuoimon.model.entity.Customer;
import ra.bt_ktr_cuoimon.model.entity.Role;
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


    @Override
    public Customer register(CustomerRegister customerRegister) {
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
            log.error("Sai username hoặc password!");
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
