package com.atbp.laba3.card;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CardServiceImpl implements CardService {

    private final Map<String, Double> balances = new HashMap<>();
    private final Map<String, CardStatus> statuses = new HashMap<>();

    public CardServiceImpl() {
        balances.put("CARD1", 1000.0);
        balances.put("CARD2", 50.0);
        balances.put("BLOCKED", 500.0);

        statuses.put("CARD1", CardStatus.ACTIVE);
        statuses.put("CARD2", CardStatus.ACTIVE);
        statuses.put("BLOCKED", CardStatus.BLOCKED);
    }

    @Override
    public CardStatus checkStatus(String cardId) {
        return statuses.getOrDefault(cardId, CardStatus.ACTIVE);
    }

    @Override
    public double getBalance(String cardId) {
        return balances.getOrDefault(cardId, 0.0);
    }
}