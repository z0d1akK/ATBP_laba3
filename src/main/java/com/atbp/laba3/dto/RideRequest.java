package com.atbp.laba3.dto;

import lombok.Data;

@Data
public class RideRequest {
    private int zones;
    private String ticketType;
    private String cardId;

}