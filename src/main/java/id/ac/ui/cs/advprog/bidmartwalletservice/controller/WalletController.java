package id.ac.ui.cs.advprog.bidmartwalletservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/")
    public String showTestPage(Model model) {
        List<Wallet> allWallets = walletService.findAll();
        model.addAttribute("wallets", allWallets);
        return "WalletPage";
    }
    @PostMapping("/add")
    public String addWalletManually(@ModelAttribute Wallet wallet) {
        walletService.create(wallet);
        return "redirect:/";
    }

    @GetMapping("/{userId}")
    public String getWallet(@PathVariable String userId, Model model) {
        Wallet wallet = walletService.findWalletByUserId(userId);
        model.addAttribute("wallet", wallet);
        return "WalletDetail";
    }

    @PostMapping("/{userId}/top-up")
    public String topUp(@PathVariable String userId, @RequestParam Long amount) {
        walletService.topUpBalance(userId, amount);
        return "redirect:/api/v1/wallet/" + userId;
    }

    @PostMapping("/{userId}/trybid")
    public String tryToBid(@PathVariable String userId, @RequestParam Long amount) {
        walletService.bidding(userId, amount);
        return "redirect:/api/v1/wallet/" + userId;
    }
    @PostMapping("/{userId}/withdraw")
    public String withdraw(@PathVariable String userId, @RequestParam Long amount) {
        walletService.withdrawal(userId, amount);
        return "redirect:/api/v1/wallet/" + userId;
    }
}
