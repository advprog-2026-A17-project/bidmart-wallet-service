package id.ac.ui.cs.advprog.bidmartwalletservice.controller;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.ConvertFundsRequest;
import id.ac.ui.cs.advprog.bidmartwalletservice.dto.HoldFundsRequest;
import id.ac.ui.cs.advprog.bidmartwalletservice.dto.ReleaseFundsRequest;
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
@RequestMapping({"/api/v1/wallet", "/api/wallet"})
public class WalletController {

    private final WalletService walletService;
    private final WalletTransactionRepository transactionRepository;

    public WalletController(
            WalletService walletService,
            WalletTransactionRepository transactionRepository
    ) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String userId) {
        Wallet wallet = walletService.findWalletByUserId(userId);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/{userId}/detail")
    public ResponseEntity<Map<String, Object>> getWalletDetail(@PathVariable String userId) {
        Wallet wallet = walletService.findWalletByUserId(userId);
        List<WalletTransaction> history = transactionRepository.findAllByUserIdOrderByTimestampDesc(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("wallet", wallet);
        response.put("history", history);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Wallet> addWalletManually(@RequestBody Wallet wallet) {
        return ResponseEntity.ok(walletService.create(wallet));
    }

    @PostMapping("/{userId}/top-up")
    public ResponseEntity<Wallet> topUp(@PathVariable String userId, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(walletService.topUpBalance(userId, amount));
    }

    @PostMapping("/hold")
    public ResponseEntity<Wallet> holdFunds(@RequestBody HoldFundsRequest request) {
        return ResponseEntity.ok(walletService.holdFunds(request.userId(), request.amount()));
    }

    @PostMapping("/release")
    public ResponseEntity<Wallet> releaseFunds(@RequestBody ReleaseFundsRequest request) {
        return ResponseEntity.ok(walletService.releaseFunds(request.userId(), request.amount()));
    }

    @PostMapping("/convert")
    public ResponseEntity<Wallet> convertHeldFunds(@RequestBody ConvertFundsRequest request) {
        return ResponseEntity.ok(walletService.convertHeldFunds(request.userId(), request.amount()));
    }

    @PostMapping("/{userId}/trybid")
    public ResponseEntity<Wallet> tryToBid(@PathVariable String userId, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(walletService.bidding(userId, amount));
    }

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<Wallet> withdraw(@PathVariable String userId, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(walletService.withdrawal(userId, amount));
    }
}
