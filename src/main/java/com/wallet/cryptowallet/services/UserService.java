package com.wallet.cryptowallet.services;

import com.wallet.cryptowallet.models.User;
import java.util.Optional;

public interface UserService {
    User findByUsername(String username);
    User save(User user);
    Optional<User> findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}