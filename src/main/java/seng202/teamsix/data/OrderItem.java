/**
 * Name: OrderItem.java
 * Date: August - September, 2019
 *
 * Defines an ordered item for an order, an order item can contain many dependent order items that are required for
 * the item.
 *
 * Authors: Connor Macdonald, Hamesh Ravji
 */

package seng202.teamsix.data;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OrderItem {
    @XmlElement
    private Item_Ref item = null;
    @XmlElementWrapper(name = "dependants")
    @XmlElement(name = "order_item")
    private ArrayList<OrderItem> dependants = new ArrayList<OrderItem>();
    private OrderItem parent_order = null;
    @XmlElement
    private int quantity = 0;

    private Currency price = new Currency();

    /**
     * This returns the price of the OrderItem.
     * @return A Currency object containing the price of the item.
     */
    public Currency getPrice() {
        return price;
    }

    /**
     * Sets the price of the OrderItem. This would be used when adding items to orders where the price would be
     * from a menu item.
     * @param newPrice
     */
    public void setPrice(Currency newPrice) {
        price = newPrice;
    }

    /**
     * This retrieves the item reference from the attribute item.
     * @return The item reference corresponding to this OrderItem object.
     */
    public Item_Ref getItem() {
        return this.item;
    }

    /**
     * Sets the item attribute to reference the given item reference.
     * @param item An Item_Ref object which references an item.
     */
    public void setItem(Item_Ref item) {
        this.item = item;
    }

    /**
     * Returns a list of dependencies, where dependencies contains OrderItem objects.
     * @return
     */
    public ArrayList<OrderItem> getDependants() {
        return this.dependants;
    }

    /**
     * @return parent order for order item. Can be null if root order
     */
    public OrderItem getParent() {
        return this.parent_order;
    }

    /**
     * Adds an OrderItem object to the list of dependencies.
     * @param order_item The OrderItem object we want to add.
     */
    public void addDependant(OrderItem order_item) {
        this.dependants.add(order_item);

    }

    /**
     * A getter for the number of items currently assigned to this OrderItem object.
     * @return An integer quantity.
     */
    public int getQuantity() {
        return this.quantity;
    }

    /**
     * A setter for quantity.
     * @param quantity The number of items we want to assign to the quantity attribute of the OrderItem.
     */
    public void setQuantity(int quantity) {
        this.quantity = Math.max(quantity, 0);
    }

    /**
     * Returns an arraylist of stockinstance reference objects ordered by date where each stockinstance relates to the
     * given item reference.
     *
     * @param item_ref Item reference related to the stock instances we want to find.
     * @return Arraylist of stockinstance refs.
     */
    public ArrayList<StockInstance_Ref> getOrderedRelevantStockInstances(Item_Ref item_ref) {
        Item item = StorageAccess.instance().getItem(item_ref);
        ArrayList<StockInstance_Ref> orderedRelevantStockInstanceRefs = new ArrayList<StockInstance_Ref>();

        //Query 1
        DataQuery<StockInstance> stockDataQuery1 = new DataQuery<>(StockInstance.class);
        String regex = String.format("(?i).*(%s).*", item.getName());
        stockDataQuery1.addConstraintRegex("name", regex);
        stockDataQuery1.addConstraintEqual("notExpired", "true");
        stockDataQuery1.addConstraintEqual("hidden", "false");
        stockDataQuery1.sort_by("date_expires", true);
        List<UUID_Entity> orderedRelevantUUIDEntities1 = stockDataQuery1.runQuery();
        for (UUID_Entity uuid_entity: orderedRelevantUUIDEntities1) {
            orderedRelevantStockInstanceRefs.add((StockInstance_Ref) uuid_entity);
        }

        return orderedRelevantStockInstanceRefs;
    }

    /**
     * Used to update the stock when an order is completed, easier to remove stock once order is finalised as keeping track
     * of dates once an instance is removed is unnecessary and difficult under the time constraints.
     *
     * Whenever this function is used, user should test to see if the stock exists using stockExists with the same params.
     *
     * @param current_depth
     * @param remove If true, updates the stock accordingly. Else if false, does not update the stock.
     * @return true if there is enough stock, false otherwise.
     */
    public boolean checkOrRemoveStock(int current_depth, boolean remove) {
        boolean exists = true;

        // test to see if this is the root node or
        if (current_depth == 0) { // root layer
            for (OrderItem dependant : dependants) {
                exists &= dependant.checkOrRemoveStock(current_depth + 1, remove);
            }
            return exists;
        }

        // retrieve item from storage.
        Item temp_item = StorageAccess.instance().getItem(item);
        if (temp_item != null) { // anything below root layer
            ArrayList<StockInstance_Ref> orderedRelevantStockInstances = getOrderedRelevantStockInstances(item);
            int relevantInstanceIndex = 0;
            int item_quantity = getQuantity();
            while (relevantInstanceIndex < orderedRelevantStockInstances.size() && item_quantity > 0) {
                StockInstance relevantInstance = StorageAccess.instance().getStockInstance(orderedRelevantStockInstances.get(relevantInstanceIndex));
                if (item_quantity >= relevantInstance.getQuantityRemaining()) {
                    item_quantity -= relevantInstance.getQuantityRemaining();

                    if (remove) {
                        relevantInstance.setQuantityRemaining(0);
                        relevantInstance.setHidden("true");
                        StorageAccess.instance().updateStockInstance(relevantInstance);
                    }

                    if (item_quantity == 0) {
                        return true;
                    }

                } else {
                    if (remove) {
                        relevantInstance.subQuantity(item_quantity);
                        StorageAccess.instance().updateStockInstance(relevantInstance);
                    }
                    return true;
                }
                relevantInstanceIndex++;
            }

            // compositeitem not in stock, check if each of the items to make it are in stock.
            if (item_quantity > 0 && temp_item instanceof CompositeItem) {
                for (OrderItem dependant : dependants) {
                    exists &= dependant.checkOrRemoveStock(current_depth + 1, remove);
                }
                return exists;
            }
        }
        return false;
    }

    /**
     * Adds an Item to the Order, given an Item_Ref item_ref, int qty, and ItemTag_Ref default tag.
     * @param item_ref Refers to the Item of which we want to add to the order.
     * @param qty      The number of items we want to add too the order.
     * @return the added or existing OrderItem reference
     */
    public OrderItem addToOrder(Item_Ref item_ref, int qty, Currency new_item_price, int recurse_depth) {
        Item item = StorageAccess.instance().getItem(item_ref);

        OrderItem new_orderitem = new OrderItem();
        new_orderitem.setItem(item_ref);
        new_orderitem.setQuantity(qty);
        new_orderitem.parent_order = this;
        this.dependants.add(new_orderitem);

        if (new_item_price != null) {
            Currency temp_price = new_orderitem.getPrice();
            temp_price.addCash(new_item_price);
            new_orderitem.setPrice(temp_price);
        }
        if (item instanceof CompositeItem) {
            for (Item_Ref child_ref : ((CompositeItem) item).getItems()) {
                new_orderitem.addToOrder(child_ref, 1, new_item_price, recurse_depth + 1);
            }
        } else if (item instanceof VariantItem) {
            new_orderitem.addToOrder(((VariantItem) item).getVariants().get(0), 1, null,
                    recurse_depth + 1);
        }
        return new_orderitem;
    }

    /**
     * This method updates the price of the current OrderItem, likely to be used on the root orderitem of the order.
     * @param ref Used in case the OrderItem does not have an associated price to get the default markup price of the item
     *            to be added.
     * @param qty The number of items being added, such that we can add the price this many times.
     * @param new_item_price Pass through null of not used, otherwise will be used to add the price to the current
     *                       orderitem.
     */
    public void addToPrice(Item_Ref ref, int qty, Currency new_item_price) {
        Item item = StorageAccess.instance().getItem(ref);
        for (int i = 0; i < qty; i++) {
            if (new_item_price != null) {
                price.addCash(new_item_price);
            } else {
                price.addCash(item.getMarkupPrice());
            }
        }
    }

    public boolean removeFromOrder(OrderItem order_item) {
        return dependants.remove(order_item);
    }

    /**
     * Removes an Item from the Order, given an Item_Ref item_ref, and an int qty.
     * @param item_ref Refers to the Item which we want to remove from the Order.
     * @param qty The number of items we want to remove from the order.
     * @return True if items are removed, false if they were not in the order tree to begin with.
     */
    public boolean removeFromOrder(Item_Ref item_ref, int qty, Currency price_of_item_to_remove, boolean removeFromPrice) {
        boolean is_removed = false;
        // check the list of dependants to see if the item is in the list (at the top level)
        int index = 0;
        while (index < dependants.size() && !is_removed) {
            OrderItem order_item = dependants.get(index);
            if (order_item.getItem().equals(item_ref)) {
                // match found, check if the quantity in the list of dependants is larger than the qty passed through.
                if (order_item.getQuantity() > qty) {
                    order_item.setQuantity(order_item.getQuantity() - qty);
                    is_removed = true;
                } else if (order_item.getQuantity() == qty) {
                    dependants.remove(order_item);
                    is_removed = true;
                }
            }
            index++;
        }
        if (is_removed && removeFromPrice) {
            for (int i = 0; i < qty; i++) {
                if (price_of_item_to_remove != null) {
                    price.subCash(price_of_item_to_remove);
                } else {
                    Item item = StorageAccess.instance().getItem(item_ref);
                    price.subCash(item.getMarkupPrice());
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * If order item is variant item then swaps dependent to be the next variation.
     * @return the new order item variant
     */
    public OrderItem swapWithNextVariant() {
        Item item = StorageAccess.instance().getItem(getItem());

        if (item instanceof VariantItem) {
            int current_variant_index = ((VariantItem) item).getVariants().indexOf(this.getDependants().get(0).getItem());
            int new_variant_index = (current_variant_index + 1) % ((VariantItem) item).getVariants().size();
            Item_Ref variant_ref = ((VariantItem) item).getVariants().get(new_variant_index);

            dependants.clear();
            return this.addToOrder(variant_ref, 1, null, 0);
        }
        return null;
    }

    /**
     * If order item is variant item then swaps dependent to be the prev variation.
     * @return the new order item variant
     */
    public OrderItem swapWithPrevVariant() {
        Item item = StorageAccess.instance().getItem(getItem());

        if (item instanceof VariantItem) {
            int current_variant_index = ((VariantItem) item).getVariants().indexOf(this.getDependants().get(0).getItem());
            int new_variant_index = Math.floorMod((current_variant_index - 1), ((VariantItem) item).getVariants().size());
            Item_Ref variant_ref = ((VariantItem) item).getVariants().get(new_variant_index);

            dependants.clear();
            return this.addToOrder(variant_ref, 1, null, 0);
        }
        return null;
    }

    /**
     * This method was used for testing the addToOrder method's hierarchical structure.
     * @param current_depth Initially 0 should be the input, as the recursion depth increases, so does the current
     *                      depth.
     * @return A string containing the hierarchical structure of the OrderItem and everything below it in the tree.
     */
    public String getOrderTreeRepr(int current_depth) {
        Item item = StorageAccess.instance().getItem(getItem());
        String order_name = "(Empty)";
        if (item != null) {
            order_name = item.getName();
        }

        String spacer = String.join("", Collections.nCopies(current_depth, "|--"));
        String line = spacer + "+ " + order_name + "\n";

        StringBuilder output = new StringBuilder();
        output.append(line);
        for (OrderItem dependant : getDependants()) {
            output.append(dependant.getOrderTreeRepr(current_depth + 1));
        }

        if (current_depth > 0 && getDependants().size() > 0) {
            output.append("|" + String.join("", Collections.nCopies(Math.max(current_depth - 1, 0)
                    , "--|")) + "\n");
        }
        return output.toString();
    }

    /**
     * Used for testing outputs, whats in an order.
     * @return The item name and the number of items.
     */
    @Override
    public String toString() {
        Item item = StorageAccess.instance().getItem(getItem());
        if (item != null) {
            return item.getName() + "x" + quantity;
        }
        return "(NULL Item)";
    }

    /**
     * This method returns a string representation of the everything below the OrderItem in the hierarchy tree,
     * where dashed lines represent the items inside the order.
     *
     * @param current_depth Pass through 0 initially, used to determine how much we need to indent the line.
     * @param wants_price Contains true if we want the representation to include the price of each OrderItem at a
     *                    current depth of 0.
     * @return A string containing all the dependants and what makes up those dependants in a hierarchical form.
     */
    public String getCleanOrderRepresentation(int current_depth, boolean wants_price) {
        Item item = StorageAccess.instance().getItem(getItem());
        String order_name;
        String line = "";
        if (item != null) {
            order_name = item.getName();
            String spacer = String.join("", Collections.nCopies(current_depth - 1, "  "));
            if (current_depth == 1) {
                line = spacer + order_name;
                if (wants_price) {
                    line += " @ "+this.getPrice()+" each";
                }
                line += "\n";
            } else {
                line = spacer + "  - " + order_name;
                if ((item instanceof VariantItem) || (item instanceof CompositeItem)) {
                    line += "\n";
                }
            }
        }
        StringBuilder output = new StringBuilder();
        output.append(line);
        for (OrderItem dependant : getDependants()) {
            output.append(dependant.getCleanOrderRepresentation(current_depth + 1, wants_price));
        }
        if (!(item instanceof VariantItem) && !(item instanceof CompositeItem)) {
            output.append("\n");
        }
        return output.toString();
    }
}
