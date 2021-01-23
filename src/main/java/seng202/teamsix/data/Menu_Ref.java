package seng202.teamsix.data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Name: Menu_Ref.java
 * Date: August, 2019
 * Author: Connor Macdonald
 */

@XmlRootElement(name="menu_ref")
public class Menu_Ref extends UUID_Entity {
    public Menu_Ref() {}
    public Menu_Ref(UUID_Entity entity) {
        uuid = entity.uuid;
    }

    public Menu_Ref copyRef() {
        Menu_Ref ref = new Menu_Ref();
        ref.uuid = uuid;
        return ref;
    }
}
