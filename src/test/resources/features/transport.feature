Feature: Расчет стоимости проезда по транспортной карте

  Background:
    Given сервис доступен

  @success
  Scenario Outline: Успешный расчет стоимости поездки
    Given карта "<cardId>" имеет статус "ACTIVE" и баланс <balance>
    When я отправляю POST запрос на "/transport/ride" с телом:
      """
      {
        "zones": <zones>,
        "ticketType": "<ticketType>",
        "cardId": "<cardId>"
      }
      """
    Then API возвращает статус-код 200
    And ответ содержит цену <expectedPrice>
    And ответ содержит успешный статус
    And цена положительная

    Examples:
      | cardId | zones | ticketType | balance | expectedPrice |
      | CARD1  | 1     | adult      | 1000.0  | 100.0         |
      | CARD1  | 2     | adult      | 1000.0  | 150.0         |
      | CARD1  | 3     | adult      | 1000.0  | 200.0         |
      | CARD2  | 1     | child      | 50.0    | 50.0          |

  @error
  Scenario Outline: Негативные сценарии - ошибки валидации
    Given карта "<cardId>" имеет статус "<status>" и баланс <balance>
    When я отправляю POST запрос на "/transport/ride" с телом:
      """
      {
        "zones": <zones>,
        "ticketType": "<ticketType>",
        "cardId": "<cardId>"
      }
      """
    Then API возвращает статус-код <expectedStatus>
    And ответ содержит сообщение об ошибке "<errorMessage>"

    Examples:
      | cardId   | status  | balance | zones | ticketType | expectedStatus | errorMessage                        |
      | BLOCKED  | BLOCKED | 500.0   | 1     | adult      | 403            | Карта заблокирована                  |
      | CARD1    | ACTIVE  | 1000.0  | 0     | adult      | 400            | Количество зон должно быть            |
      | CARD1    | ACTIVE  | 1000.0  | 4     | adult      | 400            | Количество зон должно быть            |
      | CARD1    | ACTIVE  | 1000.0  | 1     | student    | 400            | Тип билета должен быть                |
      | CARD2    | ACTIVE  | 50.0    | 2     | child      | 400            | Баланс карты меньше стоимости         |
      | CARD2    | ACTIVE  | 50.0    | 3     | child      | 400            | Баланс карты меньше стоимости         |

  @card-info
  Scenario Outline: Проверка информации о карте
    Given сервис доступен
    Given я проверяю статус карты "<cardId>"
    Then API возвращает статус-код 200
    And ответ содержит информацию о карте
    And баланс карты соответствует ожидаемому <expectedBalance>
    And статус карты соответствует ожидаемому "<expectedStatus>"

    Examples:
      | cardId   | expectedBalance | expectedStatus |
      | CARD1    | 1000.0          | ACTIVE         |
      | CARD2    | 50.0            | ACTIVE         |
      | BLOCKED  | 500.0           | BLOCKED        |