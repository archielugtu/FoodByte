package seng202.teamsix.data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Name: Item_Ref.java
 *
 * Each item can be retrieved from StorageAccess using an Item_Ref reference, this allows us to load Items as we
 * need them rather than all at the start.
 *
 * Date(s): August - September, 2019
 * Author(s): Connor Macdonald
 */

@XmlRootElement(name="item_ref")
public class Item_Ref extends UUID_Entity {
    private int quantity = 1;

    public Item_Ref() {}
    public Item_Ref(UUID_Entity entity) {
        uuid = entity.uuid;
    }

    public Item_Ref copyRef() {
        Item_Ref ref = new Item_Ref();
        ref.uuid = uuid;
        return ref;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return this.quantity;
    }
}
