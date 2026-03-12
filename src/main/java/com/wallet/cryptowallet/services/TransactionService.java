package com.wallet.cryptowallet.services;

import com.wallet.cryptowallet.models.Transaction;
import com.wallet.cryptowallet.models.Wallet;
import com.wallet.cryptowallet.repositories.TransactionRepository;
import com.wallet.cryptowallet.repositories.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    public void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {
        validateTransferParameters(fromWalletId, toWalletId, amount);

        Wallet fromWallet = walletRepository.findById(fromWalletId)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));
        Wallet toWallet = walletRepository.findById(toWalletId)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (fromWallet.getId().equals(toWallet.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same wallet");
        }

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
        toWallet.setBalance(toWallet.getBalance().add(amount));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        // Create debit transaction
        Transaction debitTransaction = new Transaction();
        debitTransaction.setAmount(amount.negate());
        debitTransaction.setType("TRANSFER_OUT");
        debitTransaction.setDescription("Transfer to wallet " + toWalletId);
        debitTransaction.setWallet(fromWallet);
        debitTransaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(debitTransaction);

        // Create credit transaction
        Transaction creditTransaction = new Transaction();
        creditTransaction.setAmount(amount);
        creditTransaction.setType("TRANSFER_IN");
        creditTransaction.setDescription("Transfer from wallet " + fromWalletId);
        creditTransaction.setWallet(toWallet);
        creditTransaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(creditTransaction);
    }

    public Transaction recordDeposit(Wallet wallet, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction depositTransaction = new Transaction();
        depositTransaction.setAmount(amount);
        depositTransaction.setType("DEPOSIT");
        depositTransaction.setDescription("Deposit to wallet " + wallet.getId());
        depositTransaction.setWallet(wallet);
        depositTransaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(depositTransaction);
    }

    public Transaction recordWithdrawal(Wallet wallet, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance for withdrawal");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction withdrawalTransaction = new Transaction();
        withdrawalTransaction.setAmount(amount.negate());
        withdrawalTransaction.setType("WITHDRAWAL");
        withdrawalTransaction.setDescription("Withdrawal from wallet " + wallet.getId());
        withdrawalTransaction.setWallet(wallet);
        withdrawalTransaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(withdrawalTransaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByTimestampDesc();
    }

    private void validateTransferParameters(Long fromWalletId, Long toWalletId, BigDecimal amount) {
        if (fromWalletId == null || toWalletId == null) {
            throw new IllegalArgumentException("Wallet IDs cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}