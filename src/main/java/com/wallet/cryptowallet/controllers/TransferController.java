package com.wallet.cryptowallet.controllers;

import com.wallet.cryptowallet.models.Wallet;
import com.wallet.cryptowallet.services.TransactionService;
import com.wallet.cryptowallet.services.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class TransferController {

    private final TransactionService transactionService;
    private final WalletService walletService;

    public TransferController(TransactionService transactionService, WalletService walletService) {
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    @GetMapping("/transfer")
    public String showTransferForm(Model model) {
        // ✅ wallets with user preloaded for template usage
        List<Wallet> wallets = walletService.getAllWalletsWithUser();
        model.addAttribute("wallets", wallets);
        return "transfer";
    }

    @PostMapping("/transfer")
    public String processTransfer(@RequestParam Long fromWalletId,
                                  @RequestParam Long toWalletId,
                                  @RequestParam BigDecimal amount,
                                  Model model) {
        try {
            transactionService.transfer(fromWalletId, toWalletId, amount);
            model.addAttribute("success", true);
            model.addAttribute("amount", amount);
        } catch (Exception e) {
            model.addAttribute("error", "Transfer failed: " + e.getMessage());
        }

        // ✅ repopulate list with user preloaded
        List<Wallet> wallets = walletService.getAllWalletsWithUser();
        model.addAttribute("wallets", wallets);
        return "transfer";
    }
}
