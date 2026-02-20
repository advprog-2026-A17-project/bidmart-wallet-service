package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;

import java.util.List;

public interface WalletService {
    Wallet create(Wallet wallet);
    List<Wallet> findAll();
    Wallet findWalletByUserId(String userId);
    Wallet topUpBalance(String userId, Long amount);
}
