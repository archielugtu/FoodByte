package seng202.teamsix.data;


import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StockOperationsStepDefs {
    StockInstance stock_item;
    double current_stock = 0;

    @Given("New stock items arrive")
    public void newStockItemsArrive() {
        stock_item = new StockInstance();
        stock_item.setUUID("41b0ce5b-8ff1-4d63-8b73-311d79069ffc");
    }

    @When("Add stock is selected")
    public void addStockIsSelected() {
        stock_item.setQuantityRemaining(5);

    }

    @Then("Stock is added to the inventory")
    public void stockIsAddedToTheInventory() {
        assertEquals(5, stock_item.getQuantityRemaining());
    }

    @Given("Stock items expire are used etc")
    public void stockItemsExpireAreUsedEtc() {
        stock_item = new StockInstance();
        stock_item.setUUID("41b0ce5b-8ff1-4d63-8b73-311d79069ffc");
        stock_item.setQuantityRemaining(10);
}

    @When("Remove stock is selected")
    public void removeStockIsSelected() {
        stock_item.subQuantity(5);
    }

    @Then("Stock is removed to the inventory")
    public void stockIsRemovedToTheInventory() {
        assertEquals(5, stock_item.getQuantityRemaining());
    }

    @Given("A user needs to view stock levels")
    public void aUserNeedsToViewStockLevels() {
        stock_item = new StockInstance();
        stock_item.setUUID("41b0ce5b-8ff1-4d63-8b73-311d79069ffc");
        stock_item.setQuantityRemaining(19);
    }

    @When("View inventory is selected")
    public void viewInventoryIsSelected() {
        current_stock = stock_item.getQuantityRemaining();
    }

    @Then("List of all inventory items is displayed")
    public void listOfAllInventoryItemsIsDisplayed() {
        assertEquals(19, current_stock);
    }
}
