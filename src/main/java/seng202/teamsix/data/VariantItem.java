/**
 * Name: VariantItem.java
 * Authors: Anzac Morel
 * Date: 19/08/2019
 */

package seng202.teamsix.data;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;

@XmlRootElement
public class VariantItem extends Item {
    @XmlElementWrapper(name = "variant_list")
    @XmlElement(name = "variant")
    private ArrayList<Item_Ref> variantList = new ArrayList<>();

    public VariantItem() {}
    public VariantItem(String name, String description, Currency base_price, Currency markup_price, Recipe recipe,
                         ArrayList<ItemTag_Ref> tags, UnitType qty_unit, ArrayList<Item_Ref> variant_list) {
        super(name, description, base_price, markup_price, recipe, tags, qty_unit);
        this.variantList = variant_list;
    }

    /**
     * Gets the variant list.
     * @return ArrayList containing variants
     */
    public ArrayList<Item_Ref> getVariants() {
        return variantList;
    }

    /**
     * Sets variant list
     * @param varitants item references of possible variants for item
     */
    public void setVariants(ArrayList<Item_Ref> varitants) {
        variantList = varitants;
    }
    /**
     * Adds item to variant list.
     * @param itemToAdd to be added to the list.
     */
    public void addVariant(Item_Ref itemToAdd) {
        variantList.add(itemToAdd);
    }

    /**
     * Removes item from menu list.
     * @param itemToRemove to be removed from the list.
     */
    public void removeVariant(Item_Ref itemToRemove) {
        variantList.remove(itemToRemove);
    }

    /**
     * Check if VariantItem has a variant with tag
     * @param ref item tag to check for
     * @return true if contains a variant with tag
     */
    @Override
    public boolean hasTag(ItemTag_Ref ref) {
        boolean parent_has_tag = false;
        for(Item_Ref variant_ref : getVariants()) {
            Item variant = StorageAccess.instance().getItem(variant_ref);
            parent_has_tag |= variant.hasTag(ref);
        }

        return parent_has_tag;
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
        String line = indents + spacer + "+ " + getName() + " *\n";

        StringBuilder output = new StringBuilder();
        output.append(line);
        for(Item_Ref child_item_ref : getVariants()) {
            Item child_item = StorageAccess.instance().getItem(child_item_ref);
            if(child_item != null) {
                output.append(child_item.getItemTreeRepr(current_depth + 1, num_indents));
            }
        }
        if(current_depth > 0){
            output.append(indents + "|" + String.join("", Collections.nCopies(Math.max(current_depth-1, 0), "--|")) + "\n");
        }

        return output.toString();
    }
}
