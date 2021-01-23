package seng202.teamsix.data;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * A note on auto save.
 * As the class stands it will save the data upon any update to its members this guarantees that
 * the file will always be up to date but has a few caveats that I would like to note for any future development.
 * Firstly this style of auto save suffers under batch updating as it will attempt to save every update. This could
 * Be mitigated by an option to disable auto save for a time but still shows a flaw in this kind of design.
 * Secondly this will make all program updates get slower and slower over time as the file grows and takes longer to save.
 * This solution may be fine for small files but as they grow the program would become much slower. For this to be solved
 * you would have to create a far more complex xml saving system using chunk based loading and saving to ensure you only
 * edit parts of the xml file. A medium solution between these extremes would be to spilt orders from the main XML_cache
 * as this part of the application is the most likely to grow to large proportions.
 */

@XmlRootElement(name="data")
@XmlAccessorType(XmlAccessType.FIELD)
class XML_Cache {
    HashMap<Item_Ref, Item> item_map = new HashMap<>();
    HashMap<ItemTag_Ref, ItemTag> item_tag_map = new HashMap<>();
    HashMap<Menu_Ref, Menu> menu_map = new HashMap<>();
    HashMap<Order_Ref, Order> order_map = new HashMap<>();
    HashMap<StockInstance_Ref, StockInstance> stock_instance_map = new HashMap<>();
    CashRegister cashRegister = null;

    public void mergeWith(XML_Cache cache) {
        this.item_map.putAll(cache.item_map);
        this.item_tag_map.putAll(cache.item_tag_map);
        this.menu_map.putAll(cache.menu_map);
        this.order_map.putAll(cache.order_map);
        this.stock_instance_map.putAll(cache.stock_instance_map);
    }
}

/**
 * Implementation of StorageAccess that uses XML to save and load data
 */
public class XML_StorageAccess extends StorageAccess{
    // Members
    private String data_filename = "data.xml";
    private String temp_filename = "temp.xml";

    private Boolean cache_modified = false;
    private XML_Cache cache = new XML_Cache();

    private JAXBContext  contextCache;

    public XML_StorageAccess() throws JAXBException {
        contextCache = JAXBContext.newInstance(XML_Cache.class);
    }

    /**
     * @param source_dir source directory where xml files should be saved and loaded
     * @throws IOException raised if cannot create xml directory if does not exist
     */
    public XML_StorageAccess(String source_dir) throws IOException, JAXBException {

        // Create object contexts for JAXB
        contextCache = JAXBContext.newInstance(XML_Cache.class);

        initFileStructure(source_dir);

        // Load data
        loadData();
    }

    // Methods
    @Override
    public Item getItem(Item_Ref ref) {
        return cache.item_map.getOrDefault(ref, null);
    }

    @Override
    public ItemTag getItemTag(ItemTag_Ref ref) {
        return cache.item_tag_map.getOrDefault(ref, null);
    }

    @Override
    public Menu getMenu(Menu_Ref ref) {
        return cache.menu_map.getOrDefault(ref, null);
    }

    @Override
    public Order getOrder(Order_Ref ref) {
        return cache.order_map.getOrDefault(ref, null);
    };

    @Override
    public StockInstance getStockInstance(StockInstance_Ref ref) {
        return cache.stock_instance_map.getOrDefault(ref, null);
    }

    @Override
    public CashRegister getCashRegister() {
        if (cache.cashRegister == null) {
            cache.cashRegister = new CashRegister(0);
        }
    return cache.cashRegister; }

    @Override
    public void updateItem(Item item) {
        cache.item_map.put(item.copyRef(), item);
        cache_modified = true;
        saveData();
    }

    @Override
    public void updateItemTag(ItemTag tag) {
        cache.item_tag_map.put(tag.copyRef(), tag);
        cache_modified = true;
        saveData();
    }

    @Override
    public void updateMenu(Menu menu) {
        cache.menu_map.put(menu.copyRef(), menu);
        cache_modified = true;
        saveData();
    }

