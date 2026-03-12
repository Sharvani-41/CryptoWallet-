package com.wallet.cryptowallet.services;

import com.wallet.cryptowallet.models.User;
import com.wallet.cryptowallet.models.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletService {
    Wallet findByUser(User user);
    void save(Wallet wallet);
    List<Wallet> getAllWallets();           // keeps old method (used elsewhere)
    Optional<Wallet> findById(Long id);

    // NEW: guarantees wallet.user is loaded (fixes Thymeleaf lazy error)
    List<Wallet> getAllWalletsWithUser();
}
