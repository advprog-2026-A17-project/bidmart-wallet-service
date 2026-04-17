package id.ac.ui.cs.advprog.bidmartwalletservice.dto;

import java.math.BigDecimal;

public record WalletResponse(
        String id,
        String userId,
        BigDecimal activeBalance,
        BigDecimal heldBalance
) {
}
