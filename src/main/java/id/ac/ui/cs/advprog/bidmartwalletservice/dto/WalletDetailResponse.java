package id.ac.ui.cs.advprog.bidmartwalletservice.dto;

import java.util.List;

public record WalletDetailResponse(
        WalletResponse wallet,
        List<WalletTransactionResponse> history
) {
}
