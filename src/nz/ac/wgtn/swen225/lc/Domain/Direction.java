package nz.ac.wgtn.swen225.lc.Domain;

/**
 * Direction
 * This enum defines a direction. For each cardinal direction there is a
 * vector, and a constructor can transform a string direction to a direction enum.
 *
 * @author Judah Dabora
 */
public enum Direction {
    /**
     * left direction relative to player.
     */
    Left(0, -1),

    /**
     * right direction relative to player.
     */
    Right(0, 1),

    /**
     * up direction relative to player.
     */
    Up(-1, 0),

    /**
     * down direction relative to player.
     */
    Down(1, 0),
    /**
     * players position.
     */
    None(0, 0);

    /**
     * X and Y represent the coordinates.
     */
    public final int x, y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
}