package com.wallet.cryptowallet.repositories;

import com.wallet.cryptowallet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // exact (case-sensitive) finders
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // case-insensitive finders (used for login)
    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
