package id.ac.ui.cs.advprog.bidmartwalletservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Wallet {
    private Long id;
    private String userId;
    private double activeBalance;
    private double heldBalance;
}
