package ra.bt_ktr_cuoimon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.bt_ktr_cuoimon.model.entity.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByUsername(String username);
}
