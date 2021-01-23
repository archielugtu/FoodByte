/**
 * Name: ItemTag.java
 * Authors: George Stephenson
 * Date: 19/08/2019
 */

package seng202.teamsix.data;

import javax.xml.bind.annotation.*;

/**
 * Name: ItemTag.java
 *
 * Class for tags, an item tag can contain a name such as Gluten-Free which when added to a list of tags for an item,
 * shows us that the item is Gluten-Free.
 *
 * Date: August - September, 2019
 * Author(s): George Stephenson
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ItemTag extends ItemTag_Ref{
    // Members
    @XmlElement @QueryField
    private String name;
    @XmlElement @QueryField
    private Boolean is_dominant;

    // Constructors
    public ItemTag() {}
    public ItemTag(String name, Boolean is_dominant) {
        super();
        this.name = name;
        this.is_dominant = is_dominant;
    }

    // Methods
    /**
     * Gets the value of is_dominant.
     * Dominance determines if parent item also has tag.
     * @return Boolean true if tag is dominant
     */
    public Boolean getIsDominant() {
        return is_dominant;
    }

    /**
     * Gets the value of name.
     * @return String name of tag
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
