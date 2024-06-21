package nz.ac.wgtn.swen225.lc.Domain;

/**
 * Level
 * this class defines a 2d array of cells which can be manipulated
 * with the method getTiles.
 * It is used to hold the state of the board that the player moves on.
 *
 * @author Judah Dabora
 */
public class Level {
    private Cell[][] tiles; // Assuming you have a 2D array of Cell objects.

    public Level(Cell[][] tiles) {
        this.tiles = tiles;
    } //sets the level.

    public Cell[][] getTiles() {
        return tiles;
    } //gets level tiles.
}