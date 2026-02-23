package com.atbp.laba3;

import com.atbp.laba3.card.CardService;
import com.atbp.laba3.card.CardStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@DisplayName("ЛР-2: Тестирование расчёта стоимости проезда с CardSystem API")
public class FareCalculatorTest {

    private CardService cardServiceMock;
    private FareCalculator fareCalculator;

    @BeforeEach
    void setUp() {
        cardServiceMock = Mockito.mock(CardService.class);
        fareCalculator = new FareCalculator(cardServiceMock);
    }

    @ParameterizedTest(name = "Взрослый билет, зоны={0}, ожидаем {1}")
    @CsvSource({
            "1, 100.0",
            "2, 150.0",
            "3, 200.0"
    })
    void testAdultFare(int zones, double expected) {

        Mockito.when(cardServiceMock.checkStatus("CARD1"))
                .thenReturn(CardStatus.ACTIVE);
        Mockito.when(cardServiceMock.getBalance("CARD1"))
                .thenReturn(1000.0);

        double result = fareCalculator.calculateFare(zones, "adult", "CARD1");

        Assertions.assertEquals(expected, result);
    }

    @ParameterizedTest(name = "Детский билет, зоны={0}, ожидаем {1}")
    @CsvSource({
            "1, 50.0",
            "2, 75.0",
            "3, 100.0"
    })
    void testChildFare(int zones, double expected) {

        Mockito.when(cardServiceMock.checkStatus("CARD2"))
                .thenReturn(CardStatus.ACTIVE);
        Mockito.when(cardServiceMock.getBalance("CARD2"))
                .thenReturn(1000.0);

        double result = fareCalculator.calculateFare(zones, "child", "CARD2");

        Assertions.assertEquals(expected, result);
    }

    @Test
    @DisplayName("Ошибка: зона меньше допустимой")
    void testZoneTooLow() {

        Mockito.when(cardServiceMock.checkStatus(ArgumentMatchers.any()))
                .thenReturn(CardStatus.ACTIVE);
        Mockito.when(cardServiceMock.getBalance(ArgumentMatchers.any()))
                .thenReturn(500.0);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fareCalculator.calculateFare(0, "adult", "CARD"));
    }

    @Test
    @DisplayName("Ошибка: зона больше допустимой")
    void testZoneTooHigh() {

        Mockito.when(cardServiceMock.checkStatus(ArgumentMatchers.any()))
                .thenReturn(CardStatus.ACTIVE);
        Mockito.when(cardServiceMock.getBalance(ArgumentMatchers.any()))
                .thenReturn(500.0);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fareCalculator.calculateFare(4, "adult", "CARD"));
    }

    @Test
    @DisplayName("Ошибка: неверный тип билета")
    void testInvalidTicketType() {

        Mockito.when(cardServiceMock.checkStatus(ArgumentMatchers.any()))
                .thenReturn(CardStatus.ACTIVE);
        Mockito.when(cardServiceMock.getBalance(ArgumentMatchers.any()))
                .thenReturn(500.0);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fareCalculator.calculateFare(1, "student", "CARD"));
    }

    @Test
    @DisplayName("Ошибка: тип билета равен null")
    void testNullTicketType() {

        Mockito.when(cardServiceMock.checkStatus(ArgumentMatchers.any()))
                .thenReturn(CardStatus.ACTIVE);
        Mockito.when(cardServiceMock.getBalance(ArgumentMatchers.any()))
                .thenReturn(500.0);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fareCalculator.calculateFare(1, null, "CARD"));
    }

    @Test
    @DisplayName("Карта заблокирована — проезд невозможен")
    void testBlockedCard() {

        Mockito.when(cardServiceMock.checkStatus("BLOCKED"))
                .thenReturn(CardStatus.BLOCKED);

        Assertions.assertThrows(IllegalStateException.class,
                () -> fareCalculator.calculateFare(1, "adult", "BLOCKED"));

        Mockito.verify(cardServiceMock).checkStatus("BLOCKED");
        Mockito.verify(cardServiceMock, Mockito.never()).getBalance(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Статус карты InsufficientFunds")
    void testInsufficientFundsStatus() {

        Mockito.when(cardServiceMock.checkStatus("NOFUNDS"))
                .thenReturn(CardStatus.INSUFFICIENT_FUNDS);

        Assertions.assertThrows(IllegalStateException.class,
                () -> fareCalculator.calculateFare(2, "adult", "NOFUNDS"));

        Mockito.verify(cardServiceMock).checkStatus("NOFUNDS");
        Mockito.verify(cardServiceMock, Mockito.never()).getBalance(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Сложный сценарий: карта активна, но баланс меньше стоимости")
    void testActiveCardLowBalance() {

        Mockito.when(cardServiceMock.checkStatus("LOWBAL"))
                .thenReturn(CardStatus.ACTIVE);
        Mockito.when(cardServiceMock.getBalance("LOWBAL"))
                .thenReturn(50.0);

        Assertions.assertThrows(IllegalStateException.class,
                () -> fareCalculator.calculateFare(1, "adult", "LOWBAL"));

        Mockito.verify(cardServiceMock).checkStatus("LOWBAL");
        Mockito.verify(cardServiceMock).getBalance("LOWBAL");
    }
}
