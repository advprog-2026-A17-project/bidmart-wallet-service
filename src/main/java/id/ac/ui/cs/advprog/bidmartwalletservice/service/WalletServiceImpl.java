package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService{

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Wallet create(Wallet wallet){
        return walletRepository.createWallet(wallet);
    }

    @Override
    public List<Wallet> findAll() {
        Iterator<Wallet> productIterator = walletRepository.findAll();
        List<Wallet> allWallet = new ArrayList<>();
        productIterator.forEachRemaining(allWallet::add);
        return allWallet;
    }

    @Override
    public Wallet findWalletByUserId(String userId){
        return walletRepository.findWalletByUserId(userId).orElseThrow(
                () -> new RuntimeException("Wallet doesn't exist"));
    }

    @Override
    public Wallet topUpBalance(String userId, Long amount){
        Wallet wallet = findWalletByUserId(userId);
        wallet.setActiveBalance(wallet.getActiveBalance() + amount);
        return walletRepository.updateWallet(wallet);
    }

    @Override
    public Wallet holdFunds(String userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Wallet wallet = findWalletByUserId(userId);
        if (wallet.getActiveBalance() < amount) {
            throw new IllegalStateException("Insufficient active balance");
        }

        wallet.setActiveBalance(wallet.getActiveBalance() - amount);
        wallet.setHeldBalance(wallet.getHeldBalance() + amount);
        return walletRepository.updateWallet(wallet);
    }

    @Override
    public Wallet releaseFunds(String userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Wallet wallet = findWalletByUserId(userId);
        if (wallet.getHeldBalance() < amount) {
            throw new IllegalStateException("Insufficient held balance");
        }

        wallet.setHeldBalance(wallet.getHeldBalance() - amount);
        wallet.setActiveBalance(wallet.getActiveBalance() + amount);
        return walletRepository.updateWallet(wallet);
    }

    @Override
    public Wallet convertHeldFunds(String userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Wallet wallet = findWalletByUserId(userId);
        if (wallet.getHeldBalance() < amount) {
            throw new IllegalStateException("Insufficient held balance");
        }

        wallet.setHeldBalance(wallet.getHeldBalance() - amount);
        return walletRepository.updateWallet(wallet);
    }
}
