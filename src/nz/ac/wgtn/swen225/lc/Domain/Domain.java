package nz.ac.wgtn.swen225.lc.Domain;

//import apple.laf.JRSUIConstants.Direction;

import java.util.ArrayList; // import the ArrayList class
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Domain class - runs game logic.
 *
 * @author Judah Dabora
 */
public class Domain {
    /**
     * This class is used to make a domain obj (game state).
     * it will hold the 2d array for the board.
     * includes getters and setters to edit game state
     * <p>
     * called from persistency when load is called from app
     */
    public Cell[][] board;  //Field that holds the board.
    public String levelInfo; //Which level the player is on.
    public ArrayList<String> inventory; //List that holds the players inventory.
    public final int treasuresNeeded; //Field that tracks how many treasures are needed in the current level.
    public int numOfTreasures = 0; //Field that holds the number of treasures the player has.
    public Level currentLevel; //Field for the current level.
    private static boolean levelCompleted = false; //Has the player completed the level.
    private static boolean playerDied = false; //Has the player died.
    private static boolean triggeredQuestion = false; //Has the player moved onto a question cell.


    public int gameTimer;   //Field that holds the game time.

    public List<Enemy> enemyList;  //List holding the enemies for the level.

    // player
    public Player player;

    /**
     * Domain constructor
     * Defines a level of the game.
     *
     * @param levelInfo       which level, level1 or level 2
     * @param inventory       player's inventory (should start empty)
     * @param cells           The 2d array of cells
     * @param playerX         Player's starting X (col)
     * @param playerY         Player's starting Y (row)
     * @param treasuresNeeded how many treasures to finish the level
     * @param gameTimer       An integer, the number of seconds before losing the level
     * @param enemies         The list of enemy objects
     */
    public Domain(String levelInfo, ArrayList<String> inventory, Cell[][] cells, int playerX, int playerY,
                  int treasuresNeeded, int gameTimer, List<Enemy> enemies) {
        // Initialise variables
        this.levelInfo = levelInfo;
        this.inventory = inventory;
        this.board = cells;
        this.player = new Player(playerX, playerY);
        this.treasuresNeeded = treasuresNeeded;
        currentLevel = new Level(cells);
        this.gameTimer = gameTimer;
        this.enemyList = enemies;
    }

    /**
     * getGameTimer
     *
     * @return int game timer
     */
    public int getGameTimer() {
        return this.gameTimer;
    }

    /**
     * decrementTimer
     * Reduce the game's timer integer by 1
     */
    public void decrementTimer() {
        this.gameTimer -= 1;
    }

    /**
     * setCell
     * Sets the board at [y][x] to be the cell newCell
     *
     * @param x       the coord of the cell
     * @param y       the coord of the cell
     * @param newCell The cell to set at that position
     */
    public void setCell(int x, int y, Cell newCell) {
        this.board[y][x] = newCell; // [y][x] not error they are just the other way in 2 d array
        currentLevel.getTiles()[y][x] = newCell;
        currentLevel = new Level(board);
    }

    /**
     * getLevelInfo
     *
     * @return a string of level info
     */
    public String getLevelInfo() {
        return levelInfo;
    }

    /**
     * getInventory
     *
     * @return list of strings defining players inventory
     */
    public ArrayList<String> getInventory() {
        return inventory;
    }

    /**
     * getBoard
     *
     * @return the 2d array of cells definig the board
     */
    public Cell[][] getBoard() {
        return board;
    }

    /**
     * setBoard
     *
     * @param newBoard the new 2d array of cells to be the board
     */
    public void setBoard(Cell[][] newBoard) {
        this.board = newBoard;
    }

    /**
     * getPlayer
     *
     * @return the player object for the current level
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * addTreasure
     * increments the number of treasures needed to finish the level
     */
    public void addTreasure() {
        this.numOfTreasures += 1;
    }

    /**
     * getTreasuresNeeded
     *
     * @return the number of treasures needed to finish the level
     */
    public int getTreasuresNeeded() {
        return treasuresNeeded;
    }

    /**
     * getNumOfTreasures
     *
     * @return the number of treasures the player currently has
     */
    public int getNumOfTreasures() {
        return numOfTreasures;
    }

