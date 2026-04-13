package id.ac.ui.cs.advprog.bidmartwalletservice.controller;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void testGetWalletSuccess() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setUserId("user-123");
        wallet.setActiveBalance(10000L);

        when(walletService.findWalletByUserId("user-123")).thenReturn(wallet);

        mockMvc.perform(get("/api/v1/wallet/user-123"))
                .andExpect(status().isOk());
    }

    @Test
    void testTopUpSuccess() throws Exception {
        Wallet updatedWallet = new Wallet();
        updatedWallet.setUserId("user-123");
        updatedWallet.setActiveBalance(15000L);

        when(walletService.topUpBalance("user-123", 5000L)).thenReturn(updatedWallet);

        mockMvc.perform(post("/api/v1/wallet/user-123/top-up")
                        .param("amount", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeBalance").value(15000));
    }

    @Test
    void testHoldFundsSuccess() throws Exception {
        Wallet updatedWallet = new Wallet();
        updatedWallet.setUserId("user-123");
        updatedWallet.setActiveBalance(7500L);
        updatedWallet.setHeldBalance(2500L);

        when(walletService.holdFunds("user-123", 2500)).thenReturn(updatedWallet);

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
                .andExpect(jsonPath("$.activeBalance").value(7500))
                .andExpect(jsonPath("$.heldBalance").value(2500));
    }

    @Test
    void testReleaseFundsSuccess() throws Exception {
        Wallet updatedWallet = new Wallet();
        updatedWallet.setUserId("user-123");
        updatedWallet.setActiveBalance(9000L);
        updatedWallet.setHeldBalance(1000L);

        when(walletService.releaseFunds("user-123", 1000)).thenReturn(updatedWallet);

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
                .andExpect(jsonPath("$.activeBalance").value(9000))
                .andExpect(jsonPath("$.heldBalance").value(1000));
    }

    @Test
    void testConvertFundsSuccess() throws Exception {
        Wallet updatedWallet = new Wallet();
        updatedWallet.setUserId("user-123");
        updatedWallet.setActiveBalance(9000L);
        updatedWallet.setHeldBalance(0L);

        when(walletService.convertHeldFunds("user-123", 1000)).thenReturn(updatedWallet);

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
                .andExpect(jsonPath("$.activeBalance").value(9000))
                .andExpect(jsonPath("$.heldBalance").value(0));
    }
}
