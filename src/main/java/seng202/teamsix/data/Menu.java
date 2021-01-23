/**
 * Name: Menu.java
 * Authors: Anzac Morel, Connor Macdonald
 *
 * A menu contains a list of menu items as well a name and a description.
 *
 * Date: 19/08/2019
 */

package seng202.teamsix.data;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class Menu extends Menu_Ref {
    @XmlElementWrapper(name = "menuitem_list")
    @XmlElement(name = "menuitem")
    public ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    @XmlElement @QueryField
    public String name;
    @XmlElement @QueryField("desc")
    public String description;

    /**
     * Sets the value of name.
     * @param newName containing the value of name.
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * Gets the value of name.
     * @return String name of menu
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of description.
     * @param newDescription containing the value of the description.
     */
    public void setDescription(String newDescription) {
        description = newDescription;
    }

    /**
     * Gets the value of description.
     * @return String description of menu
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the menu items in the menu.
     * @return ArrayList containing menu items
     */
    public ArrayList<MenuItem> getMenuItems() {
         return menuItems;
    }

    /**
     * Adds item to menu list.
     * @param itemToAdd to be added to the list.
     */
    public void addToMenu(MenuItem itemToAdd) {
        menuItems.add(itemToAdd);
    }

    /**
     * Removes item from menu list.
     * @param itemToRemove to be removed from the list.
     */
    public void removeFromMenu(MenuItem itemToRemove) {
        menuItems.remove(itemToRemove);
    }
}
