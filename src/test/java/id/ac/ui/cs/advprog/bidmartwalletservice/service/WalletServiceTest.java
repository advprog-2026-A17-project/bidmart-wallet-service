package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.WalletProvisionRequestedV1;
import id.ac.ui.cs.advprog.bidmartwalletservice.exception.WalletNotFoundException;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

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
    void testFindWalletByUserIdNotFoundThrowsWalletNotFoundException() {
        when(walletRepository.findByUserId("missing-user")).thenReturn(Optional.empty());

        assertThrows(
                WalletNotFoundException.class,
                () -> walletService.findWalletByUserId("missing-user")
        );
    }

    @Test
    void testTopUpBalanceSuccess() {
        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.topUpBalance("userTest", new BigDecimal("5000"));

        assertEquals(new BigDecimal("15000"), result.getActiveBalance());
        verify(transactionRepository, times(1)).save(any(WalletTransaction.class));
    }

    @Test
    void testTopUpBalanceRejectsNonPositiveAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> walletService.topUpBalance("userTest", BigDecimal.ZERO)
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
    void testReleaseFundsSuccess() {
        wallet.setHeldBalance(new BigDecimal("3000"));
        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.releaseFunds("userTest", new BigDecimal("1000"));

        assertEquals(new BigDecimal("11000"), result.getActiveBalance());
        assertEquals(new BigDecimal("2000"), result.getHeldBalance());
        verify(transactionRepository, times(1)).save(any(WalletTransaction.class));
    }

    @Test
    void testReleaseFundsInsufficientHeldBalanceThrows() {
        wallet.setHeldBalance(new BigDecimal("500"));
        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));

        assertThrows(
                IllegalStateException.class,
                () -> walletService.releaseFunds("userTest", new BigDecimal("1000"))
        );
    }

    @Test
    void testConvertHeldFundsSuccess() {
        wallet.setHeldBalance(new BigDecimal("3000"));
        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.convertHeldFunds("userTest", new BigDecimal("1000"));

        assertEquals(new BigDecimal("2000"), result.getHeldBalance());
        verify(transactionRepository, times(1)).save(any(WalletTransaction.class));
    }

    @Test
    void testBiddingSuccess() {
        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.bidding("userTest", new BigDecimal("3000"));

        assertEquals(new BigDecimal("7000"), result.getActiveBalance());
        assertEquals(new BigDecimal("3000"), result.getHeldBalance());
        verify(transactionRepository, times(1)).save(argThat(tx -> "BID".equals(tx.getType())));
    }

    @Test
    void testWithdrawalSuccess() {
        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.withdrawal("userTest", new BigDecimal("2500"));

        assertEquals(new BigDecimal("7500"), result.getActiveBalance());
        verify(transactionRepository, times(1)).save(argThat(tx -> "WITHDRAW".equals(tx.getType())));
    }

    @Test
    void testWithdrawalInsufficientBalanceThrows() {
        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));

        assertThrows(
                IllegalStateException.class,
                () -> walletService.withdrawal("userTest", new BigDecimal("20000"))
        );
    }

    @Test
    void testCancelBidSuccess() {
        wallet.setActiveBalance(new BigDecimal("5000"));
        wallet.setHeldBalance(new BigDecimal("5000"));

        WalletTransaction bidTransaction = new WalletTransaction();
        bidTransaction.setId("tx-1");
        bidTransaction.setUserId("userTest");
        bidTransaction.setType("BID");
        bidTransaction.setAmount(new BigDecimal("2000"));

        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(transactionRepository.findById("tx-1")).thenReturn(Optional.of(bidTransaction));

        walletService.cancelBid("userTest", "tx-1");

        assertEquals(new BigDecimal("7000"), wallet.getActiveBalance());
        assertEquals(new BigDecimal("3000"), wallet.getHeldBalance());
        verify(transactionRepository, times(1)).save(argThat(tx -> "CANCEL_BID".equals(tx.getType())));
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void testCancelBidTransactionNotFoundThrows() {
        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(transactionRepository.findById("missing-tx")).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> walletService.cancelBid("userTest", "missing-tx")
        );
    }

    @Test
    void testCancelBidForbiddenAccessThrows() {
        WalletTransaction bidTransaction = new WalletTransaction();
        bidTransaction.setId("tx-1");
        bidTransaction.setUserId("other-user");
        bidTransaction.setAmount(new BigDecimal("1000"));

        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(transactionRepository.findById("tx-1")).thenReturn(Optional.of(bidTransaction));

        assertThrows(
                IllegalStateException.class,
                () -> walletService.cancelBid("userTest", "tx-1")
        );
    }

    @Test
    void testCancelBidInsufficientHeldBalanceThrows() {
        wallet.setHeldBalance(new BigDecimal("200"));

        WalletTransaction bidTransaction = new WalletTransaction();
        bidTransaction.setId("tx-1");
        bidTransaction.setUserId("userTest");
        bidTransaction.setAmount(new BigDecimal("1000"));

        when(walletRepository.findByUserId("userTest")).thenReturn(Optional.of(wallet));
        when(transactionRepository.findById("tx-1")).thenReturn(Optional.of(bidTransaction));

        assertThrows(
                IllegalStateException.class,
                () -> walletService.cancelBid("userTest", "tx-1")
        );
    }

    @Test
    void testProvisionWalletInvalidPayloadThrows() {
        assertThrows(
                IllegalArgumentException.class,
                () -> walletService.provisionWallet(null)
        );

        WalletProvisionRequestedV1 invalidEvent = new WalletProvisionRequestedV1(
                null,
                "user-1",
                "u@example.com",
                Instant.now(),
                "auth"
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> walletService.provisionWallet(invalidEvent)
        );
    }

    @Test
    void testProvisionWalletDuplicateEventIdNoops() {
        when(provisioningEventRepository.existsById(sampleEvent.eventId())).thenReturn(true);

        walletService.provisionWallet(sampleEvent);

        verify(provisioningEventRepository, never()).save(any(WalletProvisioningEvent.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testProvisionWalletDuplicateConstraintOnEventNoops() {
        when(provisioningEventRepository.existsById(sampleEvent.eventId())).thenReturn(false);
        when(provisioningEventRepository.save(any(WalletProvisioningEvent.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate event"));

        walletService.provisionWallet(sampleEvent);

        verify(walletRepository, never()).save(any(Wallet.class));
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
    void testProvisionWalletExistingWalletNoopsCreation() {
        Wallet existingWallet = new Wallet();
        existingWallet.setUserId(sampleEvent.userId());

        when(provisioningEventRepository.existsById(sampleEvent.eventId())).thenReturn(false);
        when(walletRepository.findByUserId(sampleEvent.userId())).thenReturn(Optional.of(existingWallet));

        walletService.provisionWallet(sampleEvent);

        verify(provisioningEventRepository, times(1)).save(any(WalletProvisioningEvent.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testProvisionWalletDuplicateConstraintOnWalletIsIgnored() {
        when(provisioningEventRepository.existsById(sampleEvent.eventId())).thenReturn(false);
        when(walletRepository.findByUserId(sampleEvent.userId())).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenThrow(new DataIntegrityViolationException("duplicate wallet"));

        walletService.provisionWallet(sampleEvent);

        verify(provisioningEventRepository, times(1)).save(any(WalletProvisioningEvent.class));
    }

    @Test
    void testReconcileProvisionedWalletsUsesDefaultBatchWhenInputInvalid() {
        WalletProvisioningEvent event = new WalletProvisioningEvent();
        event.setUserId("user-1");

        when(provisioningEventRepository.findAllByOrderByProcessedAtDesc(PageRequest.of(0, 100)))
                .thenReturn(List.of(event));
        when(walletRepository.findByUserId("user-1")).thenReturn(Optional.empty());

        int createdCount = walletService.reconcileProvisionedWallets(0);

        assertEquals(1, createdCount);
    }

    @Test
    void testReconcileProvisionedWalletsSkipsExistingWallets() {
        WalletProvisioningEvent event = new WalletProvisioningEvent();
        event.setUserId("existing-user");

        when(provisioningEventRepository.findAllByOrderByProcessedAtDesc(PageRequest.of(0, 50)))
                .thenReturn(List.of(event));
        when(walletRepository.findByUserId("existing-user")).thenReturn(Optional.of(new Wallet()));

        int createdCount = walletService.reconcileProvisionedWallets(50);

        assertEquals(0, createdCount);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testReconcileProvisionedWalletsHandlesDuplicateWalletCreation() {
        WalletProvisioningEvent event = new WalletProvisioningEvent();
        event.setUserId("user-1");

        when(provisioningEventRepository.findAllByOrderByProcessedAtDesc(PageRequest.of(0, 20)))
                .thenReturn(List.of(event));
        when(walletRepository.findByUserId("user-1")).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenThrow(new DataIntegrityViolationException("duplicate wallet"));

        int createdCount = walletService.reconcileProvisionedWallets(20);

        assertEquals(0, createdCount);
    }
}
