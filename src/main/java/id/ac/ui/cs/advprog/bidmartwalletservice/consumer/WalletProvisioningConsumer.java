package id.ac.ui.cs.advprog.bidmartwalletservice.consumer;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.WalletProvisionRequestedV1;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WalletProvisioningConsumer {

    private final WalletService walletService;
    private final Counter consumedEventsCounter;
    private final Counter failedEventsCounter;

    public WalletProvisioningConsumer(WalletService walletService, MeterRegistry meterRegistry) {
        this.walletService = walletService;
        this.consumedEventsCounter = Counter.builder("wallet.provisioning.events.consumed")
                .description("Total wallet provisioning events consumed by wallet service")
                .register(meterRegistry);
        this.failedEventsCounter = Counter.builder("wallet.provisioning.events.failed")
                .description("Total wallet provisioning events failed by wallet service consumer")
                .register(meterRegistry);
    }

    @RabbitListener(
            queues = "${app.wallet.provisioning.queue:wallet.provision.queue}",
            containerFactory = "walletProvisioningListenerContainerFactory"
    )
    public void consumeProvisioningEvent(WalletProvisionRequestedV1 event) {
        consumedEventsCounter.increment();
        try {
            walletService.provisionWallet(event);
        } catch (RuntimeException ex) {
            failedEventsCounter.increment();
            throw ex;
        }
    }
}
