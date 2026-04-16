package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.WalletProvisionRequestedV1;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private WalletProvisionRequestedV1 sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = new WalletProvisionRequestedV1(
                "evt-123", "user-789", "haqi@example.com", LocalDateTime.now(), "auth-service"
        );
    }

    @Test
    void testProvisionWallet_WhenWalletAbsent_CreatesWallet() {
        when(walletRepository.findByUserId(sampleEvent.userId())).thenReturn(Optional.empty());
        walletService.provisionWallet(sampleEvent);
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository, times(1)).save(walletCaptor.capture());
        Wallet savedWallet = walletCaptor.getValue();
        assertEquals("user-789", savedWallet.getUserId());
    }

    @Test
    void testProvisionWallet_WhenWalletExists_DoesNotCreateDuplicate() {
        Wallet existingWallet = new Wallet();
        existingWallet.setUserId(sampleEvent.userId());
        when(walletRepository.findByUserId(sampleEvent.userId())).thenReturn(Optional.of(existingWallet));
        walletService.provisionWallet(sampleEvent);
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}