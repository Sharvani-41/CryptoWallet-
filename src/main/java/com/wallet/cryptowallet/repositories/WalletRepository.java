package com.wallet.cryptowallet.repositories;

import com.wallet.cryptowallet.models.User;
import com.wallet.cryptowallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser(User user);

    // Fetch-join user so wallet.user is initialized for views
    @Query("select w from Wallet w left join fetch w.user")
    List<Wallet> findAllWithUser();
}
