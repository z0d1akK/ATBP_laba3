package com.atbp.laba3.card;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class CardServiceImpl implements CardService {

    private final Map<String, Double> balances = new HashMap<>();
    private final Map<String, CardStatus> statuses = new HashMap<>();

    private final Map<String, Double> initialBalances = new HashMap<>();
    private final Map<String, CardStatus> initialStatuses = new HashMap<>();

    public CardServiceImpl() {
        initialBalances.put("CARD1", 1000.0);
        initialBalances.put("CARD2", 50.0);
        initialBalances.put("BLOCKED", 500.0);

        initialStatuses.put("CARD1", CardStatus.ACTIVE);
        initialStatuses.put("CARD2", CardStatus.ACTIVE);
        initialStatuses.put("BLOCKED", CardStatus.BLOCKED);

        resetToInitialState();
    }

    @Override
    public CardStatus checkStatus(String cardId) {
        return statuses.getOrDefault(cardId, CardStatus.ACTIVE);
    }

    @Override
    public double getBalance(String cardId) {
        return balances.getOrDefault(cardId, 0.0);
    }

    @Override
    public void deductBalance(String cardId, double amount) {
        if (balances.containsKey(cardId)) {
            double currentBalance = balances.get(cardId);
            balances.put(cardId, currentBalance - amount);
        }
    }

    @Override
    public void resetToInitialState() {
        balances.clear();
        statuses.clear();

        balances.putAll(initialBalances);
        statuses.putAll(initialStatuses);
    }
}