package seng202.teamsix.data;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CashRegisterOperationsStepDefs {
    CashRegister cashRegister;
    CashRegister cashRegister2;
    CashRegister cashRegister3;
    Currency subAmount = new Currency();

    @Given("${int} current total cash")
    public void $CurrentTotalCash(int arg0) {
        cashRegister = new CashRegister();
        cashRegister.setRegisterAmount(10000);
    }

    @When("${int} is add to the current total cash")
    public void $IsAddToTheCurrentTotalCash(int arg0) {
        cashRegister.setRegisterAmount(5000);
    }

    @Then("The current total cash is now ${int}")
    public void theCurrentTotalCashIsNow$(int arg0) {
        assertEquals(150, cashRegister.getRegisterAmount());
    }


    @Given("${int} dollars in cash \\(total)")
    public void $DollarsInCashTotal(int arg0) {
        cashRegister2 = new CashRegister();
        cashRegister2.setRegisterAmount(20000);
    }

    @When("${int} is removed from the total cash")
    public void $IsRemovedFromTheTotalCash(int arg0) {
        cashRegister2.setRegisterAmount(-5000);
    }

    @Then("Now ${int} in cash \\(total)")
    public void now$InCashTotal(int arg0) {
        assertEquals(150, cashRegister2.getRegisterAmount());
    }

    @Given("${int} dollars in total cash \\(total)")
    public void $DollarsInTotalCashTotal(int arg0) {
        cashRegister3 = new CashRegister();
        cashRegister3.setRegisterAmount(4000);
    }

    @When("${int} is removed from the cash register")
    public void $IsRemovedFromTheCashRegister(int arg0) {
        subAmount.setValue(arg0,0);
    }

    @Then("An error is thrown, money not removed")
    public void anErrorIsThrown() {
        assertEquals(40, cashRegister3.getRegisterAmount());
    }
}
