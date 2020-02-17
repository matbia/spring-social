package com.matbia.repository;

import com.matbia.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
    /*@Query(value = "SELECT * FROM roles WHERE ?1 MEMBER OF users", nativeQuery = true)
    List<Role> findByUsersContains(User user);*/
}
