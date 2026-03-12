package com.wallet.cryptowallet.services;

import com.wallet.cryptowallet.models.User;
import com.wallet.cryptowallet.models.Wallet;
import com.wallet.cryptowallet.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Wallet findByUser(User user) {
        if (user == null) return null;
        return walletRepository.findByUser(user).orElse(null);
    }

    @Override
    public void save(Wallet wallet) {
        if (wallet != null) walletRepository.save(wallet);
    }

    @Override
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return walletRepository.findById(id);
    }

    @Override
    public List<Wallet> getAllWalletsWithUser() {
        return walletRepository.findAllWithUser();
    }
}
