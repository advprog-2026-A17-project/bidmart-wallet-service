package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    Wallet create(Wallet wallet);
    List<Wallet> findAll();
    Wallet findWalletByUserId(String userId);
    Wallet topUpBalance(String userId, BigDecimal amount);
    Wallet bidding(String userId, BigDecimal amount);
    Wallet withdrawal(String userId, BigDecimal amount);
    void cancelBid(String userId, String bidId);
}
