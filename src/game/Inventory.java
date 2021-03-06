package game;

import java.util.Observable;

/**
 * Keeps track of collectible power-ups and bombs owned by a player.
 * @author Burak Gök
 */
public class Inventory extends Observable {
    
    /**
     * The quantities of items are stored at the locations which are the same as
     * their id's.
     */
    private final int[] quantity = new int[Catalog.SIZE];
    
    /**
     * Increments the quantity of the specified item by 1.
     * Equivalent to {@see #add(int, 1)}.
     */
    void add(int itemId) {
        quantity[itemId]++;
        setChanged();
    }
    
    /**
     * Updates the quantity of the specified item by the specified amount.
     */
    public void add(int itemId, int quantity) { // TODO Fix Access Privilege Violation
        if (quantity < 0)
            throw new RuntimeException("A quantity cannot be negative.");
        this.quantity[itemId] += quantity;
        setChanged();
    }
    
    /**
     * Decrements the quantity of the specified item by 1.
     */
    void remove(int itemId) {
        if (quantity[itemId] == 0)
            throw new RuntimeException("The quantity of the specified item is "
                    + "already zero.");
        if (quantity[itemId] != Integer.MAX_VALUE) { // Infinite amount
            quantity[itemId]--;
            setChanged();
        }
    }
    
    /**
     * Returns the quantity of the specified item.
     */
    public int get(int itemId) {
        if (itemId < 0 || itemId > quantity.length)
            throw new RuntimeException("Invalid Catalog Item Id!");
        return quantity[itemId];
    }
    
}
