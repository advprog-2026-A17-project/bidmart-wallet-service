package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;

import java.util.List;

public interface WalletService {
    Wallet create(Wallet wallet);
    List<Wallet> findAll();
    Wallet findWalletByUserId(String userId);
    Wallet topUpBalance(String userId, Long amount);
    Wallet holdFunds(String userId, double amount);
    Wallet releaseFunds(String userId, double amount);
    Wallet convertHeldFunds(String userId, double amount);
}
