package id.ac.ui.cs.advprog.bidmartwalletservice.consumer;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.WalletProvisionRequestedV1;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WalletProvisioningConsumerTest {

    @Test
    void testConsumeProvisioningEventSuccessIncrementsConsumedCounter() {
        WalletService walletService = mock(WalletService.class);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        WalletProvisioningConsumer consumer = new WalletProvisioningConsumer(walletService, meterRegistry);

        WalletProvisionRequestedV1 event = new WalletProvisionRequestedV1(
                "evt-1",
                "user-1",
                "user-1@example.com",
                Instant.now(),
                "auth-service"
        );

        consumer.consumeProvisioningEvent(event);

        verify(walletService).provisionWallet(event);
        assertEquals(1.0, meterRegistry.counter("wallet.provisioning.events.consumed").count());
        assertEquals(0.0, meterRegistry.counter("wallet.provisioning.events.failed").count());
    }

    @Test
    void testConsumeProvisioningEventFailureIncrementsFailedCounterAndRethrows() {
        WalletService walletService = mock(WalletService.class);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        WalletProvisioningConsumer consumer = new WalletProvisioningConsumer(walletService, meterRegistry);

        WalletProvisionRequestedV1 event = new WalletProvisionRequestedV1(
                "evt-1",
                "user-1",
                "user-1@example.com",
                Instant.now(),
                "auth-service"
        );
        doThrow(new IllegalStateException("boom")).when(walletService).provisionWallet(event);

        assertThrows(
                IllegalStateException.class,
                () -> consumer.consumeProvisioningEvent(event)
        );
        assertEquals(1.0, meterRegistry.counter("wallet.provisioning.events.consumed").count());
        assertEquals(1.0, meterRegistry.counter("wallet.provisioning.events.failed").count());
    }
}
