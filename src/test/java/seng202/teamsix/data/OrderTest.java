package seng202.teamsix.data;

import org.junit.Test;
import seng202.teamsix.managers.OrderManager;

import static org.junit.Assert.assertEquals;

public class OrderTest {

    @Test
    public void getTotalCostTest() {
        Order order = new Order();
        Item_Ref burger_ref = initialiseItem1(); // returns a reference to a burger item already one of the xml files.
        OrderItem root = order.getOrderTree();
        root.addToOrder(burger_ref, 25, null, 0); // later changed so that this does not add to the price, instead check to see if not null then add price
        root.addToPrice(burger_ref, 25, null);
        // Each burger has a selling price of $19.99. By multiplying this by 25 we get $499.75.
        assertEquals(499.75, order.getTotalCost().getTotalCash(), 0.0);
    }

    //@Ignore
    @Test
    public void testPrintChefsOrderAndReceipt() {
        StorageAccess.initTestMode("ItemTest");

        Item_Ref combo_ref = new Item_Ref();
        combo_ref.setUUID(8782518176451284363l, -6654882082024982124l);
        CompositeItem combo_item = (CompositeItem) StorageAccess.instance().getItem(combo_ref);
        MenuItem menu_combo = new MenuItem();
        menu_combo.setItem(combo_ref);
        Currency menu_combo_price = new Currency(19.99);
        menu_combo.setPrice(menu_combo_price);
        OrderManager orderManager = new OrderManager();
        orderManager.addToCart(menu_combo, false);
        orderManager.addToCart(menu_combo, false);
        orderManager.addToCart(menu_combo, false);
        orderManager.addToCart(menu_combo, false);
        String expectedChefsOrder =
                "/**********  Chef's Order  **********/\n" +
                "Order Number: 1\n" +
                "Contents:\n" +
                "Cheese Burger Combo\n" +
                "    - Cheese Burger\n" +
                "      - Buns\n" +
                "        - Gluten Free Bun\n" +
                "      - Patty\n" +
                "        - Meat Patty\n" +
                "      - Cheese\n" +
                "    - Drink\n" +
                "    - Chips\n" +
                "Cheese Burger Combo\n" +
                "    - Cheese Burger\n" +
                "      - Buns\n" +
                "        - Gluten Free Bun\n" +
                "      - Patty\n" +
                "        - Meat Patty\n" +
                "      - Cheese\n" +
                "    - Drink\n" +
                "    - Chips\n" +
                "Cheese Burger Combo\n" +
                "    - Cheese Burger\n" +
                "      - Buns\n" +
                "        - Gluten Free Bun\n" +
                "      - Patty\n" +
                "        - Meat Patty\n" +
                "      - Cheese\n" +
                "    - Drink\n" +
                "    - Chips\n" +
                "Cheese Burger Combo\n" +
                "    - Cheese Burger\n" +
                "      - Buns\n" +
                "        - Gluten Free Bun\n" +
                "      - Patty\n" +
                "        - Meat Patty\n" +
                "      - Cheese\n" +
                "    - Drink\n" +
                "    - Chips\n" +
                "\n" +
                "/************************************/";
        assertEquals(expectedChefsOrder, orderManager.getCart().getChefOrder());

        String expectedReceipt =
                "/************  Receipt  *************/\n" +
                "FoodByte\n" +
                "null\n" +
                "Payment Method: Cash\n" +
                "Receipt (Order Number: 1)\n" +
                "Contents:\n" +
                "Cheese Burger Combo @ $19.99 each\n" +
                "    - Cheese Burger\n" +
                "      - Buns\n" +
                "        - Gluten Free Bun\n" +
                "      - Patty\n" +
                "        - Meat Patty\n" +
                "      - Cheese\n" +
                "    - Drink\n" +
                "    - Chips\n" +
                "Cheese Burger Combo @ $19.99 each\n" +
                "    - Cheese Burger\n" +
                "      - Buns\n" +
                "        - Gluten Free Bun\n" +
                "      - Patty\n" +
                "        - Meat Patty\n" +
                "      - Cheese\n" +
                "    - Drink\n" +
                "    - Chips\n" +
                "Cheese Burger Combo @ $19.99 each\n" +
                "    - Cheese Burger\n" +
                "      - Buns\n" +
                "        - Gluten Free Bun\n" +
                "      - Patty\n" +
                "        - Meat Patty\n" +
                "      - Cheese\n" +
                "    - Drink\n" +
                "    - Chips\n" +
                "Cheese Burger Combo @ $19.99 each\n" +
                "    - Cheese Burger\n" +
                "      - Buns\n" +
                "        - Gluten Free Bun\n" +
                "      - Patty\n" +
                "        - Meat Patty\n" +
                "      - Cheese\n" +
                "    - Drink\n" +
                "    - Chips\n" +
                "\n" +
                "Total Amount Paid: $79.96\n" +
                "/************************************/";
        assertEquals(expectedReceipt, orderManager.getCart().getReceipt());
    }

    /**
     * This helper method imports the xml items and retrieves a reference to a burger item already created in the xml.
     * @return reference to a burger.
     */
    Item_Ref initialiseItem1() {
        StorageAccess.initTestMode("ItemTest");
        Item_Ref combo_ref = new Item_Ref();
        combo_ref.setUUID(8782518176451284363l, -6654882082024982124l);
        return combo_ref;
    }
}