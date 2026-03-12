package com.wallet.cryptowallet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String root() {
        return "index"; // your welcome page
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "redirect:/home";
    }
}
