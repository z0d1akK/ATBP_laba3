package com.atbp.laba3;

import com.atbp.laba3.card.CardService;
import com.atbp.laba3.card.CardStatus;

public class FareCalculator {

    private final CardService cardService;

    public FareCalculator(CardService cardService) {
        this.cardService = cardService;
    }

    public double calculateFare(int zones, String ticketType, String cardId) {
        CardStatus status = cardService.checkStatus(cardId);
        if (status == CardStatus.BLOCKED) {
            throw new IllegalStateException("Карта заблокирована");
        }

        double price = getPrice(zones, ticketType);
        double balance = cardService.getBalance(cardId);

        if (balance < price) {
            throw new IllegalStateException("Баланс карты меньше стоимости поездки");
        }

        cardService.deductBalance(cardId, price);

        return Math.round(price * 100.0) / 100.0;
    }

    private static double getPrice(int zones, String ticketType) {
        double price = switch (zones) {
            case 1 -> 100.0;
            case 2 -> 150.0;
            case 3 -> 200.0;
            default -> throw new IllegalArgumentException("Количество зон должно быть 1, 2 или 3.");
        };

        if (ticketType == null) {
            throw new IllegalArgumentException("Тип билета должен быть указан.");
        }

        switch (ticketType.toLowerCase()) {
            case "adult" -> { }
            case "child" -> price *= 0.5;
            default -> throw new IllegalArgumentException("Тип билета должен быть 'adult' или 'child'.");
        }
        return price;
    }
}