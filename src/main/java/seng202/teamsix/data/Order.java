/**
 * Name: Order.java
 * Authors: Connor Macdonald, Hamesh Ravji
 * Date: 30/08/2019
 */

package seng202.teamsix.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;

/**
 * Orders are the finalised version of an order. An order contains an order tree of OrderItems that express items ordered.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Order extends Order_Ref{
    @XmlElement
    private OrderItem order_tree;
    @XmlElement @QueryField
    private Date timestamp;
    @XmlElement
    private Currency total_cost;
    @XmlElement
    private boolean isRefunded = false;

    public boolean getIsRefunded() {
        return this.isRefunded;
    }

    public void setIsRefunded(boolean isRefunded) {
       this.isRefunded = isRefunded;
    }
    @XmlElement
    private Boolean is_eftpos = false;

    public Order() {
        order_tree = new OrderItem();
        updateTotalCost();
    }

    /**
     * This is to be used in finalise order so that the total order price is saved with the order.
     */
    public void updateTotalCost() {
        total_cost = order_tree.getPrice();
    }

    public OrderItem getOrderTree() {
        return order_tree;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * This attribute is not a unique identifier, it is only unique within each instance of OrderManager. This helps to
     * keep track of the order number so that we can send chef orders, the order numbers are also printed on the receipts.
     */
    public int localTicketNumber;

    /**
     * @return the Currency object contained in the price attribute of the root OrderItem.
     */
    public Currency getTotalCost() {
        return total_cost;
    }

    /**
     * This method prints the order as well as the local ticket number as the order number.
     */
    public String getChefOrder() {
        String returnString = "/**********  Chef's Order  **********/\n";
        returnString += "Order Number: " + localTicketNumber + "\nContents:\n";
        returnString += getOrderTree().getCleanOrderRepresentation(0, false);
        returnString += "/************************************/";
        return returnString;
    }

    /**
     * This method prints the Order in a receipt format, currently to the command line.
     */
    public String getReceipt() {
        // similar to the printChefOrder, more details.
        String returnString = "/************  Receipt  *************/\n";
        returnString += "FoodByte\n"+timestamp+"\n";

        String payment_method = "Cash";
        if (is_eftpos) {
            payment_method = "Eftpos";
        }

        returnString += "Payment Method: "+payment_method+"\n";
        returnString += "Receipt (Order Number: "+localTicketNumber+")\nContents:\n";
        returnString += getOrderTree().getCleanOrderRepresentation(0, true);
        returnString += "Total Amount Paid: "+getTotalCost()+"\n";
        returnString +=       "/************************************/";
        return returnString;
    }

    /**
     * Returns true only if the current order is empty
     * @return boolean representation of if the order is empty (has 0 items)
     */
    public boolean isEmpty() {
        return (this.getOrderTree().getDependants().size() == 0);
    }

    /**
     * A setter for orderTree so that we can add to cart
     * @param new_root The new root which we want to set for the Order object.
     */
    public void setOrderTree(OrderItem new_root) {
        order_tree = new_root;
    }

    public void setIsEftpos(Boolean is_eftpos) {
        this.is_eftpos = is_eftpos;
    }
}
