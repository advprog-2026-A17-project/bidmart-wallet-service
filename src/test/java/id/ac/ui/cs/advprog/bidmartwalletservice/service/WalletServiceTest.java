package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.WalletProvisionRequestedV1;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.WalletProvisioningEvent;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.WalletTransaction;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletProvisioningEventRepository;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletRepository;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletTransactionRepository transactionRepository;

    @Mock
    private WalletProvisioningEventRepository provisioningEventRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private Wallet wallet;
    private WalletProvisionRequestedV1 sampleEvent;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setUserId("userTest");
        wallet.setActiveBalance(new BigDecimal("10000"));
        wallet.setHeldBalance(BigDecimal.ZERO);

        sampleEvent = new WalletProvisionRequestedV1(
                "evt-123",
                "user-789",
                "user-789@example.com",
                Instant.now(),
                "bidmart-auth-service"
        );
    }

    @Test
    void testHoldFundsSuccess() {
        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.holdFunds("userTest", new BigDecimal("2500"));

        assertEquals(new BigDecimal("7500"), result.getActiveBalance());
        assertEquals(new BigDecimal("2500"), result.getHeldBalance());
        verify(transactionRepository, times(1)).save(any(WalletTransaction.class));
    }

    @Test
    void testHoldFundsInsufficientBalanceThrows() {
        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));

        assertThrows(
                IllegalStateException.class,
                () -> walletService.holdFunds("userTest", new BigDecimal("20000"))
        );
    }

    @Test
    void testProvisionWalletWhenWalletAbsentCreatesWallet() {
        when(provisioningEventRepository.existsById(sampleEvent.eventId())).thenReturn(false);
        when(walletRepository.findByUserId(sampleEvent.userId())).thenReturn(Optional.empty());

        walletService.provisionWallet(sampleEvent);

        verify(provisioningEventRepository, times(1)).save(any(WalletProvisioningEvent.class));
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository, times(1)).save(walletCaptor.capture());
        assertEquals(sampleEvent.userId(), walletCaptor.getValue().getUserId());
    }

    @Test
    void testProvisionWalletDuplicateEventIdNoops() {
        when(provisioningEventRepository.existsById(sampleEvent.eventId())).thenReturn(true);

        walletService.provisionWallet(sampleEvent);

        verify(provisioningEventRepository, never()).save(any(WalletProvisioningEvent.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testProvisionWalletExistingWalletNoopsCreation() {
        Wallet existingWallet = new Wallet();
        existingWallet.setUserId(sampleEvent.userId());

        when(provisioningEventRepository.existsById(sampleEvent.eventId())).thenReturn(false);
        when(walletRepository.findByUserId(sampleEvent.userId())).thenReturn(Optional.of(existingWallet));

        walletService.provisionWallet(sampleEvent);

        verify(provisioningEventRepository, times(1)).save(any(WalletProvisioningEvent.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}
