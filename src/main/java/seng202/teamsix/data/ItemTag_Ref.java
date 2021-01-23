package seng202.teamsix.data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Name: ItemTag_Ref.java
 *
 * This class is used for referencing item tags.
 *
 * Date: August, 2019
 * Author: Connor Macdonald
 */

@XmlRootElement(name="itemtag_ref")
public class ItemTag_Ref extends UUID_Entity{
    public ItemTag_Ref() {}
    public ItemTag_Ref(UUID_Entity entity) {
        uuid = entity.uuid;
    }

    public ItemTag_Ref copyRef() {
        ItemTag_Ref ref = new ItemTag_Ref();
        ref.uuid = uuid;
        return ref;
    }
}
