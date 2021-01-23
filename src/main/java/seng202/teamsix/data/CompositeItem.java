/**
 * Name: CompositeItem.java
 * Authors: George Stephenson, Connor Macdonald, Hamesh Ravji
 * Date: 22/08/2019
 */
package seng202.teamsix.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement
public class CompositeItem extends Item{
    @XmlElementWrapper(name = "composite_item_list")
    @XmlElement(name = "item")
    private ArrayList<Item_Ref> item_list;

    /**
     * Constructor for blank composite item.
     */
    public CompositeItem() {
        super("NA", "NA", new Currency(), new Currency(), new Recipe("NA"), new ArrayList<ItemTag_Ref>(), UnitType.KG);
    }

    /**
     * Constructor that takes a list of items as parameter.
     * @param item_list List of UUID_Entity components
     */
    public CompositeItem(String name, String description, Currency base_price, Currency markup_price, Recipe recipe,
                         ArrayList<ItemTag_Ref> tags, UnitType qty_unit, ArrayList<Item_Ref> item_list) {
        super(name, description, base_price, markup_price, recipe, tags, qty_unit);
        this.item_list = item_list;
    }

    /**
     * Sets list of components.
     * @param item_list List of UUID_Entity components
     */
    public void setComponents(ArrayList<Item_Ref> item_list) {
        this.item_list = item_list;
    }

    /**
     * Getter for component items.
     * @return List of UUID_Entity components
     */
    public List<Item_Ref> getItems() {
        return item_list;
    }

    /**
     * Checks if the CompositeItem has the given tag.
     * @param ref tag to check
     * @return true if item contains tag
     */
    @Override
    public boolean hasTag(ItemTag_Ref ref) {
        // Check first if naturally contains tag
        for(ItemTag_Ref tag : getTags()) {
            if(tag.equals(ref)) {
                return true;
            }
        }
        // Check if item inherits tag from children
        if(getItems().size() > 0) {
            ItemTag tag = StorageAccess.instance().getItemTag(ref);
            Boolean parent_has_tag = tag.getIsDominant();

            for (Item_Ref child_ref : getItems()) {
                Item item = StorageAccess.instance().getItem(child_ref);
                Boolean child_has_tag = item.hasTag(ref);
                if (tag.getIsDominant()) {
                    parent_has_tag &= child_has_tag;
                } else {
                    parent_has_tag |= child_has_tag;
                }
            }
            return parent_has_tag;
        }
        return false;
    }

    /**
     * Recursive function that creates item tree string representation.
     * @param current_depth this should be zero if called directly used by recursion function
     * @return item tree representation
     */
    @Override
    String getItemTreeRepr(int current_depth, int num_indents) {
        String indents = getIndents(num_indents);
        String spacer = String.join("", Collections.nCopies(current_depth, "|--"));
        String line = indents + spacer + "+ " + getName() + "\n";

        StringBuilder output = new StringBuilder();
        output.append(line);
        for(Item_Ref child_item_ref : getItems()) {
            Item child_item = StorageAccess.instance().getItem(child_item_ref);
            if(child_item != null) {
                output.append(child_item.getItemTreeRepr(current_depth + 1, num_indents));
            }
        }
        if(current_depth > 0){
            output.append(""+indents+"|" + String.join("", Collections.nCopies(Math.max(current_depth-1, 0), "--|")) + "\n");
        }
        return output.toString();
    }
}
