package ra.bt_ktr_cuoimon.service;

import ra.bt_ktr_cuoimon.model.dto.request.CustomerLogin;
import ra.bt_ktr_cuoimon.model.dto.request.CustomerRegister;
import ra.bt_ktr_cuoimon.model.dto.response.JWTResponse;
import ra.bt_ktr_cuoimon.model.entity.Customer;

public interface CustomerService {
    Customer register(CustomerRegister customerRegister);

    JWTResponse login(CustomerLogin customerLogin);
}
