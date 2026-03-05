package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService{

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Wallet create(Wallet wallet){
        return walletRepository.save(wallet);
    }

    @Override
    public List<Wallet> findAll() {
        return walletRepository.findAll();
    }

    @Override
    public Wallet findWalletByUserId(String userId){
        return walletRepository.findByUserId(userId).orElseThrow(
                () -> new RuntimeException("Wallet doesn't exist"));
    }

    @Override
    public Wallet topUpBalance(String userId, Long amount){
        Wallet wallet = findWalletByUserId(userId);
        wallet.setActiveBalance(wallet.getActiveBalance() + amount);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet bidding(String userId, Long amount){
        Wallet wallet = findWalletByUserId(userId);
        if(wallet.getActiveBalance() - amount < 0){
            return walletRepository.save(wallet);
        }
        wallet.setActiveBalance(wallet.getActiveBalance() - amount);
        wallet.setHeldBalance(wallet.getHeldBalance() + amount);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet withdrawal(String userId, Long amount){
        Wallet wallet = findWalletByUserId(userId);
        if(wallet.getActiveBalance() - amount < 0){
            return walletRepository.save(wallet);
        }
        wallet.setActiveBalance(wallet.getActiveBalance() - amount);
        return walletRepository.save(wallet);
    }
}
