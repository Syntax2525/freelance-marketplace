// com.freelance.repository.UserRepository.java
package com.freelance.repository;

import com.freelance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Optional: useful for admin or search features
    // List<User> findByFullNameContainingIgnoreCase(String namePart);

}