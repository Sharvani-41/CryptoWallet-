package com.wallet.cryptowallet.controllers;

import com.wallet.cryptowallet.models.Transaction;
import com.wallet.cryptowallet.models.User;
import com.wallet.cryptowallet.models.Wallet;
import com.wallet.cryptowallet.repositories.TransactionRepository;
import com.wallet.cryptowallet.services.UserService;
import com.wallet.cryptowallet.services.WalletService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class HistoryController {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final WalletService walletService;

    public HistoryController(TransactionRepository transactionRepository,
                             UserService userService,
                             WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.walletService = walletService;
    }

    @GetMapping("/history")
    public String showTransactionHistory(Model model) {
        // who is logged in?
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userService.findByUsername(username);
        List<Transaction> transactions;

        if (user != null) {
            Wallet wallet = walletService.findByUser(user);
            if (wallet != null) {
                // ✅ only this wallet's transactions
                transactions = transactionRepository.findByWalletOrderByTimestampDesc(wallet);
                model.addAttribute("walletId", wallet.getId());
                model.addAttribute("balance", wallet.getBalance());
            } else {
                transactions = Collections.emptyList();
            }
        } else {
            transactions = Collections.emptyList();
        }

        model.addAttribute("transactions", transactions);
        return "history";
    }
}
