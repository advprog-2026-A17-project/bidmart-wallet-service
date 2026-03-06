package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.WalletTransaction;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletRepository;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService{

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletServiceImpl(WalletRepository walletRepository, WalletTransactionRepository walletTransactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = walletTransactionRepository;
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
    public Wallet topUpBalance(String userId, BigDecimal amount){
        Wallet wallet = findWalletByUserId(userId);
        wallet.setActiveBalance(wallet.getActiveBalance().add(amount));
        WalletTransaction history = new WalletTransaction(userId, "TOP_UP", amount);
        transactionRepository.save(history);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet bidding(String userId, BigDecimal amount){
        Wallet wallet = findWalletByUserId(userId);
        if(wallet.getActiveBalance().compareTo(amount) < 0){
            return walletRepository.save(wallet);
        }
        wallet.setActiveBalance(wallet.getActiveBalance().subtract(amount));
        wallet.setHeldBalance(wallet.getHeldBalance().add(amount));
        WalletTransaction history = new WalletTransaction(userId, "BID", amount);
        transactionRepository.save(history);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet withdrawal(String userId, BigDecimal amount){
        Wallet wallet = findWalletByUserId(userId);
        if(wallet.getActiveBalance().compareTo(amount) < 0){
            return walletRepository.save(wallet);
        }
        wallet.setActiveBalance(wallet.getActiveBalance().subtract(amount));
        WalletTransaction history = new WalletTransaction(userId, "WITHDRAW", amount);
        transactionRepository.save(history);
        return walletRepository.save(wallet);
    }

    @Override
     public void cancelBid(String userId, String bidId){
        Wallet wallet = findWalletByUserId(userId);
        WalletTransaction transaction = transactionRepository.findById(bidId).orElseThrow(
                ()-> new RuntimeException("Transaksi tidak ditemukan"));
        if(!userId.equals(transaction.getUserId())){
            return;
        }
        BigDecimal amount = transaction.getAmount();
        wallet.setHeldBalance(wallet.getHeldBalance().subtract(amount));
        wallet.setActiveBalance((wallet.getActiveBalance()).add(amount));
        WalletTransaction history = new WalletTransaction(userId, "CANCEL_BID", amount);
        transactionRepository.save(history);
        walletRepository.save(wallet);

    }
}
