package com.example.JavaCode.Controller;

import com.example.JavaCode.DTO.WalletOperationRequest;
import com.example.JavaCode.model.Wallet;
import com.example.JavaCode.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    @Autowired
    private WalletRepository walletRepository;

    @PostMapping
    public ResponseEntity<Wallet> createWallet() {
        Wallet wallet = new Wallet();
        return ResponseEntity.ok(walletRepository.save(wallet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWallet(@PathVariable UUID id) {

        Optional<Wallet> optionalWallet = walletRepository.findById(id);

        if (!optionalWallet.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        walletRepository.deleteById(id);
        return ResponseEntity.ok("Wallet successfully deleted");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWallet(@PathVariable UUID id) {
        Optional<Wallet> wallet = walletRepository.findById(id);
        return wallet.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/operation")
    public ResponseEntity<Wallet> performOperation(@RequestBody WalletOperationRequest request) {

        Optional<Wallet> optionalWallet = walletRepository.findById(request.getWalletId());

        if (!optionalWallet.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Wallet wallet = optionalWallet.get();
        int amount = request.getAmount();

        if ("DEPOSIT".equalsIgnoreCase(request.getOperationType())) {
            wallet.setAmount(wallet.getAmount() + amount);
        } else if ("WITHDRAW".equalsIgnoreCase(request.getOperationType())) {
            if (wallet.getAmount() < amount) {
                return ResponseEntity.badRequest().body(null);
            }
            wallet.setAmount(wallet.getAmount() - amount);
        } else {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(walletRepository.save(wallet));
    }
}
