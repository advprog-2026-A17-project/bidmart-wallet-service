package id.ac.ui.cs.advprog.bidmartwalletservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
//@RestController
//@RequestMapping("/api/wallet")
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
    public ResponseEntity<Wallet> getWallet(@PathVariable String userId) {
        Wallet wallet = walletService.findWalletByUserId(userId);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/{userId}/top-up")
    public ResponseEntity<Wallet> topUp(@PathVariable String userId, @RequestParam Long amount) {
        Wallet updatedWallet = walletService.topUpBalance(userId, amount);
        return ResponseEntity.ok(updatedWallet);
    }
}
