package id.ac.ui.cs.advprog.bidmartwalletservice.dto;

import java.time.Instant;

public record WalletProvisionRequestedV1(
        String eventId,
        String userId,
        String email,
        Instant occurredAt,
        String source
) {}
