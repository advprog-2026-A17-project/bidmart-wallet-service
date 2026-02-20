package id.ac.ui.cs.advprog.bidmartwalletservice.repository;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Repository
public class WalletRepository {
    private List<Wallet> walletList = new ArrayList<>();

    public Wallet createWallet(Wallet wallet){
        walletList.add(wallet);
        return wallet;
    }

    public Wallet updateWallet(Wallet newWallet){
        for (Wallet wallet : walletList) {
            if (wallet.getId().equals(newWallet.getId())) {
                walletList.remove(wallet);
                break;
            }
        }
        walletList.add(newWallet);
        return newWallet;
    }

    public boolean deleteWallet(Long walletId) {
        return walletList.removeIf(wallet -> wallet.getId().equals(walletId));
    }

    public Optional<Wallet> findWalletById(Long id){
        for (Wallet wallet : walletList) {
            if (wallet.getId().equals(id)) {
                return Optional.of(wallet);
            }
        }
        return Optional.empty();
    }

    public Optional<Wallet> findWalletByUserId(String id){
        for (Wallet wallet : walletList) {
            if (wallet.getUserId().equals(id)) {
                return Optional.of(wallet);
            }
        }
        return Optional.empty();
    }

    public Iterator<Wallet> findAll(){
        return walletList.iterator();
    }
}
