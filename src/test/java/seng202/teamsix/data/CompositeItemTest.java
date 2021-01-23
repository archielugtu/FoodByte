/**
 * Name: CompositeItemTest.java
 * Authors: George Stephenson
 * Date: 22/08/2019
 */

package seng202.teamsix.data;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


public class CompositeItemTest {
    @Test
    public void testSetGetItems() {
        CompositeItem item = new CompositeItem();
        ArrayList<Item_Ref> subItems = new ArrayList<>();
        subItems.add(new Item_Ref());
        item.setComponents(subItems);
        assertEquals(item.getItems().size(), 1);
    }


    @Test
    public void testItemRepresentation() {
        StorageAccess.initTestMode("ItemTest");


        Item_Ref combo_ref = new Item_Ref();
        combo_ref.setUUID(8782518176451284363l, -6654882082024982124l);
        CompositeItem combo_item = (CompositeItem) StorageAccess.instance().getItem(combo_ref);

        String expected =   "+ Cheese Burger Combo\n" +
                            "|--+ Cheese Burger\n" +
                            "|--|--+ Buns *\n" +
                            "|--|--|--+ Gluten Free Bun\n" +
                            "|--|--|--+ Regular Bread Bun\n" +
                            "|--|\n" +
                            "|--|--+ Patty *\n" +
                            "|--|--|--+ Meat Patty\n" +
                            "|--|--|--+ Meat Patty\n" +
                            "|--|\n" +
                            "|--|--+ Cheese\n" +
                            "|\n" +
                            "|--+ Drink\n" +
                            "|--+ Chips\n";

        assertEquals(combo_item.getItemTreeRepr(0, 0), expected);
    }
}