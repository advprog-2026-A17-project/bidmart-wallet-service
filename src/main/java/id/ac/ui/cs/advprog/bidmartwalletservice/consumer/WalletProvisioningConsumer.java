package id.ac.ui.cs.advprog.bidmartwalletservice.consumer;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.WalletProvisionRequestedV1;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WalletProvisioningConsumer {

    private final WalletService walletService;

    public WalletProvisioningConsumer(WalletService walletService) {
        this.walletService = walletService;
    }

    @RabbitListener(
            queues = "${app.wallet.provisioning.queue:wallet.provision.queue}",
            containerFactory = "walletProvisioningListenerContainerFactory"
    )
    public void consumeProvisioningEvent(WalletProvisionRequestedV1 event) {
        walletService.provisionWallet(event);
    }
}
