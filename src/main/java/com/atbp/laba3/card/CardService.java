package com.atbp.laba3.card;

public interface CardService {
    CardStatus checkStatus(String cardId);
    double getBalance(String cardId);
}