package com.example.JavaCode.Controller;

import com.example.JavaCode.model.Wallet;
import com.example.JavaCode.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class WalletControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletController walletController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
    }

    @Test
    void testDeleteWallet_Success() throws Exception {
        UUID walletId = UUID.randomUUID();
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet()));

        mockMvc.perform(delete("/api/v1/wallets/{id}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(walletRepository, times(1)).deleteById(walletId);
    }

    @Test
    void testDeleteWallet_NotFound() throws Exception {
        UUID walletId = UUID.randomUUID();
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/v1/wallets/{id}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(walletRepository, times(0)).deleteById(any(UUID.class));
    }

    @Test
    void testCreateWallet() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists()) // Если у вас есть поле id
                .andExpect(jsonPath("$.amount").value(0)); // Если у вас есть поле balance и хотите проверить его значение
    }

    @Test
    void testGetWalletFound() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setAmount(0);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        mockMvc.perform(get("/api/v1/wallets/" + walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(walletId.toString()))
                .andExpect(jsonPath("$.amount").value(0));
    }

    @Test
    void testGetWalletNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/wallets/" + walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPerformDepositOperation() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setAmount(100);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallets/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"" + walletId + "\",\"operationType\":\"DEPOSIT\",\"amount\":50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150));
    }

    @Test
    void testPerformWithdrawOperation() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setAmount(100);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallets/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"" + walletId + "\",\"operationType\":\"WITHDRAW\",\"amount\":50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(50));
    }

    @Test
    void testPerformWithdrawInsufficientFunds() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setAmount(30);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        mockMvc.perform(post("/api/v1/wallets/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"" + walletId + "\",\"operationType\":\"WITHDRAW\",\"amount\":50}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPerformOperationWalletNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/wallets/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"" + walletId + "\",\"operationType\":\"DEPOSIT\",\"amount\":50}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPerformOperationInvalidType() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setAmount(100);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        mockMvc.perform(post("/api/v1/wallets/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"" + walletId + "\",\"operationType\":\"INVALID\",\"amount\":50}"))
                .andExpect(status().isBadRequest());
    }
}