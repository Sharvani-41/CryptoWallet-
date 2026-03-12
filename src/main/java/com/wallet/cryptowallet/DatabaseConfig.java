package com.wallet.cryptowallet;

import com.wallet.cryptowallet.models.User;
import com.wallet.cryptowallet.models.Wallet;
import com.wallet.cryptowallet.repositories.TransactionRepository;
import com.wallet.cryptowallet.repositories.UserRepository;
import com.wallet.cryptowallet.repositories.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DatabaseConfig {

    @Bean
    public CommandLineRunner initializeData(UserRepository userRepository,
                                            WalletRepository walletRepository,
                                            TransactionRepository transactionRepository) {
        return args -> {
            // ✳️ DO NOT delete anything here.
            // We only ensure demo users exist — and we DON'T touch other users.

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // --- Ensure Sahithi exists (upsert style) ---
            User sahithi = userRepository.findByUsername("sahithi").orElse(null);
            if (sahithi == null) {
                sahithi = new User("sahithi", "sahithi@example.com", encoder.encode("password123"));
                userRepository.save(sahithi);
            }
            // Ensure Sahithi's wallet exists
            Wallet w1 = walletRepository.findByUser(sahithi).orElse(null);
            if (w1 == null) {
                w1 = new Wallet(sahithi);
                w1.setBalance(BigDecimal.valueOf(5000));
                walletRepository.save(w1);
            }

            // --- Ensure Chandana exists (upsert style) ---
            User chandana = userRepository.findByUsername("chandana").orElse(null);
            if (chandana == null) {
                chandana = new User("chandana", "chandana@example.com", encoder.encode("password123"));
                userRepository.save(chandana);
            }
            // Ensure Chandana's wallet exists
            Wallet w2 = walletRepository.findByUser(chandana).orElse(null);
            if (w2 == null) {
                w2 = new Wallet(chandana);
                w2.setBalance(BigDecimal.valueOf(7000));
                walletRepository.save(w2);
            }

            System.out.println("✔ Demo users ensured. No data was deleted. New registrations will persist.");
        };
    }
}
