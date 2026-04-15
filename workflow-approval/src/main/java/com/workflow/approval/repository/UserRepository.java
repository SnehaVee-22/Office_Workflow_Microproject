package com.workflow.approval.repository;

import com.workflow.approval.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    List<User> findByRole(User.Role role);
    List<User> findByRoleAndActive(User.Role role, boolean active);
    @Query("SELECT u FROM User u WHERE u.manager.id = :managerId")
    List<User> findByManagerId(Long managerId);
    List<User> findByDepartment(String department);
}
