package com.wallet.cryptowallet.controllers;

import com.wallet.cryptowallet.models.User;
import com.wallet.cryptowallet.models.Wallet;
import com.wallet.cryptowallet.repositories.UserRepository;
import com.wallet.cryptowallet.services.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class WithdrawController {

    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public WithdrawController(UserRepository userRepository,
                              TransactionService transactionService) {
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }

    @GetMapping("/withdraw")
    public String showWithdrawForm(Authentication authentication, Model model) {
        // find the current user
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Wallet wallet = user.getWallet();
        if (wallet == null) {
            // This should not happen because you create a wallet on register,
            // but handle gracefully anyway.
            model.addAttribute("error", "Wallet not found for user.");
            return "withdraw";
        }

        model.addAttribute("walletId", wallet.getId());
        return "withdraw";
    }

    @PostMapping("/withdraw")
    @Transactional
    public String processWithdraw(@RequestParam("amount") BigDecimal amount,
                                  Authentication authentication,
                                  Model model) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Wallet wallet = user.getWallet();
        if (wallet == null) {
            model.addAttribute("error", "Wallet not found for user.");
            return "withdraw";
        }

        try {
            // Validations are handled inside the service too
            transactionService.recordWithdrawal(wallet, amount);
            model.addAttribute("success", "Withdrawal successful!");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            model.addAttribute("error", "Withdrawal failed: " + ex.getMessage());
        }

        // Always repopulate walletId for the form after POST
        model.addAttribute("walletId", wallet.getId());
        return "withdraw";
    }
}
