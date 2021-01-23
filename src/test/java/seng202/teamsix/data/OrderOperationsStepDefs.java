package seng202.teamsix.data;

import com.sun.xml.bind.v2.TODO;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.junit.Assert;
import org.junit.jupiter.api.extension.ExtensionContext;
import seng202.teamsix.managers.OrderManager;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Date;

public class OrderOperationsStepDefs {
    MenuItem menuBurger = new MenuItem();
    Item chips = new Item();
    OrderManager cart = new OrderManager();
    MenuItem menuChip = new MenuItem();
    Item burger = new Item();
    Item_Ref chip_ref = new Item_Ref();
    Item_Ref burger_ref = new Item_Ref();
    int totalPrice = 0;
    CashRegister register = new CashRegister();

    @Given("Current order contains only one burger")
    public void currentOrderContainsOnlyOneBurger() {
        cart.getCart().setUUID("3aadca48-545b-40fc-90ce-f908284b93e8");
        burger_ref.setUUID("79e1c5bf-ecca-4d8b-a3a5-1c0166c9f994");
        burger = StorageAccess.instance().getItem(burger_ref);

        menuBurger.setItem(burger_ref);
        cart.addToCart(menuBurger, false);
    }

    @When("Chips are added to the current order")
    public void chipsAreAddedToTheCurrentOrder() {
        chip_ref.setUUID("8921e663-6b3a-4321-ad6f-c5e6e22a33c5");
        chips = StorageAccess.instance().getItem(chip_ref);

        menuChip.setItem(chip_ref);
        cart.addToCart(menuChip, false);
    }

    @Then("The current order consists of one burger and one chips")
    public void theCurrentOrderConsistsOfOneBurgerAndOneChips() {
        
        assertEquals("Cheese Burger Combo", StorageAccess.instance().getItem(cart.getCart().getOrderTree().getDependants().get(0).getItem()).getName());
        assertEquals("Chips", StorageAccess.instance().getItem(cart.getCart().getOrderTree().getDependants().get(1).getItem()).getName());
        assertEquals(2, cart.getCart().getOrderTree().getDependants().size());
    }

    @Given("Current order contains one burger and one chips")
    public void currentOrderContainsOneBurgerAndOneChips() {
        cart.getCart().setUUID("3aadca48-545b-40fc-90ce-f908284b93e8");
        Item_Ref burger_ref = new Item_Ref();
        burger_ref.setUUID("79e1c5bf-ecca-4d8b-a3a5-1c0166c9f994");
        burger = StorageAccess.instance().getItem(burger_ref);

        Item_Ref chip_ref = new Item_Ref();
        chip_ref.setUUID("8921e663-6b3a-4321-ad6f-c5e6e22a33c5");
        chips = StorageAccess.instance().getItem(chip_ref);

        menuChip.setItem(chip_ref);
        cart.addToCart(menuChip,false);

        menuBurger.setItem(burger_ref);
        cart.addToCart(menuBurger, false);
    }

    @When("Chips are removed from the current order")
    public void chipsAreRemovedFromTheCurrentOrder() {
        cart.removeFromCart(menuChip, true);
    }

    @Then("The current order consists of one burger")
    public void theCurrentOrderConsistsOfOneBurger() {
        assertEquals("Cheese Burger Combo", StorageAccess.instance().getItem(cart.getCart().getOrderTree().getDependants().get(0).getItem()).getName());
        assertEquals(1, cart.getCart().getOrderTree().getDependants().size());
    }

    @Given("The current order contains a burger and chips")
    public void theCurrentOrderContainsABurgerAndChips() {
        cart.getCart().setUUID("3aadca48-545b-40fc-90ce-f908284b93e8");
        Item_Ref burger_ref = new Item_Ref();
        burger_ref.setUUID("79e1c5bf-ecca-4d8b-a3a5-1c0166c9f994");
        burger = StorageAccess.instance().getItem(burger_ref);

        Item_Ref chip_ref = new Item_Ref();
        chip_ref.setUUID("8921e663-6b3a-4321-ad6f-c5e6e22a33c5");
        chips = StorageAccess.instance().getItem(chip_ref);

        menuChip.setItem(chip_ref);
        cart.addToCart(menuChip, false);

        menuBurger.setItem(burger_ref);
        cart.addToCart(menuBurger, false);
    }

    @When("The customer asks to cancel the order")
    public void theCustomerAsksToCancelTheOrder() {
        cart.resetCart();
    }

    @Then("Current order is terminated and new order is started \\(empty)")
    public void currentOrderIsTerminatedAndNewOrderIsStartedEmpty() {
        assertEquals(0, cart.getCart().getOrderTree().getDependants().size());
        assertEquals(true, cart.getCart().isEmpty());
    }


    @Given("The customer has ordered all that they desire")
    public void theCustomerHasOrderedAllThatTheyDesire() {
        cart.getCart().setUUID("3aadca48-545b-40fc-90ce-f908284b93e8");
        Item_Ref burger_ref = new Item_Ref();
        burger_ref.setUUID("79e1c5bf-ecca-4d8b-a3a5-1c0166c9f994");
        burger = StorageAccess.instance().getItem(burger_ref);

        Item_Ref chip_ref = new Item_Ref();
        chip_ref.setUUID("8921e663-6b3a-4321-ad6f-c5e6e22a33c5");
        chips = StorageAccess.instance().getItem(chip_ref);

        menuChip.setItem(chip_ref);
        cart.addToCart(menuChip,false);

        menuBurger.setItem(burger_ref);
        cart.addToCart(menuBurger,false);
    }

    @When("The order is confirmed by the user")
    public void theOrderIsConfirmedByTheUser() {
        cart.finaliseOrder(true);
    }

    @Then("The customer is asked to pay the full price which is then added to the cash register")
    public void theCustomerIsAskedToPayTheFullPriceWhichIsThenAddedToTheCashRegister() {
        assertEquals(0, cart.getCart().getOrderTree().getDependants().size());
        assertEquals(true, cart.getCart().isEmpty());
        // TODO(Anzac): Check that the chefs order and receipts are correct
    }

    @Given("A customer returns items bought from the food truck")
    public void aCustomerReturnsItemsBoughtFromTheFoodTruck() {
        Currency chipPrice = new Currency(5);
        Currency burgerPrice = new Currency(10);
        register.setRegisterAmount(5000);

        cart.getCart().setUUID("3aadca48-545b-40fc-90ce-f908284b93e8");
        Item_Ref burger_ref = new Item_Ref();
        burger_ref.setUUID("79e1c5bf-ecca-4d8b-a3a5-1c0166c9f994");
        burger = StorageAccess.instance().getItem(burger_ref);

        Item_Ref chip_ref = new Item_Ref();
        chip_ref.setUUID("8921e663-6b3a-4321-ad6f-c5e6e22a33c5");
        chips = StorageAccess.instance().getItem(chip_ref);

        cart.resetCart();

        menuChip.setItem(chip_ref);
        menuChip.setPrice(chipPrice);
        cart.addToCart(menuChip,false);

        menuBurger.setItem(burger_ref);
        menuBurger.setPrice(burgerPrice);
        cart.addToCart(menuBurger,false);
    }

    @When("Total price of order is found and cash returned")
    public void totalPriceOfOrderIsFoundAndCashReturned() {
        totalPrice = cart.getCart().getTotalCost().getDollars();
        Currency refundAmount = new Currency(totalPrice);
        register.subRegisterAmount(refundAmount);
    }

    @Then("New cash value in the register")
    public void newCashValueInTheRegister() {
        assertEquals(35, register.getRegisterAmount());
    }
}
