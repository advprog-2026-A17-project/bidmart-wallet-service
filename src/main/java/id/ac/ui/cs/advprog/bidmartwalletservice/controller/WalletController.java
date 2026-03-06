package id.ac.ui.cs.advprog.bidmartwalletservice.controller;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.WalletTransaction;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletTransactionRepository;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletTransactionRepository transactionRepository;

    public WalletController(WalletService walletService, WalletTransactionRepository transactionRepository) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getWalletDetail(@PathVariable String userId) {
        Wallet wallet = walletService.findWalletByUserId(userId);
        List<WalletTransaction> history = transactionRepository.findHistoryByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("wallet", wallet);
        response.put("history", history);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Wallet> addWalletManually(@RequestBody Wallet wallet) {
        Wallet savedWallet = walletService.create(wallet);
        return ResponseEntity.ok(savedWallet);
    }

    @PostMapping("/{userId}/top-up")
    public ResponseEntity<Wallet> topUp(@PathVariable String userId, @RequestParam BigDecimal amount) {
        Wallet updatedWallet = walletService.topUpBalance(userId, amount);
        return ResponseEntity.ok(updatedWallet);
    }

    @PostMapping("/{userId}/trybid")
    public ResponseEntity<Wallet> tryToBid(@PathVariable String userId, @RequestParam BigDecimal amount) {
        Wallet updatedWallet = walletService.bidding(userId, amount);
        return ResponseEntity.ok(updatedWallet);
    }

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<Wallet> withdraw(@PathVariable String userId, @RequestParam BigDecimal amount) {
        Wallet updatedWallet = walletService.withdrawal(userId, amount);
        return ResponseEntity.ok(updatedWallet);
    }
}