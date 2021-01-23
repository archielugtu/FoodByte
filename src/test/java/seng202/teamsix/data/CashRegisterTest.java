package seng202.teamsix.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CashRegisterTest {

    @Test
    void getRegisterAmountTest() {
        CashRegister cashRegister = new CashRegister();
        cashRegister.setRegisterAmount(158);
        assertEquals(1.58, cashRegister.getRegisterAmount());
    }

    @Test
    void addRegisterAmountTest() {
        CashRegister cashRegister = new CashRegister();
        assertEquals(0.0, cashRegister.getRegisterAmount());
        Currency oneNinetyEight = new Currency(1, 98);
        cashRegister.addRegisterAmount(oneNinetyEight);
        assertEquals(1.98, cashRegister.getRegisterAmount());
    }
}