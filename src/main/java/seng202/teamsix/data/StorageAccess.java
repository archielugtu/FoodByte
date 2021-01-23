/**
 * Name: StorageAccess.java
 * Authors: Connor Macdonald
 * Date: 26/08/2019
 */

package seng202.teamsix.data;


import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * Storage Access is the template of data storage classes as well as a singleton with a reference to current storage access used.
 * An example usage of the class can be seen below:
 * Item_Ref item_reference;
 * Item item = StorageAccess.instance().getItem(item_reference);
 */
public abstract class StorageAccess {
    private static StorageAccess internal;

    /**
     * Singleton access method
     * @return singleton instance
     */
    public static StorageAccess instance() {
        if(internal == null) {
            //TODO(Connor): Add better error handling here
            try {
                internal = new XML_StorageAccess("data");
            } catch(IOException e) {
                System.err.println("Could not create/open data file");
            } catch(JAXBException e) {
                System.err.println("XML_StorageAccess could not create jaxb object contexts");
            }
        }

        return internal;
    }

    /**
     * Run before tests to use the custom test set
     */
    public static void initTestMode(String source_dir) {
        // TODO(Connor): Add better error handling here
        try {
            internal = new XML_StorageAccess("test_data/" + source_dir);
        } catch(IOException e) {
            System.err.println("Could not create/open test data file");
        } catch(JAXBException e) {
            System.err.println("XML_StorageAccess could not create jaxb object contexts");
        }
    }

    public UUID_Entity get(UUID_Entity ref) {
        Item item = getItem(new Item_Ref(ref));
        if(item != null) {
            return item;
        }

        ItemTag itemtag = getItemTag(new ItemTag_Ref(ref));
        if(itemtag != null) {
            return itemtag;
        }

        Menu menu = getMenu(new Menu_Ref(ref));
        if(menu != null) {
            return menu;
        }

        Order order = getOrder(new Order_Ref(ref));
        if(order != null) {
            return order;
        }

        StockInstance stock = getStockInstance(new StockInstance_Ref(ref));
        if(stock != null) {
            return stock;
        }

        return null;
    }

    public <T> Set<? extends UUID_Entity> getAllByClassType(Class<T> type) {
        if(type == Item.class) return getAllItems();
        if(type == ItemTag.class) return getAllItemTags();
        if(type == Menu.class) return getAllMenus();
        if(type == Order.class) return getAllOrders();
        if(type == StockInstance.class) return getAllStockInstances();
        return null;
    }

    // Get Methods
    public abstract Item getItem(Item_Ref ref);
    public abstract ItemTag getItemTag(ItemTag_Ref ref);
    public abstract Menu getMenu(Menu_Ref ref);
    public abstract Order getOrder(Order_Ref ref);
    public abstract StockInstance getStockInstance(StockInstance_Ref ref);
    public abstract CashRegister getCashRegister();

    // Update/Set Methods
    public abstract void updateItem(Item item);
    public abstract void updateItemTag(ItemTag tag);
    public abstract void updateMenu(Menu menu);
    public abstract void updateOrder(Order order);
    public abstract void updateStockInstance(StockInstance stock);
    public abstract void updateCashRegister(CashRegister register);

    // Get All references
    public abstract Set<Item_Ref> getAllItems();
    public abstract Set<ItemTag_Ref> getAllItemTags();
    public abstract Set<Menu_Ref> getAllMenus();
    public abstract Set<Order_Ref> getAllOrders();
    public abstract Set<StockInstance_Ref> getAllStockInstances();

    // Storage methods
    public abstract boolean loadData();
    public abstract void saveData();
    public abstract boolean exportData(String filename);

    public abstract boolean importData(String import_filename);
}
