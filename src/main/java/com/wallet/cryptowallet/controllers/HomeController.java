package com.wallet.cryptowallet.controllers;

import com.wallet.cryptowallet.models.User;
import com.wallet.cryptowallet.models.Wallet;
import com.wallet.cryptowallet.services.UserService;  // ✅ Correct import
import com.wallet.cryptowallet.services.WalletService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;
    private final WalletService walletService;

    public HomeController(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }

    @GetMapping("/home")
    public String showDashboard(Model model) {
        // Get currently logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Find user by username
        User user = userService.findByUsername(username);

        if (user != null) {
            // Find user's wallet
            Wallet wallet = walletService.findByUser(user);

            // Add data to model
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("wallet", wallet);
            model.addAttribute("balance", wallet != null ? wallet.getBalance() : 0);
        } else {
            // Fallback if user not found
            model.addAttribute("username", username);
            model.addAttribute("email", "Not available");
            model.addAttribute("wallet", null);
            model.addAttribute("balance", 0);
        }

        return "home";
    }
}