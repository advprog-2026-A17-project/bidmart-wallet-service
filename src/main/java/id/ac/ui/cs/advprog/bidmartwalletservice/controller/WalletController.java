package id.ac.ui.cs.advprog.bidmartwalletservice.controller;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.*;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.WalletTransaction;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletTransactionRepository;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
    public ResponseEntity<WalletResponse> getWallet(@PathVariable String userId) {
        Wallet wallet = walletService.findWalletByUserId(userId);
        return ResponseEntity.ok(toWalletResponse(wallet));
    }

    @GetMapping("/{userId}/detail")
    public ResponseEntity<WalletDetailResponse> getWalletDetail(@PathVariable String userId) {
        Wallet wallet = walletService.findWalletByUserId(userId);
        List<WalletTransactionResponse> history = transactionRepository.findAllByUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(this::toWalletTransactionResponse)
                .toList();

        return ResponseEntity.ok(new WalletDetailResponse(toWalletResponse(wallet), history));
    }

    @PostMapping("/add")
    public ResponseEntity<WalletResponse> addWalletManually(@RequestBody WalletCreateRequest request) {
        Wallet wallet = new Wallet();
        wallet.setUserId(request.userId());
        wallet.setActiveBalance(request.activeBalance() == null ? BigDecimal.ZERO : request.activeBalance());
        wallet.setHeldBalance(request.heldBalance() == null ? BigDecimal.ZERO : request.heldBalance());

        return ResponseEntity.ok(toWalletResponse(walletService.create(wallet)));
    }

    @PostMapping("/{userId}/top-up")
    public ResponseEntity<WalletResponse> topUp(@PathVariable String userId, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(toWalletResponse(walletService.topUpBalance(userId, amount)));
    }

    @PostMapping("/hold")
    public ResponseEntity<WalletResponse> holdFunds(@RequestBody HoldFundsRequest request) {
        return ResponseEntity.ok(toWalletResponse(walletService.holdFunds(request.userId(), request.amount())));
    }

    @PostMapping("/release")
    public ResponseEntity<WalletResponse> releaseFunds(@RequestBody ReleaseFundsRequest request) {
        return ResponseEntity.ok(toWalletResponse(walletService.releaseFunds(request.userId(), request.amount())));
    }

    @PostMapping("/convert")
    public ResponseEntity<WalletResponse> convertHeldFunds(@RequestBody ConvertFundsRequest request) {
        return ResponseEntity.ok(toWalletResponse(walletService.convertHeldFunds(request.userId(), request.amount())));
    }

    @PostMapping("/{userId}/trybid")
    public ResponseEntity<WalletResponse> tryToBid(@PathVariable String userId, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(toWalletResponse(walletService.bidding(userId, amount)));
    }

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<WalletResponse> withdraw(@PathVariable String userId, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(toWalletResponse(walletService.withdrawal(userId, amount)));
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getActiveBalance(),
                wallet.getHeldBalance()
        );
    }

    private WalletTransactionResponse toWalletTransactionResponse(WalletTransaction transaction) {
        return new WalletTransactionResponse(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getTimestamp()
        );
    }
}
