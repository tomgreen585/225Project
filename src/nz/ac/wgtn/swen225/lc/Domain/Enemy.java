package nz.ac.wgtn.swen225.lc.Domain;

/**
 * Enemy
 * this class defines an enemy's position, direction and speed
 *
 * @author Judah Dabora
 */
public class Enemy {

    /**
     * Enemy contructor
     * @param x the X position that enemy is on.
     * @param y the Y position that enemy is on.
     * @param dir the direction that the enemy is heading (1 is up, -1 is down).
     */
    public Enemy(int x, int y, int dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.floatingPos = new float[]{(float) x, (float) y};
    }

    public float[] floatingPos; //Their movement path.
    public float moveSpeed = 0.2f; //Move speed.

    public int getX() {
        return x;
    } //returns the x position.

    public int getY() {
        return y;
    } //returns the y position.

    public int getDir() {
        return dir;
    } //returns the direction the enemy is heading in.

    /**
     * 1= up, -1 = down
     */
    public int dir = 1;

    /**
      x and y
     */
    public int x;
    public int y;

}