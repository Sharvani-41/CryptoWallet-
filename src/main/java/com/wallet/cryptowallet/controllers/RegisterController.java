package com.wallet.cryptowallet.controllers;

import com.wallet.cryptowallet.models.User;
import com.wallet.cryptowallet.models.Wallet;
import com.wallet.cryptowallet.repositories.UserRepository;
import com.wallet.cryptowallet.repositories.WalletRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserRepository userRepository,
                              WalletRepository walletRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {

        // normalize inputs
        if (user.getUsername() != null) user.setUsername(user.getUsername().trim());
        if (user.getEmail() != null) user.setEmail(user.getEmail().trim().toLowerCase());

        // validations
        if (userRepository.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already registered");
        }
        if (result.hasErrors()) {
            return "register";
        }

        // encode plain password (from @Transient field)
        String rawPassword = user.getPassword();
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        // save user and create wallet
        User savedUser = userRepository.save(user);
        Wallet wallet = new Wallet(savedUser);
        walletRepository.save(wallet);

        // link wallet to user (optional but nice to have)
        savedUser.setWallet(wallet);
        userRepository.save(savedUser);

        // success → go to login
        return "redirect:/login?success";
    }
}
