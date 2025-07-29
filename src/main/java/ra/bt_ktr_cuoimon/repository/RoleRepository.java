package ra.bt_ktr_cuoimon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.bt_ktr_cuoimon.model.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(String roleName);
}
