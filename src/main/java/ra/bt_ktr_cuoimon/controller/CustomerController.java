package ra.bt_ktr_cuoimon.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.bt_ktr_cuoimon.model.dto.request.CustomerLogin;
import ra.bt_ktr_cuoimon.model.dto.request.CustomerRegister;
import ra.bt_ktr_cuoimon.model.dto.response.APIResponse;
import ra.bt_ktr_cuoimon.model.dto.response.JWTResponse;
import ra.bt_ktr_cuoimon.model.entity.Customer;
import ra.bt_ktr_cuoimon.service.CustomerService;

@RestController
@RequestMapping("/api/v1/auth")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<Customer>> registerCustomer(@RequestBody @Valid CustomerRegister customerRegister) {
        return new ResponseEntity<>(
            new APIResponse<>
                (true, "Đăng ký tài khoản thành công!", customerService.register(customerRegister), HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<JWTResponse>> login(@RequestBody @Valid CustomerLogin customerLogin) {
        return new ResponseEntity<>(
            new APIResponse<>
                (true, "Đăng nhập tài khoản thành công!", customerService.login(customerLogin), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        customerService.logout(token);
        return ResponseEntity.ok("Logout thành công và token đã bị thu hồi!");
    }
}
