package id.ac.ui.cs.advprog.bidmartwalletservice.scheduler;

import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WalletProvisioningReconciliationSchedulerTest {

    @Test
    void testReconcileProvisionedWalletsUsesConfiguredBatchSize() {
        WalletService walletService = mock(WalletService.class);
        WalletProvisioningReconciliationScheduler scheduler = new WalletProvisioningReconciliationScheduler(walletService);
        ReflectionTestUtils.setField(scheduler, "batchSize", 77);

        scheduler.reconcileProvisionedWallets();

        verify(walletService).reconcileProvisionedWallets(77);
    }
}
