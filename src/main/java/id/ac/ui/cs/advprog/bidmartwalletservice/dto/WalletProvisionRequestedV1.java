package id.ac.ui.cs.advprog.bidmartwalletservice.dto;

import java.time.LocalDateTime;

public record WalletProvisionRequestedV1(
        String eventId,
        String userId,
        String email,
        LocalDateTime occurredAt,
        String source
) {}
