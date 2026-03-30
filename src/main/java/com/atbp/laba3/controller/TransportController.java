package com.atbp.laba3.controller;

import com.atbp.laba3.FareCalculator;
import com.atbp.laba3.card.CardService;
import com.atbp.laba3.card.CardStatus;
import com.atbp.laba3.dto.RideRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TransportController {

    private final CardService cardService;
    private final FareCalculator fareCalculator;

    public TransportController(CardService cardService) {
        this.cardService = cardService;
        this.fareCalculator = new FareCalculator(cardService);
    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<?> checkCard(@PathVariable String cardId) {
        CardStatus status = cardService.checkStatus(cardId);
        double balance = cardService.getBalance(cardId);
        return ResponseEntity.ok(Map.of(
                "status", status,
                "balance", balance
        ));
    }

    @PostMapping("/transport/ride")
    public ResponseEntity<?> ride(@RequestBody RideRequest request) {
        try {
            double price = fareCalculator.calculateFare(
                    request.getZones(),
                    request.getTicketType(),
                    request.getCardId()
            );

            double newBalance = cardService.getBalance(request.getCardId());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "price", price,
                    "balance", newBalance
            ));
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("заблокирована")) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(Map.of(
                "status", "online",
                "timestamp", new java.util.Date()
        ));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset() {
        cardService.resetToInitialState();
        return ResponseEntity.ok(Map.of("status", "reset completed"));
    }
}
