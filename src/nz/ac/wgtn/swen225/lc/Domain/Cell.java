package nz.ac.wgtn.swen225.lc.Domain;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.*;

/**
 * Cell holds information of each cell on the board including the images.
 * It checks if it is a key, door, wall etc.
 * Information is held via a string.
 *
 * @author Judah Dabora
 */
public class Cell extends JPanel {
    public String type; //Hold the type of the cell.
    BufferedImage imageTexture; //Holds the buffered image.

    /**
     * Cell constructor.
     *
     * @param type the type of cell.
     */
    public Cell(String type) {
        this.type = type;
        // Load the texture image based on the cell type.
        try {
            if (type.equals("Floor")) {
                imageTexture = ImageIO.read(getClass().getResource("res/Floor.png"));
            } else if (type.equals("Wall")) {
                imageTexture = ImageIO.read(getClass().getResource("res/Wall.png"));
            } else if (type.equals("Player")) {
                imageTexture = ImageIO.read(getClass().getResource("res/Player.png"));
            } else if (type.equals("RedDoor")) {
                imageTexture = ImageIO.read(getClass().getResource("res/RedDoor.png"));
            } else if (type.equals("GreenDoor")) {
                imageTexture = ImageIO.read(getClass().getResource("res/GreenDoor.png"));
            } else if (type.equals("BlueDoor")) {
                imageTexture = ImageIO.read(getClass().getResource("res/BlueDoor.png"));
            } else if (type.equals("YellowDoor")) {
                imageTexture = ImageIO.read(getClass().getResource("res/YellowDoor.png"));
            } else if (type.equals("RedKey")) {
                imageTexture = ImageIO.read(getClass().getResource("res/RedKey.png"));
            } else if (type.equals("GreenKey")) {
                imageTexture = ImageIO.read(getClass().getResource("res/GreenKey.png"));
            } else if (type.equals("BlueKey")) {
                imageTexture = ImageIO.read(getClass().getResource("res/BlueKey.png"));
            } else if (type.equals("YellowKey")) {
                imageTexture = ImageIO.read(getClass().getResource("res/YellowKey.png"));
            } else if (type.equals("Treasure")) {
                imageTexture = ImageIO.read(getClass().getResource("res/Treasure.png"));
            } else if (type.equals("Background")) {
                this.imageTexture = ImageIO.read(getClass().getResource("res/Space.png"));
            } else if (type.equals("InvisibleWall")) {
                imageTexture = ImageIO.read(getClass().getResource("res/Floor.png"));
            } else if (type.equals("InvisibleFloor")) {
                imageTexture = ImageIO.read(getClass().getResource("res/Floor.png"));
            } else if (type.equals("Exit")) {
                imageTexture = ImageIO.read(getClass().getResource("res/exit.png"));
            } else if (type.equals("ExitLockDoor")) {
                imageTexture = ImageIO.read(getClass().getResource("res/exitLock.png"));
            } else if (type.equals("Enemy")) {
                imageTexture = ImageIO.read(getClass().getResource("res/enemy.png"));
            } else if (type.equals("Question")) {
                imageTexture = ImageIO.read(getClass().getResource("res/question.png"));
            } else {
                // DEBUG - uncomment this when all valid cell types are added.
                throw new RuntimeException("Invalid Cell Type" + type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns a string from the type field.
     * @return The type of cell
     */
    public String getType() {
        return type;
    }
    @Override
    public String toString() {
        return type;
    }

    /**
     * Checks if the cell is passable (walkable).
     * @return boolean, is or isn't walkable.
     */
    public boolean isPassable() {
        return type.equals("Floor"); // Modify this condition as needed
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // if image texture exits, draw.
        if (imageTexture != null) {
            g.drawImage(imageTexture, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
