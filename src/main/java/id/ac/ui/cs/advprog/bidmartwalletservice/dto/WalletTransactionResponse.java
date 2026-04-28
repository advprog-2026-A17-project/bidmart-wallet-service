package id.ac.ui.cs.advprog.bidmartwalletservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletTransactionResponse(
        String id,
        String userId,
        String type,
        BigDecimal amount,
        LocalDateTime timestamp
) {
}
