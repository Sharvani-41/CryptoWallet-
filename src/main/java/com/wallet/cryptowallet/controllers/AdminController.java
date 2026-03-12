package com.wallet.cryptowallet.controllers;

import com.wallet.cryptowallet.models.User;
import com.wallet.cryptowallet.models.Wallet;
import com.wallet.cryptowallet.repositories.UserRepository;
import com.wallet.cryptowallet.repositories.WalletRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository,
                           WalletRepository walletRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Show form page
    @GetMapping("/create-user")
    public String createUserForm() {
        return "admin_create_user"; // template name
    }

    // Handle form submission
    @PostMapping("/create-user")
    public String createUserSubmit(@RequestParam String username,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam(required = false, defaultValue = "0") String initialBalance,
                                   Model model) {

        // ✅ FIXED: Use proper repository methods
        if (userRepository.existsByEmail(email) || userRepository.existsByUsername(username)) {
            model.addAttribute("error", "User or email already exists!");
            return "admin_create_user";
        }

        try {
            // ✅ FIXED: Create user properly (adjust based on your User class constructor)
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));

            User savedUser = userRepository.save(user);

            // Create wallet for the user
            Wallet wallet = new Wallet(savedUser);
            wallet.setBalance(new BigDecimal(initialBalance));
            walletRepository.save(wallet);

            model.addAttribute("message", "✅ User created: " + username);

        } catch (Exception e) {
            model.addAttribute("error", "Error creating user: " + e.getMessage());
        }

        return "admin_create_user";
    }
}