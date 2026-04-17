package id.ac.ui.cs.advprog.bidmartwalletservice.scheduler;

import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WalletProvisioningReconciliationScheduler {

    private final WalletService walletService;

    @Value("${app.wallet.provisioning.reconciliation.batch-size:200}")
    private int batchSize;

    public WalletProvisioningReconciliationScheduler(WalletService walletService) {
        this.walletService = walletService;
    }

    @Scheduled(fixedDelayString = "${app.wallet.provisioning.reconciliation.fixed-delay-ms:300000}")
    public void reconcileProvisionedWallets() {
        walletService.reconcileProvisionedWallets(batchSize);
    }
}
