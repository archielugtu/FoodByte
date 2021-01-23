Feature: Menu operations

  Scenario: Change the price of menu item
    Given The current price of a burger is $10
    When The user selects change price and enters $12
    Then The current price of a burger is now the new price $12

  Scenario: Checking the recipe for a given menu item
    Given Menu is open and the user needs to check the recipe for a burger
    When A burger is selected and view recipe is selected
    Then The recipe for a burger is displayed

  Scenario: Add item to existing menu
    Given A new burger is created
    When New burger is added to the menu
    Then New burger now in menu

  Scenario: Remove item from existing menu
    Given A burger is no longer being sold
    When Delete burger is selected
    Then Burger no longer in menu

  Scenario: Add recipe to accompany a existing menu item
    Given A new recipe for an item is created
    When Add recipe is selected
    Then Recipe is now in the menu

