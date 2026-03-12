package com.wallet.cryptowallet.services;

import com.wallet.cryptowallet.models.User;
import com.wallet.cryptowallet.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        String input = usernameOrEmail == null ? "" : usernameOrEmail.trim();

        User user = userRepository.findByUsernameIgnoreCase(input)
                .orElseGet(() -> userRepository.findByEmailIgnoreCase(input)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + input)));

        // Return Spring Security user with BCrypt password
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .roles("USER")
                .build();
    }
}
