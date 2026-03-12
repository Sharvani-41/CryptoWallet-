package com.wallet.cryptowallet.controllers;

import com.wallet.cryptowallet.models.User;
import com.wallet.cryptowallet.models.Wallet;
import com.wallet.cryptowallet.services.TransactionService;
import com.wallet.cryptowallet.services.UserService;
import com.wallet.cryptowallet.services.WalletService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final WalletService walletService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService,
                                 WalletService walletService,
                                 UserService userService) {
        this.transactionService = transactionService;
        this.walletService = walletService;
        this.userService = userService;
    }

    @GetMapping("/transactions")
    public String showTransactionPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user != null) {
            Wallet userWallet = walletService.findByUser(user);
            model.addAttribute("userWallet", userWallet);
            model.addAttribute("userWalletId", userWallet != null ? userWallet.getId() : null);
        }

        // Fetch wallets with user preloaded (prevents LazyInitializationException)
        List<Wallet> allWallets = walletService.getAllWalletsWithUser();

        if (user != null && user.getWallet() != null) {
            allWallets.removeIf(w -> w.getId().equals(user.getWallet().getId()));
        }

        model.addAttribute("wallets", allWallets);
        model.addAttribute("message", "");
        return "transactions";
    }

    @PostMapping("/transactions")
    public String processTransaction(@RequestParam Long toWalletId,
                                     @RequestParam String amount,
                                     Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (user == null) {
                model.addAttribute("message", "❌ Error: User not found");
                setupTransactionPage(model);
                return "transactions";
            }

            Wallet fromWallet = walletService.findByUser(user);
            if (fromWallet == null) {
                model.addAttribute("message", "❌ Error: Wallet not found for user");
                setupTransactionPage(model);
                return "transactions";
            }

            if (fromWallet.getId().equals(toWalletId)) {
                model.addAttribute("message", "❌ Error: Cannot send money to your own wallet!");
                setupTransactionPage(model);
                return "transactions";
            }

            BigDecimal transferAmount = new BigDecimal(amount);
            transactionService.transfer(fromWallet.getId(), toWalletId, transferAmount);
            model.addAttribute("message", "✅ Transfer successful! Amount: ₹" + amount);

        } catch (Exception e) {
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }

        setupTransactionPage(model);
        return "transactions";
    }

    private void setupTransactionPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user != null) {
            Wallet userWallet = walletService.findByUser(user);
            model.addAttribute("userWallet", userWallet);
            model.addAttribute("userWalletId", userWallet != null ? userWallet.getId() : null);
        }

        List<Wallet> allWallets = walletService.getAllWalletsWithUser();

        if (user != null && user.getWallet() != null) {
            allWallets.removeIf(w -> w.getId().equals(user.getWallet().getId()));
        }

        model.addAttribute("wallets", allWallets);
    }
}
