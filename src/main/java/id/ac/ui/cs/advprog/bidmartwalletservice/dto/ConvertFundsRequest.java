package id.ac.ui.cs.advprog.bidmartwalletservice.dto;

import java.math.BigDecimal;

public record ConvertFundsRequest(String userId, BigDecimal amount, String description) {
}
