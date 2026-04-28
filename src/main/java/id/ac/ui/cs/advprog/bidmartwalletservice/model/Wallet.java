package id.ac.ui.cs.advprog.bidmartwalletservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private BigDecimal activeBalance = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal heldBalance = BigDecimal.ZERO;

}