    @Override
    public void updateOrder(Order order) {
        cache.order_map.put(order.copyRef(), order);
        cache_modified = true;
        saveData();
    }

    @Override
    public void updateStockInstance(StockInstance stock) {
        cache.stock_instance_map.put(stock.copyRef(), stock);
        cache_modified = true;
        saveData();
    }

    @Override
    public void updateCashRegister(CashRegister register) {
        cache.cashRegister = register;
        cache_modified = true;
        System.out.print(cache.cashRegister.getRegisterAmount());
        saveData();
    }


    @Override
    public Set<Item_Ref> getAllItems() {
        return cache.item_map.keySet();
    }

    @Override
    public Set<ItemTag_Ref> getAllItemTags() {
        return cache.item_tag_map.keySet();
    }

    @Override
    public Set<Menu_Ref> getAllMenus() {
        return cache.menu_map.keySet();
    }

    @Override
    public Set<Order_Ref> getAllOrders() {
        return cache.order_map.keySet();
    }

    @Override
    public Set<StockInstance_Ref> getAllStockInstances() {
        return cache.stock_instance_map.keySet();
    }

    // Generates file if it does not exist
    private void initFileStructure(String source_dir) throws IOException {
        data_filename = source_dir + "/data.xml";
        temp_filename = source_dir + "/temp.xml";

        // Create folder if does not exist
        File dir = new File(source_dir);
        if (!dir.exists()) {
            // If cannot create folder create exception
            if(!dir.mkdir()) {
                throw new IOException(String.format("Cannot create xml directory '%s'", source_dir));
            }
        }

        // Create file if does not exist
        File file = new File(data_filename);

        if(!file.exists()) {
            cache_modified = true;
            saveData();
        }
    }

    /**
     * Loads xml data from file and into cache
     * @return
     */
    @Override
    public boolean loadData() {
        // Load File
        File file = new File(data_filename);

        // Load Cache to File
        try {
            Unmarshaller u = contextCache.createUnmarshaller();
            cache = (XML_Cache)u.unmarshal(file);
        } catch (JAXBException e) {
            // TODO(Connor): Exit program safely
            System.err.println("Could not load xml cache");
            return false;
        }
        return true;
    }

    /**
     * Saves cache to xml data
     */
    @Override
    public void saveData() {
        if(cache_modified) {
            cache_modified = false;
            System.out.print("Saving... ");
            // Create File
            File file = new File(temp_filename);

            // Write Cache to File
            try {
                Marshaller m = contextCache.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                m.marshal(cache, file);
                File dataFile = new File(data_filename);
                if(!dataFile.delete()) {
                    System.err.println("Could not delete data.xml to be replaced");
                }

                if(file.renameTo(dataFile)) {
                    System.out.println("Saved!");
                }else{
                    System.err.println("Could not replace data.xml with temp.xml");
                }
            } catch (JAXBException e) {
                System.err.println("Could not save xml cache");
                System.err.println("Reason:" + e.toString());
            }
        }
    }


    /**
     * Exports data to xml file
     * @param filename destination of xml file
     * @return true on success
     */
    @Override
    public boolean exportData(String filename) {
        // Create File
        File file = new File(filename);

        // Write Cache to File
        try {
            Marshaller m = contextCache.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(cache, file);
        } catch (JAXBException e) {
            System.err.println("Could not export xml file");
            return false;
        }

        return true;
    }


    /**
     * Combines external xml files into this storage access
     * @param import_filename filename of xml file you want to import
     * @return true on success
     */
    @Override
    public boolean importData(String import_filename) {
        // Create imported StorageAccess
        XML_StorageAccess import_storage = null;
        try {
            import_storage = new XML_StorageAccess();
        } catch (JAXBException e) {
            System.err.println("Could not initialise JAXB for file import");
            return false;
        }

        // Load data into storage access
        import_storage.data_filename = import_filename;
        if(!import_storage.loadData()) {
            System.err.println("Could not load contents of xml file for import");
            return false;
        }

        // Combine with this Storage
        cache.mergeWith(import_storage.cache);
        cache_modified = true;
        this.saveData();
        return true;
    }

}
