package seng202.teamsix.data;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MenuOperationsStepDefs {
    Currency price = new Currency();
    MenuItem menu_burger = new MenuItem();
    Menu_Ref menu_ref;
    Recipe testRecipe;
    Item burger = new Item();
    Item burger2 = new Item();
    int currentLen = 0;

    @Given("The current price of a burger is ${int}")
    public void theCurrentPriceOfABurgerIs$(int arg0) {
        price.setValue(10,0);

        Item_Ref burger_ref = new Item_Ref();
        burger_ref.setUUID(8782518176451284363l, -6654882082024982124l);
        burger = StorageAccess.instance().getItem(burger_ref);

        menu_burger.setItem(burger_ref);

        menu_burger.setPrice(price);
    }


    @When("The user selects change price and enters ${int}")
    public void theUserSelectsChangePriceAndEnters$(int arg0) {
        price.setValue(12, 0);
        menu_burger.setPrice(price);
    }

    @Then("The current price of a burger is now the new price ${int}")
    public void theCurrentPriceOfABurgerIsNowTheNewPrice$(int arg0) {
        Currency actual = new Currency();
        actual.setValue(12,0);
        assertEquals("$12.00", menu_burger.getPrice().toString());
    }

    @Given("Menu is open and the user needs to check the recipe for a burger")
    public void menuIsOpenAndTheUserNeedsToCheckTheRecipeForABurger() {
        Item_Ref burger_ref = new Item_Ref();
        burger_ref.setUUID("79e1c5af-ecca-4d8b-a3a5-1c0166c9f994");
        burger2 = StorageAccess.instance().getItem(burger_ref);

        menu_burger.setItem(burger_ref);
    }

    @When("A burger is selected and view recipe is selected")
    public void aBurgerIsSelected() {
        burger = StorageAccess.instance().getItem(menu_burger.getItem());
    }

    @Then("The recipe for a burger is displayed")
    public void theRecipeForABurgerIsDisplayed() {
        assertEquals("Just put it together", burger.getRecipe().getMethod());
    }

    @Given("A new burger is created")
    public void aNewBurgerIsCreated() {
        Item_Ref burger_ref = new Item_Ref();
        burger_ref.setUUID("79e1c5af-ecca-4d8b-a3a5-1c0166c9f994");
        burger = StorageAccess.instance().getItem(burger_ref);

        menu_burger.setItem(burger_ref);
    }

    @When("New burger is added to the menu")
    public void editMenuIsSelected() {
        menu_ref = new Menu_Ref();
        menu_ref.setUUID("096abbbe-2134-4841-939a-e6922e202a97");
        StorageAccess.instance().getMenu(menu_ref).addToMenu(menu_burger);
    }


    @Then("New burger now in menu")
    public void newBurgerNowInMenu() {
        assertEquals("Cheese Burger",  StorageAccess.instance().getItem(StorageAccess.instance().getMenu(menu_ref).getMenuItems().get(1).getItem()).getName());
    }

    @Given("A new recipe for an item is created")
    public void aNewRecipeForAnItemIsCreated() {
        Item_Ref burger_ref = new Item_Ref();
        burger_ref.setUUID(8782518176451284363l, -6654882082024982124l);
        burger = StorageAccess.instance().getItem(burger_ref);

        menu_burger.setItem(burger_ref);
        testRecipe = new Recipe("Test that setting recipes works");
    }

    @When("Add recipe is selected")
    public void addRecipeIsSelected() {
        burger = StorageAccess.instance().getItem(menu_burger.getItem());
        burger.setRecipe(testRecipe);
    }

    @Then("Recipe is now in the menu")
    public void recipeIsNowInTheMenu() {
        assertEquals("Test that setting recipes works", burger.getRecipe().getMethod());
    }

    @Given("A burger is no longer being sold")
    public void aBurgerIsNoLongerBeingSold() {
        Item_Ref burger_ref = new Item_Ref();
        burger_ref.setUUID("79e1c5af-ecca-4d8b-a3a5-1c0166c9f994");
        burger = StorageAccess.instance().getItem(burger_ref);

        menu_burger.setItem(burger_ref);
    }

    @When("Delete burger is selected")
    public void editMenuIsClicked() {
        menu_ref = new Menu_Ref();
        menu_ref.setUUID("096abbbe-2134-4841-939a-e6922e202a97");
        StorageAccess.instance().getMenu(menu_ref).addToMenu(menu_burger);
        currentLen = StorageAccess.instance().getMenu(menu_ref).getMenuItems().size();
        StorageAccess.instance().getMenu(menu_ref).removeFromMenu(menu_burger);

    }

    @Then("Burger no longer in menu")
    public void burgerNoLongerInMenu() {
        assertEquals(currentLen - 1, StorageAccess.instance().getMenu(menu_ref).getMenuItems().size());
        assertEquals(StorageAccess.instance().getMenu(menu_ref).getMenuItems().contains(menu_burger), false);
    }
}
