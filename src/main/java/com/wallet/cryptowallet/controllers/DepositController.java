package com.wallet.cryptowallet.controllers;

import com.wallet.cryptowallet.models.Wallet;
import com.wallet.cryptowallet.repositories.WalletRepository;
import com.wallet.cryptowallet.services.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class DepositController {

    private final WalletRepository walletRepository;
    private final TransactionService transactionService;

    public DepositController(WalletRepository walletRepository,
                             TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    @GetMapping("/deposit")
    public String showDepositPage(Model model) {
        model.addAttribute("message", "");
        return "deposit";
    }

    @PostMapping("/deposit")
    public String processDeposit(@RequestParam Long walletId,
                                 @RequestParam String amount,
                                 Model model) {
        try {
            // Validate input
            if (walletId == null) {
                model.addAttribute("message", "❌ Error: Wallet ID cannot be empty");
                return "deposit";
            }

            if (amount == null || amount.trim().isEmpty()) {
                model.addAttribute("message", "❌ Error: Amount cannot be empty");
                return "deposit";
            }

            // Find the wallet
            Wallet wallet = walletRepository.findById(walletId)
                    .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + walletId));

            BigDecimal depositAmount = new BigDecimal(amount);

            // Validate amount
            if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("message", "❌ Error: Amount must be greater than zero");
                return "deposit";
            }

            // ✅ FIXED: Only call recordDeposit - it handles both balance update and transaction creation
            transactionService.recordDeposit(wallet, depositAmount);

            // ❌ REMOVED: walletRepository.save(wallet) - recordDeposit already saves the wallet

            model.addAttribute("message", "✅ Deposit successful! Amount: ₹" + amount + " added to Wallet ID: " + walletId);

        } catch (NumberFormatException e) {
            model.addAttribute("message", "❌ Error: Invalid amount format");
        } catch (Exception e) {
            model.addAttribute("message", "❌ Error: " + e.getMessage());
        }

        return "deposit";
    }
}