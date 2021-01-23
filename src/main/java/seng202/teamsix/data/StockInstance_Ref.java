package seng202.teamsix.data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Name: StockInstance_Ref.java
 * Date: August - September, 2019
 * Author(s): Connor Macdonald, Andrew Clifford
 */

@XmlRootElement(name="order_ref")
public class StockInstance_Ref extends UUID_Entity {
    public StockInstance_Ref() {}
    public StockInstance_Ref(UUID_Entity entity) {
        uuid = entity.uuid;
    }

    public StockInstance_Ref copyRef() {
        StockInstance_Ref ref = new StockInstance_Ref();
        ref.uuid = uuid;
        return ref;
    }
}
