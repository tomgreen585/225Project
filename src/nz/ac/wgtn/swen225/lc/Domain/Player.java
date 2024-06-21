package nz.ac.wgtn.swen225.lc.Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Player
 * This is a simple class which stores the player's inventory,
 * position, and contains a method for checking this information.
 *
 * @author Judah Dabora
 */
public class Player {

    public int x; // player position X
    public int y; // player position Y

    public static final Map<String, Integer> pInventory = new HashMap<>(); // added by tom to get it working

    /**
     * constructor for player.
     * @param playerXpos players X pos.
     * @param playerYpos players Y pos.
     */
    public Player(int playerXpos, int playerYpos) {
        this.x = playerXpos;
        this.y = playerYpos;
    }

    /**
     * returns the players X position
     * @return integer of X position
     */
    public int getPlayerX() {
        return x;
    }

    /**
     * Has the player got specific item
     * @param item the string representation of the item.
     * @return boolean if the player has item or not.
     */
    public boolean playerHasItem(String item) {
        return (pInventory.get(item) != null && pInventory.get(item) > 0);
    }

    /**
     * Getter for player Y pos
     * @return integer of Y pos
     */
    public int getPlayerY() {
        return y;
    }

    /**
     * setter for player X pos.
     * @param x what the players X pos should be.
     */
    public void setPlayerX(int x) {
        this.x = x;
    }


    /**
     * Setter for players Y pos.
     * @param y what the players Y pos should be.
     */
    public void setPlayerY(int y) {
        this.y = y;
    }

    /**
     * sets the players location based on X and Y integers.
     * @param newX x Pos.
     * @param newY Y Pos.
     */
    public void setPlayerLocation(int newX, int newY) {
        // Update the player's coordinates
        x = newX;
        y = newY;
    }
}