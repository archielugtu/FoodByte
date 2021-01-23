package seng202.teamsix.data;

import org.junit.Test;
import static org.junit.Assert.*;

public class CurrencyTest {
    @Test
    public void testSetAndGetDollars() {
        Currency cash = new Currency();
        cash.setCents(500);
        assertEquals(5, cash.getDollars());
    }
    @Test
    public void testSetAndGetCents() {
        Currency cash = new Currency();
        cash.setCents(50);
        assertEquals(50, cash.getCents());
    }
    @Test
    public void testSetAndGetTotalCash() {
        Currency cash = new Currency();
        cash.setTotalCash(100);
        assertEquals(100, cash.getDollars());
    }
    @Test
    public void testAddCash() {
        Currency cash = new Currency();

        cash.setTotalCash(100);
        Currency fiveDollars = new Currency(5);
        cash.setTotalCash(100);
        cash.addCash(fiveDollars);
        assertEquals(105, cash.getDollars());

        cash.setTotalCash(100);
        Currency fiveTen = new Currency(5, 10);
        cash.addCash(fiveTen);
        assertEquals(105, cash.getDollars());
        assertEquals(10, cash.getCents());

        // 10.40 + 6.70 = 17.10
        cash.setValue(10, 40);
        Currency fiveOneSeventy = new Currency(5, 170);
        cash.addCash(fiveOneSeventy);
        assertEquals(17, cash.getDollars());
        assertEquals(10, cash.getCents());
    }
    @Test
    public void testSubCash() {
        Currency cash = new Currency();

        cash.setTotalCash(100);
        Currency fiveDollars = new Currency(5);
        cash.subCash(fiveDollars);
        assertEquals(95, cash.getDollars());

        cash.setTotalCash(100);
        Currency fiveTen = new Currency(5, 10);
        cash.subCash(fiveTen);
        assertEquals(94, cash.getDollars());
        assertEquals(90, cash.getCents());

        // 10.40 - 6.70 = 3.70
        cash.setValue(10, 40);
        Currency fiveOneSeventy = new Currency(5, 170);
        cash.subCash(fiveOneSeventy);
        assertEquals(3, cash.getDollars());
        assertEquals(70, cash.getCents());
    }

    @Test
    public void testCompareTo() {
        Currency cash = new Currency(10, 50);
        Currency less = new Currency(8,60);
        Currency more = new Currency(10, 70);

        assertTrue(cash.compareTo(less) < 0);
        assertTrue(cash.compareTo(more) > 0);
        assertTrue(cash.compareTo(cash) == 0);
    }
}
