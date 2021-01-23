package seng202.teamsix.data;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DataQueryTest {
    @Test
    public void testBasicItemFieldQuery() {
        StorageAccess.initTestMode("DataQuery");

        // Create query
        DataQuery<Item> query = new DataQuery<>(Item.class);
        query.addConstraintRange("base_price", new Currency(0, 0), new Currency(2,0));
        query.sort_by("base_price", true);
        List<UUID_Entity> result = query.runQuery();



        // Validate query results
        double last_value = Double.MAX_VALUE;
        for (UUID_Entity ref : result) {
            double current_value = StorageAccess.instance().getItem(new Item_Ref((ref))).getBasePrice().getTotalCash();

            // Assert order is valid
            assertTrue(current_value <= last_value);

            // Assert condition is met
            assertTrue(current_value >= 0 && current_value <= 2);

            last_value = current_value;
        }
    }

    @Test
    public void testBasicItemMethodQuery() {
        StorageAccess.initTestMode("DataQuery");

        // Create query
        DataQuery<Item> query = new DataQuery<>(Item.class);
        query.sort_by("profit", true);
        List<UUID_Entity> result = query.runQuery();

        // Test query results
        double last_value = Double.MAX_VALUE;
        for (UUID_Entity ref : result) {
            double current_value = StorageAccess.instance().getItem(new Item_Ref((ref))).getProfit().getTotalCash();

            // Assert order is valid
            assertTrue(current_value <= last_value);

            last_value = current_value;
        }
    }

    @Test
    public void testStringSearch() {
        StorageAccess.initTestMode("DataQuery");

        // Create query
        DataQuery<ItemTag> query = new DataQuery<>(ItemTag.class);
        query.addConstraintEqual("name", "Gluten Free");
        List<UUID_Entity> result = query.runQuery();

        assertTrue(result.size() == 1);
        ItemTag tag = StorageAccess.instance().getItemTag(new ItemTag_Ref(result.get(0)));

        assertEquals(tag.getName(), "Gluten Free");

    }
}