package seng202.teamsix.data;


import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class UUID_EntityTest {

    @Test
    public void testGenerateUUID() {
        // If this function ever fails everyone buy lotto tickets.

        UUID_Entity entity = new UUID_Entity();
        Set<UUID> generated_uuids = new HashSet<UUID>();

        for(int i = 0; i < 1000; i++) {
            entity.generateUUID();

            assertFalse(generated_uuids.contains(entity.getUUID()));
            generated_uuids.add(entity.getUUID());
        }
    }

    @Test
    public void testSetUUID() {
        long MSB = 0x0000DEADBEEF0000L;
        long LSB = 0x1111FEEDC0DE1111L;

        UUID_Entity entity = new UUID_Entity();
        entity.setUUID(MSB, LSB);
        assertEquals(entity.getUUID().getMostSignificantBits(), MSB);
        assertEquals(entity.getUUID().getLeastSignificantBits(), LSB);
    }

    @Test
    public void testEquals() {
        UUID_Entity entity = new UUID_Entity();
        entity.generateUUID();

        UUID_Entity same_entity = new UUID_Entity();
        same_entity.setUUID(entity.getUUID().getMostSignificantBits(), entity.getUUID().getLeastSignificantBits());

        UUID_Entity different_entity = new UUID_Entity();
        different_entity.generateUUID();

        System.out.println(entity.getUUID().toString());
        System.out.println(same_entity.getUUID().toString());
        System.out.println(different_entity.getUUID().toString());

        assertEquals(entity, same_entity);
        assertNotEquals(entity, different_entity);
    }

    @Test
    public void setUUIDbyString() {
        StorageAccess.initTestMode("ItemTest");
        UUID_Entity entity = new UUID_Entity();
        entity.setUUID("79e1c5bf-ecca-4d8b-a3a5-1c0166c9f994");

        Item item = StorageAccess.instance().getItem(new Item_Ref(entity));
        assertNotNull(item);
        assertTrue(item instanceof CompositeItem);
        assertEquals(item.getName(), "Cheese Burger Combo");
    }
}