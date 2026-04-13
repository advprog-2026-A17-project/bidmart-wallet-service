package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setUserId("userTest");
        wallet.setActiveBalance(10000);
    }

    @Test
    void testCreateWallet() {
        when(walletRepository.createWallet(any(Wallet.class))).thenReturn(wallet);
        Wallet savedWallet = walletService.create(wallet);

        assertNotNull(savedWallet);
        assertEquals("userTest", savedWallet.getUserId());
    }

    @Test
    void testFindWalletByUserId_Success() {
        when(walletRepository.findWalletByUserId("userTest")).thenReturn(Optional.of(wallet));
        Wallet result = walletService.findWalletByUserId("userTest");

        assertNotNull(result);
        assertEquals("userTest", result.getUserId());
    }

    @Test
    void testFindWalletByUserId_NotFound() {
        when(walletRepository.findWalletByUserId("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            walletService.findWalletByUserId("unknown");
        });
    }

    @Test
    void testTopUpBalance_Success() {
        when(walletRepository.findWalletByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(walletRepository.updateWallet(any(Wallet.class))).thenReturn(wallet);
        Wallet result = walletService.topUpBalance("userTest", 5000L);

        assertEquals(15000L, result.getActiveBalance());
        verify(walletRepository, times(1)).updateWallet(any(Wallet.class));
    }

    @Test
    void testHoldFunds_Success() {
        when(walletRepository.findWalletByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(walletRepository.updateWallet(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.holdFunds("userTest", 2500);

        assertEquals(7500, result.getActiveBalance());
        assertEquals(2500, result.getHeldBalance());
    }

    @Test
    void testHoldFunds_InsufficientBalance() {
        when(walletRepository.findWalletByUserId("userTest")).thenReturn(Optional.of(wallet));

        assertThrows(IllegalStateException.class, () -> walletService.holdFunds("userTest", 15000));
    }

    @Test
    void testReleaseFunds_Success() {
        wallet.setHeldBalance(5000);
        wallet.setActiveBalance(6000);
        when(walletRepository.findWalletByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(walletRepository.updateWallet(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.releaseFunds("userTest", 2000);

        assertEquals(8000, result.getActiveBalance());
        assertEquals(3000, result.getHeldBalance());
    }

    @Test
    void testConvertHeldFunds_Success() {
        wallet.setHeldBalance(5000);
        wallet.setActiveBalance(6000);
        when(walletRepository.findWalletByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(walletRepository.updateWallet(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.convertHeldFunds("userTest", 3000);

        assertEquals(6000, result.getActiveBalance());
        assertEquals(2000, result.getHeldBalance());
    }
}
