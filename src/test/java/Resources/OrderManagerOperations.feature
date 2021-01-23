Feature: Order operations

  Scenario: Add chips to current order
    Given Current order contains only one burger
    When Chips are added to the current order
    Then The current order consists of one burger and one chips

  Scenario: Remove chips from current order
    Given Current order contains one burger and one chips
    When Chips are removed from the current order
    Then The current order consists of one burger

  Scenario: Cancelling an order
    Given The current order contains a burger and chips
    When The customer asks to cancel the order
    Then Current order is terminated and new order is started (empty)

  Scenario: Confirming an order
    Given The customer has ordered all that they desire
    When The order is confirmed by the user
    Then The customer is asked to pay the full price which is then added to the cash register

  Scenario: A customer returns an item for a refund
    Given A customer returns items bought from the food truck
    When Total price of order is found and cash returned
    Then New cash value in the register