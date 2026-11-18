package com.mooc.repository;

import com.mooc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import com.mooc.model.User.Role;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByRole(Role role);
}
