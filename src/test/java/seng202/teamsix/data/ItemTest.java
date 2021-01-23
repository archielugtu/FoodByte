package seng202.teamsix.data;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ItemTest {
    @Test
    public void testGetMarkupPercentage() {
        ArrayList<ItemTag_Ref> tagList = new ArrayList<ItemTag_Ref>();
        Recipe recipe = new Recipe("Cut Potatoes, cover in batter, deep-try for 5 minutes.");

        Currency base_price = new Currency();
        base_price.setTotalCash(7.50);

        Currency markup_price = new Currency();
        markup_price.setTotalCash(10.00);

        Item item = new Item("Large Fries", "Deep-fried pieces of potato. ", base_price, markup_price, recipe, tagList, UnitType.G);
        assertEquals((1.0/3.0)*100, item.getMarkupPercentage(), 0.0);
    }

    @Test
    /**
     * GIVEN Large fries with a base price of $7.50 and selling price of $10.00.
     * WHEN getProfit() called on Item large fries.
     * THEN the profit amount is computed, which is just $10.00 - $7.50 = $2.50.
     */
    public void testGetProfit() {
        ArrayList<ItemTag_Ref> tagList = new ArrayList<ItemTag_Ref>();
        Recipe recipe = new Recipe("Cut Potatoes, cover in batter, deep-try for 5 minutes.");

        Currency base_price = new Currency();
        base_price.setTotalCash(7.50);

        Currency markup_price = new Currency();
        markup_price.setTotalCash(10.00);

        Item item = new Item("Large Fries", "Deep-fried pieces of potato. ", base_price, markup_price, recipe, tagList, UnitType.G);
        assertEquals(new Currency(2, 50), item.getProfit());
    }

    @Test
    public void testGetAllTags() {
        ArrayList<ItemTag_Ref> tagList = new ArrayList<ItemTag_Ref>();
        Recipe recipe = new Recipe("Cut Potatoes, cover in batter, deep-try for 5 minutes.");

        Currency base_price = new Currency();
        base_price.setTotalCash(7.50);

        Currency markup_price = new Currency();
        markup_price.setTotalCash(10.00);

        Item item = new Item("Large Fries", "Deep-fried pieces of potato. ", base_price, markup_price, recipe, tagList, UnitType.G);
        assertEquals(tagList, item.getTags());
    }
}
