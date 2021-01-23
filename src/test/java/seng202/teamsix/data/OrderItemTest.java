package seng202.teamsix.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrderItemTest {

    @Test
    public void setItemTest() {
        OrderItem order = new OrderItem();
        Item_Ref random_item = new Item_Ref();
        order.setItem(random_item);
        assertTrue(random_item.equals(order.getItem()));
    }

    @Test
    public void addDependantTest() {
        OrderItem order = new OrderItem();
        OrderItem test = new OrderItem();
        assertEquals(0, order.getDependants().size());
        order.addDependant(test);
        assertEquals(1, order.getDependants().size());
    }

    @Test
    public void setQuantityTest() {
        OrderItem order = new OrderItem();

        order.setQuantity(50);
        assertEquals(50, order.getQuantity());

        order.setQuantity(0);
        assertEquals(0, order.getQuantity());

        order.setQuantity(-100);
        assertEquals(0, order.getQuantity());
    }

    /**
     * This tests the addToOrder method. This method is necessary in order to make use of the hierarchical structure where
     * the top of the order is an OrderItem object without an associated item. This OrderItem contains all the OrderItems
     * part of the order in the list of dependencies. This test adds items to the list of dependencies, both items already
     * part of the order and not already in the order.
     */
    @Test
    public void addToOrderTest() {
        //Item_Ref chips_ref = initialiseItem1();
        //Item chips = (Item) StorageAccess.instance().getItem(chips_ref);

        Item_Ref combo_ref = initialiseItem2();
        CompositeItem combo_item = (CompositeItem) StorageAccess.instance().getItem(combo_ref);

        OrderItem bag = new OrderItem();
        //bag.addToOrder(chips, 5, null);
        //bag.addToOrder(chips, 3, null);
        bag.addToOrder(combo_item, 2, null, 0);
        assertEquals(2, bag.getDependants().get(0).getQuantity());
        //assertEquals(2, bag.getDependants().get(1).getQuantity());
    }

    @Test
    public void addToOrderRealExample() {
        Item_Ref combo_ref = initialiseItem2();
        CompositeItem combo_item = (CompositeItem) StorageAccess.instance().getItem(combo_ref);

        OrderItem order = new OrderItem();
        order.addToOrder(combo_item, 1, null, 0);

        String expected =   "+ (Empty)\n" +
                            "|--+ Cheese Burger Combo\n" +
                            "|--|--+ Cheese Burger\n" +
                            "|--|--|--+ Buns\n" +
                            "|--|--|--|--+ Gluten Free Bun\n" +
                            "|--|--|\n" +
                            "|--|--|--+ Patty\n" +
                            "|--|--|--|--+ Meat Patty\n" +
                            "|--|--|\n" +
                            "|--|--|--+ Cheese\n" +
                            "|--|\n" +
                            "|--|--+ Drink\n" +
                            "|--|--+ Chips\n" +
                            "|\n";
        assertEquals(order.getOrderTreeRepr(0), expected);
    }

    /**
     * Tests the removeFromOrder method. This method is necessary as it is used in the OrderManager to remove items from
     * the cart.
     */
    @Test
    public void removeFromOrderTest() {
        StorageAccess.initTestMode("ItemTest");
        Item_Ref item2 = initialiseItem2();
        OrderItem bag = new OrderItem();
        bag.addToOrder(item2, 5, null, 0);
        assertTrue(bag.removeFromOrder(item2, 4, null, true));
        assertEquals(1, bag.getDependants().get(0).getQuantity());
        assertFalse(bag.removeFromOrder(item2, 4, null, true));
    }

    /**
     * This returns an Item object that we can use for testing. Specifically, the Chips.
     * The test data has also been imported.
     * @return A reference to the Item object used for testing.
     */
    public Item_Ref initialiseItem1() {
        StorageAccess.initTestMode("ItemTest");
        Item_Ref chips_ref = new Item_Ref();
        chips_ref.setUUID("b73f2268-e758-440b-9237-daf2b00193d4");
        Item chips = StorageAccess.instance().getItem(chips_ref);
        System.out.println(chips.getName());
        return chips_ref;
    }

    /**
     * This returns an Item object that we can use for testing. Specifically, the Cheese Burger Combo.
     * The test data has also been imported.
     * @return A reference to the Item object used for testing.
     */
    public Item_Ref initialiseItem2() {
        StorageAccess.initTestMode("ItemTest");
        Item_Ref combo_ref = new Item_Ref();
        combo_ref.setUUID("79e1c5bf-ecca-4d8b-a3a5-1c0166c9f994");
        return combo_ref;
    }
}