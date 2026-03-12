package com.wallet.cryptowallet.repositories;

import com.wallet.cryptowallet.models.Transaction;
import com.wallet.cryptowallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // existing:
    List<Transaction> findAllByOrderByTimestampDesc();

    // NEW: only this wallet’s transactions, newest first
    List<Transaction> findByWalletOrderByTimestampDesc(Wallet wallet);
}
