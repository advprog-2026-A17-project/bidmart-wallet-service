package id.ac.ui.cs.advprog.bidmartwalletservice.controller;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.model.WalletTransaction;
import id.ac.ui.cs.advprog.bidmartwalletservice.repository.WalletTransactionRepository;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    @MockitoBean
    private WalletTransactionRepository transactionRepository;

    @Test
    void testGetWalletSuccess() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setId("wallet-123");
        wallet.setUserId("user-123");
        wallet.setActiveBalance(new BigDecimal("10000"));
        wallet.setHeldBalance(BigDecimal.ZERO);

        when(walletService.findWalletByUserId("user-123")).thenReturn(wallet);

        mockMvc.perform(get("/api/v1/wallet/user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet-123"))
                .andExpect(jsonPath("$.userId").value("user-123"));
    }

    @Test
    void testTopUpSuccess() throws Exception {
        Wallet updatedWallet = new Wallet();
        updatedWallet.setId("wallet-123");
        updatedWallet.setUserId("user-123");
        updatedWallet.setActiveBalance(new BigDecimal("15000"));
        updatedWallet.setHeldBalance(BigDecimal.ZERO);

        when(walletService.topUpBalance("user-123", new BigDecimal("5000"))).thenReturn(updatedWallet);

        mockMvc.perform(post("/api/v1/wallet/user-123/top-up").param("amount", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet-123"))
                .andExpect(jsonPath("$.activeBalance").value(15000));
    }

    @Test
    void testHoldFundsSuccess() throws Exception {
        Wallet updatedWallet = new Wallet();
        updatedWallet.setId("wallet-123");
        updatedWallet.setUserId("user-123");
        updatedWallet.setActiveBalance(new BigDecimal("7500"));
        updatedWallet.setHeldBalance(new BigDecimal("2500"));

        when(walletService.holdFunds("user-123", new BigDecimal("2500"))).thenReturn(updatedWallet);

        mockMvc.perform(post("/api/v1/wallet/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "user-123",
                                  "amount": 2500,
                                  "description": "Bid hold"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet-123"))
                .andExpect(jsonPath("$.activeBalance").value(7500))
                .andExpect(jsonPath("$.heldBalance").value(2500));
    }

    @Test
    void testReleaseFundsSuccess() throws Exception {
        Wallet updatedWallet = new Wallet();
        updatedWallet.setId("wallet-123");
        updatedWallet.setUserId("user-123");
        updatedWallet.setActiveBalance(new BigDecimal("9000"));
        updatedWallet.setHeldBalance(new BigDecimal("1000"));

        when(walletService.releaseFunds("user-123", new BigDecimal("1000"))).thenReturn(updatedWallet);

        mockMvc.perform(post("/api/v1/wallet/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "user-123",
                                  "amount": 1000,
                                  "description": "Outbid"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet-123"))
                .andExpect(jsonPath("$.activeBalance").value(9000))
                .andExpect(jsonPath("$.heldBalance").value(1000));
    }

    @Test
    void testConvertFundsSuccess() throws Exception {
        Wallet updatedWallet = new Wallet();
        updatedWallet.setId("wallet-123");
        updatedWallet.setUserId("user-123");
        updatedWallet.setActiveBalance(new BigDecimal("9000"));
        updatedWallet.setHeldBalance(BigDecimal.ZERO);

        when(walletService.convertHeldFunds("user-123", new BigDecimal("1000"))).thenReturn(updatedWallet);

        mockMvc.perform(post("/api/v1/wallet/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "user-123",
                                  "amount": 1000,
                                  "description": "Auction won"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet-123"))
                .andExpect(jsonPath("$.activeBalance").value(9000))
                .andExpect(jsonPath("$.heldBalance").value(0));
    }

    @Test
    void testGetWalletDetailSuccess() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setId("wallet-123");
        wallet.setUserId("user-123");
        wallet.setActiveBalance(new BigDecimal("7000"));
        wallet.setHeldBalance(new BigDecimal("3000"));

        WalletTransaction transaction = new WalletTransaction();
        transaction.setId("tx-1");
        transaction.setUserId("user-123");
        transaction.setType("HOLD");
        transaction.setAmount(new BigDecimal("3000"));
        transaction.setTimestamp(LocalDateTime.of(2026, 1, 1, 12, 0));

        when(walletService.findWalletByUserId("user-123")).thenReturn(wallet);
        when(transactionRepository.findAllByUserIdOrderByTimestampDesc("user-123")).thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/v1/wallet/user-123/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wallet.id").value("wallet-123"))
                .andExpect(jsonPath("$.wallet.userId").value("user-123"))
                .andExpect(jsonPath("$.history[0].id").value("tx-1"))
                .andExpect(jsonPath("$.history[0].type").value("HOLD"))
                .andExpect(jsonPath("$.history[0].amount").value(3000));
    }

    @Test
    void testAddWalletManuallyUsesCreateRequestDefaults() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setId("wallet-999");
        wallet.setUserId("new-user");
        wallet.setActiveBalance(BigDecimal.ZERO);
        wallet.setHeldBalance(BigDecimal.ZERO);

        when(walletService.create(any(Wallet.class))).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallet/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "new-user"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet-999"))
                .andExpect(jsonPath("$.userId").value("new-user"))
                .andExpect(jsonPath("$.activeBalance").value(0))
                .andExpect(jsonPath("$.heldBalance").value(0));
    }

    @Test
    void testTryBidAndWithdrawCompatibilityEndpoints() throws Exception {
        Wallet tryBidWallet = new Wallet();
        tryBidWallet.setId("wallet-trybid");
        tryBidWallet.setUserId("user-123");
        tryBidWallet.setActiveBalance(new BigDecimal("6000"));
        tryBidWallet.setHeldBalance(new BigDecimal("4000"));

        Wallet withdrawWallet = new Wallet();
        withdrawWallet.setId("wallet-withdraw");
        withdrawWallet.setUserId("user-123");
        withdrawWallet.setActiveBalance(new BigDecimal("5000"));
        withdrawWallet.setHeldBalance(new BigDecimal("1000"));

        when(walletService.bidding("user-123", new BigDecimal("1000"))).thenReturn(tryBidWallet);
        when(walletService.withdrawal("user-123", new BigDecimal("500"))).thenReturn(withdrawWallet);

        mockMvc.perform(post("/api/v1/wallet/user-123/trybid").param("amount", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet-trybid"))
                .andExpect(jsonPath("$.heldBalance").value(4000));

        mockMvc.perform(post("/api/v1/wallet/user-123/withdraw").param("amount", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet-withdraw"))
                .andExpect(jsonPath("$.activeBalance").value(5000));
    }
}
