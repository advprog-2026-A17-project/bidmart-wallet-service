package id.ac.ui.cs.advprog.bidmartwalletservice.repository;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class WalletRepositoryTest {
    private WalletRepository walletRepository;

    @BeforeEach
    void setUp(){
        this.walletRepository = new WalletRepository();
    }

    @Test
    void testCreateWallet() {
        Wallet wallet = new Wallet();
        wallet.setId(0L);
        wallet.setUserId("userTest");
        wallet.setActiveBalance(10000);
        wallet.setHeldBalance(5000);

        walletRepository.createWallet(wallet);
        Iterator<Wallet> WalletIterator = walletRepository.findAll();
        assertTrue(WalletIterator.hasNext());
        Wallet savedWallet = WalletIterator.next();
        assertEquals(wallet.getId(), savedWallet.getId());
        assertEquals(wallet.getUserId(), savedWallet.getUserId());
        assertEquals(wallet.getActiveBalance(), savedWallet.getActiveBalance());
        assertEquals(wallet.getHeldBalance(), savedWallet.getHeldBalance());

    }

    @Test
    void testUpdateWallet() {
        Wallet wallet = new Wallet();
        wallet.setId(0L);
        wallet.setUserId("userTest");
        wallet.setActiveBalance(10000);
        wallet.setHeldBalance(5000);
        walletRepository.createWallet(wallet);

        Wallet newWallet = new Wallet();
        newWallet.setId(0L);
        newWallet.setUserId("userTest");
        newWallet.setActiveBalance(5000);
        newWallet.setHeldBalance(10000);
        walletRepository.updateWallet(newWallet);

        Iterator<Wallet> WalletIterator = walletRepository.findAll();
        assertTrue(WalletIterator.hasNext());
        Wallet savedWallet = WalletIterator.next();
        assertEquals(newWallet.getId(), savedWallet.getId());
        assertEquals(newWallet.getUserId(), savedWallet.getUserId());
        assertEquals(newWallet.getActiveBalance(), savedWallet.getActiveBalance());
        assertEquals(newWallet.getHeldBalance(), savedWallet.getHeldBalance());

    }

    @Test
    void testFindWalletByUserId_NotFound() {
        Optional<Wallet> result = walletRepository.findWalletByUserId("unknown");
        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll_WithData() {
        walletRepository.createWallet(new Wallet());
        walletRepository.createWallet(new Wallet());

        Iterator<Wallet> iterator = walletRepository.findAll();

        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        assertEquals(2, count);
    }
}
