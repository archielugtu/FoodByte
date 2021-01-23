/**
 * Name: UUID_Entity.java
 * Authors: Connor Macdonald
 * Date: 19/08/2019
 */

package seng202.teamsix.data;

import javax.xml.bind.annotation.*;
import java.util.UUID;

@XmlRootElement(name = "entity")
@XmlAccessorType(XmlAccessType.NONE)
public class UUID_Entity{
    // Members
    @XmlAttribute(name = "uuid")
    protected UUID uuid;

    UUID_Entity() {
        generateUUID();
    }

    // Methods

    /**
     * Sets internal uuid
     * @param msb most significant 8 bytes in uuid
     * @param lsb least significant 8 bytes in uuid
     */
    public void setUUID(long msb, long lsb) {
        uuid = new UUID(msb, lsb);
    }

    /**
     * Sets the
     * @param str_uuid
     */
    public void setUUID(String str_uuid) {
        uuid = UUID.fromString(str_uuid);
    }

    /**
     * Returns internal uuid
     * @return uuid
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Randomly generates an internal uuid
     */
    public void generateUUID() {
        uuid = UUID.randomUUID();
    }

    /**
     * Tests if two uuid entities are equal.
     * This function should not be overwritten by subclasses as the uuid should
     * always be the condition of equality.
     * This function overwrites the Object equals as this allows for uuid to be the equality
     * in the Java Map libraries
     * @param obj compare equality with
     * @return true if equals entity
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UUID_Entity) {
            UUID_Entity entity = (UUID_Entity) obj;
            return uuid.getMostSignificantBits() == entity.uuid.getMostSignificantBits()
                    && uuid.getLeastSignificantBits() == entity.uuid.getLeastSignificantBits();
        }
        return false;
    }


    @Override
    public int hashCode() {
        return (int)(uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits());
    }

}
