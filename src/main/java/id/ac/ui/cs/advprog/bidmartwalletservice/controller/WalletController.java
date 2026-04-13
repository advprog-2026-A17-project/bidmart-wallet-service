package id.ac.ui.cs.advprog.bidmartwalletservice.controller;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.ConvertFundsRequest;
import id.ac.ui.cs.advprog.bidmartwalletservice.dto.HoldFundsRequest;
import id.ac.ui.cs.advprog.bidmartwalletservice.dto.ReleaseFundsRequest;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/wallet", "/api/wallet"})
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
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

    @PostMapping("/hold")
    public ResponseEntity<Wallet> holdFunds(@RequestBody HoldFundsRequest request) {
        Wallet updatedWallet = walletService.holdFunds(request.userId(), request.amount());
        return ResponseEntity.ok(updatedWallet);
    }

    @PostMapping("/release")
    public ResponseEntity<Wallet> releaseFunds(@RequestBody ReleaseFundsRequest request) {
        Wallet updatedWallet = walletService.releaseFunds(request.userId(), request.amount());
        return ResponseEntity.ok(updatedWallet);
    }

    @PostMapping("/convert")
    public ResponseEntity<Wallet> convertHeldFunds(@RequestBody ConvertFundsRequest request) {
        Wallet updatedWallet = walletService.convertHeldFunds(request.userId(), request.amount());
        return ResponseEntity.ok(updatedWallet);
    }
}