    /**
     * hasAllT
     * boolean flag for checking that all treasures have been obtained
     *
     * @return true if got all treasures
     */
    public Boolean hasAllT() {
        return numOfTreasures == treasuresNeeded;
    }

    /**
     * isValidPosition
     * checks if the position exists within the board boundaries
     *
     * @param x the column
     * @param y the row
     * @return true if within boundaries
     */
    private boolean isValidPosition(int x, int y) {
        // Check if the coordinates are within the bounds of your board
        return x >= 0 && x < board[0].length && y >= 0 && y < board.length;
    }

    /**
     * movePlayer
     * shifts the player's position on the board and checks
     * for collisions with other game objects
     *
     * @param direction the direction to move the player
     */
    public void movePlayer(Direction direction) {
        int playerX = player.getPlayerX();
        int playerY = player.getPlayerY();

        int newX = playerX + direction.x;
        int newY = playerY + direction.y;

        try {
            if (!isValidPosition(newX, newY))
                throw new IllegalStateException("Illegal move - tried to move off board!");
            Cell newCell = currentLevel.getTiles()[newX][newY];

            if (newCell == null) throw new IllegalStateException("Target cell was null for some reason :(");


            if (newCell.getType().equals("GreenKey") || newCell.getType().equals("RedKey") ||
                    newCell.getType().equals("BlueKey") || newCell.getType().equals("YellowKey")) {

                // play key pickup sound
                SoundPlayer.playSound("key");

                // get key
                String keyType = newCell.getType();
                Player.pInventory.put(keyType, Player.pInventory.getOrDefault(keyType, 0) + 1);
                inventory.add(keyType);

                // move player into key former space
                currentLevel.getTiles()[newX][newY] = new Cell("Floor");
                currentLevel.getTiles()[playerX][playerY] = new Cell("Floor");
                currentLevel.getTiles()[newX][newY] = new Cell("Player");
                player.setPlayerLocation(newX, newY);
            } else if (newCell.getType().equals("Treasure")) {

                // play treasure get sound
                SoundPlayer.playSound("treasure");

                addTreasure();
                currentLevel.getTiles()[newX][newY] = new Cell("Floor");
                currentLevel.getTiles()[playerX][playerY] = new Cell("Floor");
                currentLevel.getTiles()[newX][newY] = new Cell("Player");
                player.setPlayerLocation(newX, newY);
                //add treasure to the inventory as well as the number of treasures gotten
                Player.pInventory.put("Treasure", Player.pInventory.getOrDefault("Treasure", 0) + 1);
                //inventory.add("Treasure", Player.pInventory.getOrDefault("Treasure", 0) + 1);


            } else if (newCell.isPassable()
                    || (newCell.getType().equals("GreenDoor") && player.playerHasItem("GreenKey"))
                    || (newCell.getType().equals("RedDoor") && player.playerHasItem("RedKey"))
                    || (newCell.getType().equals("BlueDoor") && player.playerHasItem("BlueKey"))
                    || (newCell.getType().equals("YellowDoor") && player.playerHasItem("YellowKey"))) {
                if (!newCell.getType().equals("GreenDoor") || player.playerHasItem("GreenKey")
                        || !newCell.getType().equals("RedDoor") || player.playerHasItem("RedKey")
                        || !newCell.getType().equals("BlueDoor") || player.playerHasItem("BlueKey")
                        || !newCell.getType().equals("YellowDoor") || player.playerHasItem("YellowKey")
                        || newCell.isPassable()) {

                    // play floor footstep sound
                    if (newCell.getType().equals("Floor")) SoundPlayer.playSound("footstep");
                    else SoundPlayer.playSound("unlock");

                    currentLevel.getTiles()[playerX][playerY] = new Cell("Floor");
                    currentLevel.getTiles()[newX][newY] = new Cell("Player");
                    player.setPlayerLocation(newX, newY);

                    //if player used key for door, remove key from inventory
                    if (newCell.getType().equals("GreenDoor") && player.playerHasItem("GreenKey")) {
                        Player.pInventory.put("GreenKey", Player.pInventory.getOrDefault("GreenKey", 0) - 1);
                        inventory.remove("GreenKey");
                    } else if (newCell.getType().equals("RedDoor") && player.playerHasItem("RedKey")) {
                        Player.pInventory.put("RedKey", Player.pInventory.getOrDefault("RedKey", 0) - 1);
                        inventory.remove("RedKey");
                    } else if (newCell.getType().equals("BlueDoor") && player.playerHasItem("BlueKey")) {
                        Player.pInventory.put("BlueKey", Player.pInventory.getOrDefault("BlueKey", 0) - 1);
                        inventory.remove("BlueKey");
                    } else if (newCell.getType().equals("YellowDoor") && player.playerHasItem("YellowKey")) {
                        Player.pInventory.put("YellowKey", Player.pInventory.getOrDefault("YellowKey", 0) - 1);
                        inventory.remove("YellowKey");
                    }
                }
            } else if (newCell.getType().equals("ExitLockDoor") && hasAllT()) {

                // play unlock sound
                SoundPlayer.playSound("unlock");

                currentLevel.getTiles()[playerX][playerY] = new Cell("Floor");
                currentLevel.getTiles()[newX][newY] = new Cell("Player");
                player.setPlayerLocation(newX, newY);
            }
            // else if (newCell.getType().equals("Exit") && hasAllT()) {
            else if (newCell.getType().equals("Exit")) {
                // play level finish sound
                SoundPlayer.playSound("level_finish");

                currentLevel.getTiles()[playerX][playerY] = new Cell("Floor");
                currentLevel.getTiles()[newX][newY] = new Cell("Player");
                player.setPlayerLocation(newX, newY);
                levelCompleted = true;
            } else if (newCell.getType().equals("Enemy")) {
                SoundPlayer.playSound("die");
                playerDied = true;
                currentLevel.getTiles()[playerX][playerY] = new Cell("Floor");
            } else if (newCell.getType().equals("Question")) {
                if (!isQuestionTriggered()) {
                    // play question sound
                    SoundPlayer.playSound("question");
                    triggeredQuestion = true;
                }
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * moveEnemies
     * updates every enemy in this level's enemyList, changing their
     * directions when they encounter walls, updating their position
     * and killing the player on collision
     */
    public void moveEnemies() {
        for (Enemy e : enemyList) {
            // update floating pos
            e.floatingPos = new float[]{e.floatingPos[0], e.floatingPos[1] + e.moveSpeed * e.dir};

            int newX = (int) e.floatingPos[0];
            int newY = (int) e.floatingPos[1];

            Cell newCell = currentLevel.getTiles()[newY][newX];

            if (e.x != newX || e.y != newY) { // check if we moved
                if (newCell.getType().equals("Player")) {
                    // moved into player
                    currentLevel.getTiles()[e.y][e.x] = new Cell("Floor");
                    currentLevel.getTiles()[newY][newX] = new Cell("Enemy");
                    e.x = newX;
                    e.y = newY;
                    SoundPlayer.playSound("die");
                    playerDied = true;
                } else if (newCell.getType().equals("Floor")) { // move into floor
                    currentLevel.getTiles()[e.y][e.x] = new Cell("Floor");
                    currentLevel.getTiles()[newY][newX] = new Cell("Enemy");
                    e.x = newX;
                    e.y = newY;
                } else {
                    e.dir *= -1; // flip enemy direction
                }
            }
        }
    }

    /**
     * isQuestionTriggered
     *
     * @return true if the question block has been triggered by the player
     */
    public boolean isQuestionTriggered() {
        return triggeredQuestion;
    }

    /**
     * closeQuestionBlock
     * sets questionTriggered to false, signifying that the player is
     * not reading a question block
     */
    public void closeQuestionBlock() {
        triggeredQuestion = false;
    }


    /**
     * isPlayerDied
     *
     * @return true if player is dead
     */
    public boolean isPlayerDied() {
        return playerDied;
    }

    /**
     * complete()
     *
     * @return true if level is completed
     */
    public boolean complete() {
        return levelCompleted;
    }

    /**
     * resetBooleans()
     * resets playerDied, triggeredQuestion and levelCompleted for
     * when restarting a level if you died or loaded it midway
     */
    public void resetBooleans() {
        playerDied = false;
        triggeredQuestion = false;
        levelCompleted = false;
    }

    /**
     * getEnemies
     *
     * @return the list of enemies
     */
    public List<Enemy> getEnemies() {
        return enemyList;
    }
}
