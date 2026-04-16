package id.ac.ui.cs.advprog.bidmartwalletservice.consumer;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.WalletProvisionRequestedV1;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletProvisioningConsumer {
    private final WalletService walletService;
    @Autowired
    public WalletProvisioningConsumer(WalletService walletService) {
        this.walletService = walletService;
    }
    @RabbitListener(queues = "${app.wallet.provisioning.queue}")
    public void consumeProvisioningEvent(WalletProvisionRequestedV1 event) {
        walletService.provisionWallet(event);
    }
}
