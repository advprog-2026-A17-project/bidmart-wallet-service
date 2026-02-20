package id.ac.ui.cs.advprog.bidmartwalletservice.controller;

import id.ac.ui.cs.advprog.bidmartwalletservice.model.Wallet;
import id.ac.ui.cs.advprog.bidmartwalletservice.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        mockMvc.perform(get("/user-123"))
                .andExpect(status().isOk());
    }

    @Test
    void testTopUpSuccess() throws Exception {
        Wallet updatedWallet = new Wallet();
        updatedWallet.setUserId("user-123");
        updatedWallet.setActiveBalance(15000L);

        when(walletService.topUpBalance("user-123", 5000L)).thenReturn(updatedWallet);

        mockMvc.perform(post("/user-123/top-up")
                        .param("amount", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeBalance").value(15000));
    }
}
