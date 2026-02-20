package id.ac.ui.cs.advprog.bidmartwalletservice.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {
    Wallet wallet;

    @BeforeEach
    void setUp(){
        this.wallet = new Wallet();
        this.wallet.setId(0L);
        this.wallet.setUserId("userTest");
        this.wallet.setActiveBalance(10000);
        this.wallet.setHeldBalance(5000);
    }

    @Test
    void testGetWalletId() {
        assertEquals(0L, this.wallet.getId());
    }

    @Test
    void testGetWalletActiveBalance() {
        assertEquals(10000, this.wallet.getActiveBalance());
    }

    @Test
    void testGetWalletHeldBalance() {
        assertEquals(5000, this.wallet.getHeldBalance());
    }

    @Test
    void testGetUserIdInWallet(){
        assertEquals("userTest", this.wallet.getUserId());
    }
}
