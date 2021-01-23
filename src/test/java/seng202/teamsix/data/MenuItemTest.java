package seng202.teamsix.data;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MenuItemTest {

    @Test
    public void testSetAndGetPrice() {
        MenuItem menuItem = new MenuItem();
        menuItem.setPrice(new Currency(123, 45));
        assertTrue("100% passed", menuItem.getPrice().equals(new Currency(123,45)));
        System.out.println("100% passed");
    }

    @Test
    public void testSetAndGetName() {
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Cheeseburger");
        assertTrue("100% passed", menuItem.getName() == "Cheeseburger");
        System.out.println("100% passed");
    }

    @Test
    public void testSetAndGetDescription() {
        MenuItem menuItem = new MenuItem();
        menuItem.setDescription("Yummy");
        assertTrue("100% passed", menuItem.getDescription() == "Yummy");
        System.out.println("100% passed");
    }

    @Test
    public void testSetAndGetItemRefWithoutItem() {
        Item_Ref itemReference = new Item_Ref();
        MenuItem menuItem = new MenuItem();
        menuItem.setItem(itemReference);
        assertTrue("Passed", menuItem.getItem().equals(itemReference));
        System.out.println("100% passed");
    }

    // Broken test this should be using StorageAccess not a local item
    @Ignore
    @Test
    public void testSetAndGetItemRefWithValidItem() {
        ArrayList<ItemTag_Ref> tagList = new ArrayList<ItemTag_Ref>();
        Recipe recipe = new Recipe("Cut Potatoes, cover in batter, deep-try for 5 minutes.");

        Currency base_price = new Currency();
        base_price.setTotalCash(7.50);

        Currency markup_price = new Currency();
        markup_price.setTotalCash(10.00);

        Item item = new Item("Large Fries", "Deep-fried pieces of potato. ", base_price, markup_price, recipe, tagList, UnitType.G);
        MenuItem menuItem = new MenuItem();
        menuItem.setItem(item);
        assertTrue("Passed", menuItem.getItem() == item);
        System.out.println("100% passed");
    }
}