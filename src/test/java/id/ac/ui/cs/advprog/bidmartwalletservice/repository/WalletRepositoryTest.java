package id.ac.ui.cs.advprog.bidmartwalletservice.repository;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void testFindByUserId() {
        Wallet wallet = new Wallet();
        wallet.setUserId("userTest");
        wallet.setActiveBalance(new BigDecimal("10000"));
        wallet.setHeldBalance(new BigDecimal("5000"));
        walletRepository.save(wallet);

        Optional<Wallet> result = walletRepository.findByUserId("userTest");

        assertTrue(result.isPresent());
        assertEquals("userTest", result.get().getUserId());
        assertEquals(new BigDecimal("10000"), result.get().getActiveBalance());
    }
}
