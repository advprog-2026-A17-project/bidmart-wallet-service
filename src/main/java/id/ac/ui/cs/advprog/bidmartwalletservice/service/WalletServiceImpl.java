package id.ac.ui.cs.advprog.bidmartwalletservice.service;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.WalletProvisionRequestedV1;
import id.ac.ui.cs.advprog.bidmartwalletservice.exception.WalletNotFoundException;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.WalletProvisioningEvent;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.WalletTransaction;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletProvisioningEventRepository;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletRepository;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final WalletProvisioningEventRepository provisioningEventRepository;

    public WalletServiceImpl(
            WalletRepository walletRepository,
            WalletTransactionRepository transactionRepository,
            WalletProvisioningEventRepository provisioningEventRepository
    ) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.provisioningEventRepository = provisioningEventRepository;
    }

    @Override
    @Transactional
    public Wallet create(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Wallet> findAll() {
        return walletRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet findWalletByUserId(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet doesn't exist"));
    }

    @Override
    @Transactional
    public Wallet topUpBalance(String userId, BigDecimal amount) {
        validatePositiveAmount(amount);
        Wallet wallet = findWalletByUserId(userId);
        wallet.setActiveBalance(wallet.getActiveBalance().add(amount));
        transactionRepository.save(new WalletTransaction(userId, "TOP_UP", amount));
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet holdFunds(String userId, BigDecimal amount) {
        validatePositiveAmount(amount);

        Wallet wallet = findWalletByUserId(userId);
        if (wallet.getActiveBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient active balance");
        }

        wallet.setActiveBalance(wallet.getActiveBalance().subtract(amount));
        wallet.setHeldBalance(wallet.getHeldBalance().add(amount));
        transactionRepository.save(new WalletTransaction(userId, "HOLD", amount));
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet releaseFunds(String userId, BigDecimal amount) {
        validatePositiveAmount(amount);

        Wallet wallet = findWalletByUserId(userId);
        if (wallet.getHeldBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient held balance");
        }

        wallet.setHeldBalance(wallet.getHeldBalance().subtract(amount));
        wallet.setActiveBalance(wallet.getActiveBalance().add(amount));
        transactionRepository.save(new WalletTransaction(userId, "RELEASE", amount));
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet convertHeldFunds(String userId, BigDecimal amount) {
        validatePositiveAmount(amount);

        Wallet wallet = findWalletByUserId(userId);
        if (wallet.getHeldBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient held balance");
        }

        wallet.setHeldBalance(wallet.getHeldBalance().subtract(amount));
        transactionRepository.save(new WalletTransaction(userId, "CONVERT", amount));
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet bidding(String userId, BigDecimal amount) {
        return executeHeldBalanceOperation(userId, amount, "BID");
    }

    @Override
    @Transactional
    public Wallet withdrawal(String userId, BigDecimal amount) {
        validatePositiveAmount(amount);

        Wallet wallet = findWalletByUserId(userId);
        if (wallet.getActiveBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient active balance");
        }

        wallet.setActiveBalance(wallet.getActiveBalance().subtract(amount));
        transactionRepository.save(new WalletTransaction(userId, "WITHDRAW", amount));
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void cancelBid(String userId, String bidId) {
        Wallet wallet = findWalletByUserId(userId);
        WalletTransaction transaction = transactionRepository.findById(bidId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        if (!userId.equals(transaction.getUserId())) {
            throw new IllegalStateException("Forbidden transaction access");
        }

        BigDecimal amount = transaction.getAmount();
        if (wallet.getHeldBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient held balance");
        }

        wallet.setHeldBalance(wallet.getHeldBalance().subtract(amount));
        wallet.setActiveBalance(wallet.getActiveBalance().add(amount));
        transactionRepository.save(new WalletTransaction(userId, "CANCEL_BID", amount));
        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void provisionWallet(WalletProvisionRequestedV1 event) {
        if (event == null || event.eventId() == null || event.userId() == null) {
            throw new IllegalArgumentException("Invalid wallet provisioning event payload");
        }

        if (provisioningEventRepository.existsById(event.eventId())) {
            LOGGER.info("Skip duplicate wallet provisioning eventId={}", event.eventId());
            return;
        }

        try {
            provisioningEventRepository.save(WalletProvisioningEvent.from(event, Instant.now()));
        } catch (DataIntegrityViolationException duplicateEvent) {
            LOGGER.info("Skip duplicate wallet provisioning eventId={} due to unique constraint", event.eventId());
            return;
        }

        if (walletRepository.findByUserId(event.userId()).isPresent()) {
            return;
        }

        try {
            Wallet wallet = new Wallet();
            wallet.setUserId(event.userId());
            walletRepository.save(wallet);
        } catch (DataIntegrityViolationException duplicateWallet) {
            LOGGER.info("Skip duplicate wallet creation userId={} due to unique constraint", event.userId());
        }
    }

    @Override
    @Transactional
    public int reconcileProvisionedWallets(int batchSize) {
        int effectiveBatchSize = batchSize > 0 ? batchSize : 100;
        int createdCount = 0;

        List<WalletProvisioningEvent> events = provisioningEventRepository.findAllByOrderByProcessedAtDesc(
                PageRequest.of(0, effectiveBatchSize)
        );

        for (WalletProvisioningEvent event : events) {
            boolean walletExists = walletRepository.findByUserId(event.getUserId()).isPresent();
            if (walletExists) {
                continue;
            }

            Wallet wallet = new Wallet();
            wallet.setUserId(event.getUserId());
            try {
                walletRepository.save(wallet);
                createdCount += 1;
            } catch (DataIntegrityViolationException duplicateWallet) {
                LOGGER.info("Skip duplicate reconciliation wallet userId={}", event.getUserId());
            }
        }

        if (createdCount > 0) {
            LOGGER.info("Reconciliation created {} missing wallets", createdCount);
        }
        return createdCount;
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private Wallet executeHeldBalanceOperation(String userId, BigDecimal amount, String transactionType) {
        validatePositiveAmount(amount);

        Wallet wallet = findWalletByUserId(userId);
        if (wallet.getActiveBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient active balance");
        }

        wallet.setActiveBalance(wallet.getActiveBalance().subtract(amount));
        wallet.setHeldBalance(wallet.getHeldBalance().add(amount));
        transactionRepository.save(new WalletTransaction(userId, transactionType, amount));
        return walletRepository.save(wallet);
    }
}
