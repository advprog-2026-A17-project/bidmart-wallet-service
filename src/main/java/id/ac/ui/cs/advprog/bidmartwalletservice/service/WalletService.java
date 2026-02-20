package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;

import java.util.List;

public interface WalletService {
    Wallet create(Wallet wallet);
    List<Wallet> findAll();
    Wallet findWalletByUserId(String userId);
    Wallet topUpBalance(String userId, Long amount);
    Wallet withdrawBalance(String userId, Long amount);
    Wallet holdBalance(String userId, Long amount);
    Wallet freeBalance(String userId, Long amount);
}
