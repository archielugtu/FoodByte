Feature: Cash register operations

  Scenario: Add cash to get total
    Given $100 current total cash
    When $50 is add to the current total cash
    Then The current total cash is now $150

  Scenario: Remove cash to get total
    Given $200 dollars in cash (total)
    When $50 is removed from the total cash
    Then Now $150 in cash (total)

  Scenario: Removing more cash then is available
    Given $40 dollars in total cash (total)
    When $60 is removed from the cash register
    Then An error is thrown, money not removed