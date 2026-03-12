package com.wallet.cryptowallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CryptowalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptowalletApplication.class, args);

        // 🌐 Friendly console message for developer
        System.out.println("----------------------------------------------------");
        System.out.println("🚀 CryptoWallet application started successfully!");
        System.out.println("👉 Open your browser and go to: http://localhost:8081/");
        System.out.println("----------------------------------------------------");
    }
}
