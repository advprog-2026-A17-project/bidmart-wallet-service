package id.ac.ui.cs.advprog.bidmartwalletservice.dto;

import java.math.BigDecimal;

public record WalletCreateRequest(
        String userId,
        BigDecimal activeBalance,
        BigDecimal heldBalance
) {
}
