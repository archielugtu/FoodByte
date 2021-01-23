package seng202.teamsix.data;

import java.util.Calendar;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

public class StockInstanceTest {

    @Test
    public void testHasExpired() {
        Date date_added = new Date();
        Date date_expired = new Date();
        StockInstance stock_item = new StockInstance(date_added, true, date_expired, 100, new Item_Ref());
        assertEquals(true, stock_item.hasExpired());

        Date date_added1 = new Date();
        Date date_expired1 = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date_expired1);
        c.add(Calendar.DATE, 2);
        date_expired1 = c.getTime();
        StockInstance stock_item1 = new StockInstance(date_added1, true, date_expired1, 100, new Item_Ref());
        assertEquals(false, stock_item1.hasExpired());

    }


    @Test
    public void testTimeRemaining() {
        Date date_added = new Date();
        Date date_expired = new Date();
        StockInstance stock_item = new StockInstance(date_added, true, date_expired, 100, new Item_Ref());
        assertEquals(0, stock_item.timeRemaining());

        Date date_added2 = new Date();
        StockInstance stock_item2 = new StockInstance(date_added2, false, null, 100, new Item_Ref());
        assertEquals(1000, stock_item2.timeRemaining());
    }

    @Test
    public void testSetQuantityRemaining() {
        Date date_added = new Date();
        Date date_expired = new Date();
        StockInstance stock_item = new StockInstance(date_added, true, date_expired, 100, new Item_Ref());

        assertEquals(100, stock_item.getQuantityRemaining(), 0.0001);

        //Test adding quantity
        stock_item.addQuantity(10);
        assertEquals(110, stock_item.getQuantityRemaining(), 0.0001);

        //Test subtracting quantity
        stock_item.subQuantity(20);
        assertEquals(90, stock_item.getQuantityRemaining(), 0.0001);

        //Test subtracting more than what is remaining
        stock_item.subQuantity(100);
        assertEquals(0, stock_item.getQuantityRemaining(), 0.0001);
    }
}