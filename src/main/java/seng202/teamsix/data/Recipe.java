/**
 * Name: Recipe.java
 * Author:
 * Date: August, 2019
 */

package seng202.teamsix.data;

import javax.xml.bind.annotation.*;

/**
 * Name: Recipe.java
 *
 * Holds the recipe as an object with a string attribute called method which is essentially the instructions
 * to making a certain item, such as a burger. This is a class because we may add more methods and attributes in future.
 *
 * Date: August, 2019
 * Author(s): Hamesh Ravji
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Recipe {
    @XmlElement
    private String method;

    /**
     * A getter for the method attribute.
     * @return The recipe
     */
    public String getMethod() {
        return method;
    }

    Recipe() {}
    /**
     * Constructor class for Recipe.
     * @param method String containing the instructions for making a certain item.
     */
    public Recipe(String method) {
        this.method = method;
    }
}
