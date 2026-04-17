package id.ac.ui.cs.advprog.bidmartwalletservice.model;

import id.ac.ui.cs.advprog.bidmartwalletservice.dto.WalletProvisionRequestedV1;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "wallet_provisioning_events")
@Getter
@Setter
@NoArgsConstructor
public class WalletProvisioningEvent {

    @Id
    @Column(name = "event_id", nullable = false, length = 64)
    private String eventId;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "source", nullable = false, length = 128)
    private String source;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public static WalletProvisioningEvent from(WalletProvisionRequestedV1 event, Instant processedAt) {
        WalletProvisioningEvent provisioningEvent = new WalletProvisioningEvent();
        provisioningEvent.setEventId(event.eventId());
        provisioningEvent.setUserId(event.userId());
        provisioningEvent.setEmail(event.email());
        provisioningEvent.setOccurredAt(event.occurredAt());
        provisioningEvent.setSource(event.source());
        provisioningEvent.setProcessedAt(processedAt);
        return provisioningEvent;
    }
}
