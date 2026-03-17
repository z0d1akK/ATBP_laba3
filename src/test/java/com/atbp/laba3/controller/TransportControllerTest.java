package com.atbp.laba3.controller;

import com.atbp.laba3.card.CardService;
import com.atbp.laba3.card.CardStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransportController.class)
@DisplayName("ЛР-3: Тестирование REST API расчета стоимости проезда")
class TransportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Nested
    @DisplayName("Позитивные сценарии (200 OK)")
    class SuccessTests {

        @Test
        @DisplayName("1 зона, взрослый билет 100р")
        void shouldReturn100ForOneZone() throws Exception {
            Mockito.when(cardService.checkStatus("CARD1"))
                    .thenReturn(CardStatus.ACTIVE);
            Mockito.when(cardService.getBalance("CARD1"))
                    .thenReturn(1000.0);

            mockMvc.perform(post("/api/transport/ride")
                            .contentType("application/json")
                            .content("""
                                    {
                                      "zones": 1,
                                      "ticketType": "adult",
                                      "cardId": "CARD1"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.price").value(100.0));
        }

        @Test
        @DisplayName("2 зоны, взрослый билет 150р")
        void shouldReturn150ForTwoZones() throws Exception {

            Mockito.when(cardService.checkStatus("CARD1"))
                    .thenReturn(CardStatus.ACTIVE);
            Mockito.when(cardService.getBalance("CARD1"))
                    .thenReturn(1000.0);

            mockMvc.perform(post("/api/transport/ride")
                            .contentType("application/json")
                            .content("""
                                    {
                                      "zones": 2,
                                      "ticketType": "adult",
                                      "cardId": "CARD1"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.price").value(150.0));
        }

        @Test
        @DisplayName("3 зоны, взрослый билет 200р")
        void shouldReturn200ForThreeZones() throws Exception {

            Mockito.when(cardService.checkStatus("CARD1"))
                    .thenReturn(CardStatus.ACTIVE);
            Mockito.when(cardService.getBalance("CARD1"))
                    .thenReturn(1000.0);

            mockMvc.perform(post("/api/transport/ride")
                            .contentType("application/json")
                            .content("""
                                    {
                                      "zones": 3,
                                      "ticketType": "adult",
                                      "cardId": "CARD1"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.price").value(200.0));
        }
    }

    @Nested
    @DisplayName("Негативные сценарии")
    class ErrorTests {

        @Test
        @DisplayName("Карта заблокирована 403 Forbidden")
        void shouldReturn403WhenCardBlocked() throws Exception {

            Mockito.when(cardService.checkStatus("BLOCKED"))
                    .thenReturn(CardStatus.BLOCKED);

            mockMvc.perform(post("/api/transport/ride")
                            .contentType("application/json")
                            .content("""
                                    {
                                      "zones": 1,
                                      "ticketType": "adult",
                                      "cardId": "BLOCKED"
                                    }
                                    """))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Некорректная зона (0) 400 Bad Request")
        void shouldReturn400ForInvalidZone() throws Exception {

            Mockito.when(cardService.checkStatus("CARD1"))
                    .thenReturn(CardStatus.ACTIVE);
            Mockito.when(cardService.getBalance("CARD1"))
                    .thenReturn(1000.0);

            mockMvc.perform(post("/api/transport/ride")
                            .contentType("application/json")
                            .content("""
                                    {
                                      "zones": 0,
                                      "ticketType": "adult",
                                      "cardId": "CARD1"
                                    }
                                    """))
                    .andExpect(status().isBadRequest());
        }
    }
}