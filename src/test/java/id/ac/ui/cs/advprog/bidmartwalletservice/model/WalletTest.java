package id.ac.ui.cs.advprog.bidmartwalletservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WalletTest {

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setId("wallet-1");
        wallet.setUserId("userTest");
        wallet.setActiveBalance(new BigDecimal("10000"));
        wallet.setHeldBalance(new BigDecimal("5000"));
    }

    @Test
    void testGetWalletId() {
        assertEquals("wallet-1", wallet.getId());
    }

    @Test
    void testGetWalletActiveBalance() {
        assertEquals(new BigDecimal("10000"), wallet.getActiveBalance());
    }

    @Test
    void testGetWalletHeldBalance() {
        assertEquals(new BigDecimal("5000"), wallet.getHeldBalance());
    }

    @Test
    void testGetUserIdInWallet() {
        assertEquals("userTest", wallet.getUserId());
    }
}
