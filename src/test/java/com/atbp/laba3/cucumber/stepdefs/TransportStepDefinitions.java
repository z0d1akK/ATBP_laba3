package com.atbp.laba3.cucumber.stepdefs;

import com.atbp.laba3.cucumber.SpringIntegrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
public class TransportStepDefinitions extends SpringIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<Map> response;
    private String currentCardId;
    private double currentBalance;
    private String currentStatus;
    private final Map<String, Object> requestBody = new HashMap<>();

    @Given("сервис доступен")
    public void serviceIsAvailable() {
        String url = baseUrl() + "/status";
        ResponseEntity<Map> statusResponse = restTemplate.getForEntity(url, Map.class);

        assertEquals(200, statusResponse.getStatusCode().value());
        assertNotNull(statusResponse.getBody());
        assertEquals("online", statusResponse.getBody().get("status"));
    }

    @Given("карта {string} имеет статус {string} и баланс {double}")
    public void setupCard(String cardId, String status, double balance) {
        this.currentCardId = cardId;
        this.currentStatus = status;
        this.currentBalance = balance;

        System.out.println("Setup card: " + cardId + " with status: " + status + " and balance: " + balance);

        String url = baseUrl() + "/cards/" + cardId;
        ResponseEntity<Map> cardResponse = restTemplate.getForEntity(url, Map.class);
        assertEquals(200, cardResponse.getStatusCode().value(), "Карта должна существовать");
    }

    @When("я отправляю POST запрос на {string} с телом:")
    public void sendPostRequest(String path, String body) {
        String url = baseUrl() + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        response = restTemplate.postForEntity(url, request, Map.class);
    }

    @Then("API возвращает статус-код {int}")
    public void verifyStatus(int expectedStatus) {
        assertNotNull(response, "Response должен быть не null");
        assertEquals(expectedStatus, response.getStatusCode().value());
    }

    @Then("статус карты соответствует ожидаемому {string}")
    public void verifyCardStatus(String expectedStatus) {
        assertNotNull(response.getBody(), "Response body должен быть не null");

        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("status"), "Ответ должен содержать поле 'status'");

        String actualStatus = body.get("status").toString();
        assertEquals(expectedStatus, actualStatus,
                "Статус карты должен соответствовать ожидаемому");
    }

    @Then("ответ содержит цену {double}")
    public void verifyPrice(double expectedPrice) {
        assertNotNull(response.getBody(), "Response body должен быть не null");

        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("price"), "Ответ должен содержать поле 'price'");

        Double actualPrice = Double.valueOf(body.get("price").toString());
        assertEquals(expectedPrice, actualPrice, 0.01,
                "Цена должна соответствовать ожидаемой");
    }

    @Then("ответ содержит успешный статус")
    public void verifySuccessStatus() {
        assertNotNull(response.getBody(), "Response body должен быть не null");

        Map<String, Object> body = response.getBody();
        if (body.containsKey("status")) {
            assertEquals("success", body.get("status"),
                    "Статус ответа должен быть 'success'");
        }
    }

    @Then("цена положительная")
    public void verifyPricePositive() {
        assertNotNull(response.getBody(), "Response body должен быть не null");

        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("price"), "Ответ должен содержать поле 'price'");

        Double actualPrice = Double.valueOf(body.get("price").toString());
        assertTrue(actualPrice > 0, "Цена должна быть положительной");
    }

    @Then("ответ содержит сообщение об ошибке {string}")
    public void verifyErrorMessage(String expectedMessage) {
        assertNotNull(response.getBody(), "Response body должен быть не null");

        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("error"), "Ответ должен содержать поле 'error'");

        String actualMessage = body.get("error").toString();
        assertTrue(actualMessage.contains(expectedMessage) ||
                        actualMessage.equalsIgnoreCase(expectedMessage),
                "Сообщение об ошибке должно содержать: '" + expectedMessage +
                        "', но было: '" + actualMessage + "'");
    }

    @Then("ответ содержит информацию о карте")
    public void verifyCardInfo() {
        assertNotNull(response.getBody(), "Response body должен быть не null");

        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("status") && body.containsKey("balance"),
                "Ответ должен содержать статус и баланс карты");
    }

    @Then("баланс карты соответствует ожидаемому {double}")
    public void verifyBalance(double expectedBalance) {
        assertNotNull(response.getBody(), "Response body должен быть не null");

        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("balance"), "Ответ должен содержать поле 'balance'");

        Double actualBalance = Double.valueOf(body.get("balance").toString());
        assertEquals(expectedBalance, actualBalance, 0.01,
                "Баланс должен соответствовать ожидаемому");
    }

    @Given("я проверяю статус карты {string}")
    public void checkCardStatus(String cardId) {
        String url = baseUrl() + "/cards/" + cardId;
        response = restTemplate.getForEntity(url, Map.class);
    }
}