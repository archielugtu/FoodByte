package seng202.teamsix.data;

import javax.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;

/** Name: Item.java
 *
 * Class Item, essentially the item that is going to be listed in the menu. Can consist of multiple Compost or variant items.
 * Consists of a name, description, price that the business purchased the stock at, selling price (markup_price), optional
 * recipe, an Arraylist consisting of tags to indicate whether the Item is gluten-free etc, and a unit type.
 *
 * Date: August, 2019
 * Author: Hamesh Ravji
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({VariantItem.class, CompositeItem.class})
public class Item extends Item_Ref {
    @XmlElement @QueryField
    private String name;
    @XmlElement @QueryField("desc")
    private String description;
    @XmlElement @QueryField
    private Currency base_price;
    @XmlElement @QueryField
    private Currency markup_price;
    @XmlElement
    private Recipe recipe;
    @XmlElement
    private ArrayList<ItemTag_Ref> tags;
    @XmlElement
    private UnitType qty_unit;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setBasePrice(Currency base_price) {
        this.base_price = base_price;
    }

    public Currency getBasePrice() {
        return base_price;
    }

    public void setMarkupPrice(Currency markup_price) {
        this.markup_price = markup_price;
    }

    public Currency getMarkupPrice() {
        return markup_price;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setTags(ArrayList<ItemTag_Ref> tags) {
        this.tags = tags;
    }

    public ArrayList<ItemTag_Ref> getTags() {
        return tags;
    }

    public void setQtyUnit(UnitType type) {
        this.qty_unit = type;
    }

    public UnitType getQtyUnit() {
        return qty_unit;
    }

    public Item() {}

    /**
     * Constructor class which takes all parameters including recipe.
     * @param name The name we wish to set the item to.
     * @param description A small description we wish to set for the item.
     * @param base_price The cost price of the Item.
     * @param markup_price The selling price of the item to be listed on the menu.
     * @param recipe The recipe to assist the chefs with making the item.
     * @param tags A list of tags to indicate whether the item is gluten-free etc.
     * @param qty_unit The unit in relation to the quantity of the Item, such as sauce requires units L or ML.
     */
    public Item(String name, String description, Currency base_price, Currency markup_price, Recipe recipe,
                ArrayList<ItemTag_Ref> tags, UnitType qty_unit) {
        this.name = name;
        this.description = description;
        this.base_price = base_price;
        this.markup_price = markup_price;
        this.recipe = recipe;
        this.tags = tags;
        this.qty_unit = qty_unit;
    }

    /**
     * Constructor class for those items which do not have a recipe.
     * @param name The name we wish to set the item to.
     * @param description A small description we wish to set for the item.
     * @param base_price The cost price of the Item.
     * @param markup_price The selling price of the item to be listed on the menu.
     * @param tags A list of tags to indicate whether the item is gluten-free etc.
     * @param qty_unit The unit in relation to the quantity of the Item, such as sauce requires L or ML.
     */
    public Item(String name, String description, Currency base_price, Currency markup_price,
                ArrayList<ItemTag_Ref> tags, UnitType qty_unit) {
        this.name = name;
        this.description = description;
        this.base_price = base_price;
        this.markup_price = markup_price;
        this.tags = tags;
        this.qty_unit = qty_unit;
    }

    /**
     * Calculate the markup percentage on the item, which is essentially the percentage of the cost price added to
     * get the selling price.
     * @return The markup percentage on the Item.
     */
    public double getMarkupPercentage() {
        return (((markup_price.getTotalCash() - base_price.getTotalCash()) / base_price.getTotalCash())*100);
    }

    /**
     * Calculates the profit that can be made from selling the item at selling price.
     * @return The profit that can be made by selling the item.
     */
    @QueryField("profit")
    public Currency getProfit() {
        Currency profit = new Currency(markup_price.getTotalCash());
        profit.subCash(base_price);
        return profit;
    }

    /**
     * Check if item contains a tag
     * @param ref tag to check
     * @return true if item contains tag
     */
    public boolean hasTag(ItemTag_Ref ref) {
        // Using equals instead of contains because references may be different objects but have same uuid
        for(ItemTag_Ref tag : getTags()) {
            if(tag.equals(ref)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recursive function that creates item tree string representation.
     * Is overridden by CompositeItem and VariantItem
     * @param current_depth this should be zero if called directly used by recursion function
     * @return item tree representation
     */
    String getItemTreeRepr(int current_depth, int num_indents) {
        String indents = getIndents(num_indents);
        String spacer = String.join("", Collections.nCopies(current_depth, "|--"));
        String line = indents + spacer + "+ " + getName() + "\n";
        return line;
    }

    /**
     * This is a helper which returns a string with a number of tab indents given by the param num.
     * @param num The number of ints we want the string to include.
     * @return A string with a number of indents.
     */
    public String getIndents(int num) {
        String result = "";
        for (int i = 0; i < num; i++) {
            result += "\t";
        }
        return result;
    }

    /**
     * Overrides the toString function so when we print items, it just prints the name corresponding to the item.
     * @return The name corresponding to the item.
     */
    @Override
    public String toString() {
        return getName();
    }
}
