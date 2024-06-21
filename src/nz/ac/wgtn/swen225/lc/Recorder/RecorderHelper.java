package nz.ac.wgtn.swen225.lc.Recorder;

import nz.ac.wgtn.swen225.lc.Domain.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import static nz.ac.wgtn.swen225.lc.Recorder.Recorder.sendGame;

/**
 * RecorderHelper class contains necessary methods for Recorder class
 * to use to save and load replay files
 *
 * @author Bernard Del Puerto
 */
public class RecorderHelper {
    /**
     * Creates a JSONObject of the given gameState
     * so, it can be stored in a JSON file.
     *
     * @param gameState gameState being turned into JSONObject
     * @return JSONObject version of gameState
     */
    protected static JSONObject saveGameState(Domain gameState) {
        // Game object to be added and recorded
        JSONObject gameObject = new JSONObject();

        // Record Level information
        gameObject.put("levelInfo", gameState.getLevelInfo());

        // Record treasures needed information
        gameObject.put("treasuresNeeded", gameState.getTreasuresNeeded());

        // Record game timer
        gameObject.put("gameTimer", gameState.getGameTimer());

        // Records inventory information
        JSONArray inventory = new JSONArray();
        for(String item: gameState.getInventory()){
            // Adds player's items to JSON array
            inventory.put(item);
        }
        gameObject.put("inventory", inventory);   // save array

        // Create a location object for player
        JSONObject playerLoc = new JSONObject();
        playerLoc.put("x", gameState.getPlayer().getPlayerX() - 5);
        playerLoc.put("y", gameState.getPlayer().getPlayerY() - 5);
        gameObject.put("chap_location", playerLoc);

        // Adds all the enemies into the JSON file
        int enemyCounter = 0;
        for(Enemy currentEnemy : gameState.getEnemies()) {          // loop through all enemies
            JSONObject enemyLocation = new JSONObject();            // json object to hold enemy info
            enemyLocation.put("x", currentEnemy.getX()-5);
            enemyLocation.put("y", currentEnemy.getY()-5);
            enemyLocation.put("dir", currentEnemy.getDir());
            String enemyName = "Enemy" + ++enemyCounter + "_location";        // increment to new name
            gameObject.put(enemyName, enemyLocation);                         // save player location
        }

        // Doesn't copy background cells
        Cell[][] copy = gameState.getBoard();
        Cell[][] cells = new Cell[20][20];

        for (int row = 5; row < 25; row++) {
            for (int col = 5; col < 25; col++) {
                Cell cell = copy[row][col];
                cells[row-5][col-5] = cell;
            }
        }

        int totalTreasure = 0;
        int counterKD = 0;

        // Loops through each important cell of the board and converts into JSONObject
        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells.length; col++) {
                String currentCellType = cells[row][col].getType();

                // If it's a treasure, increment (Used for treasure name)
                if (currentCellType.equals("Treasure")) {
                    totalTreasure++;
                }

                // Grabs current cell and turns into JSONObject
                JSONObject currentCell = cellToJSONObject(col, row);

                // Names the JSONObject for the cell
                if (currentCellType.equals("Treasure")) {
                    String treasureName = currentCellType + totalTreasure;
                    gameObject.put(treasureName, currentCell);
                } else if (currentCellType.contains("Door") || currentCellType.contains("Key")) {
                    counterKD++;
                    gameObject.put(currentCellType + counterKD, currentCell);
                }
            }
        }

        return gameObject;
    }

    /**
     * Converts a cell into a JSON Object
     *
     * @param x X position of cell
     * @param y Y position of cell
     * @return Cell in JSON format
     */
    private static JSONObject cellToJSONObject(int x, int y) {
        JSONObject jsonCell = new JSONObject();

        jsonCell.put("x", x);
        jsonCell.put("y", y);

        return jsonCell;
    }

    /**
     * This is responsible for converting a JSON object to
     * a Domain object. (Taken from Persistency and modified for Recorder)
     *
     * @param jsonObject JSONObject being converted
     * @return The Domain version of jsonObject
     */

    protected static Domain JSONToGame(JSONObject jsonObject) {
        // Extracts all information required to construct a Domain
        String levelInfo = jsonObject.getString("levelInfo");
        int treasuresNeeded = jsonObject.getInt("treasuresNeeded");
        JSONObject locationObject = jsonObject.getJSONObject("chap_location");
        int playerX = locationObject.getInt("x");
        int playerY = locationObject.getInt("y");
        int gameTimer = jsonObject.getInt("gameTimer");
        JSONArray inventoryArray = jsonObject.getJSONArray("inventory");

        // Converts inventory to ArrayList of strings
        ArrayList<String> inventory = parseInventoryArray(inventoryArray);

        Cell[][] cells = constructBoard(jsonObject);
        // Converts boardArray into 2D array of Cells


        ArrayList<Enemy> enemies = new ArrayList<>();

        // Checks if an enemy is present and adds all enemies into list if true
        if (jsonObject.has("Enemy1_location")){      // level 1 doesn't include enemies, but level 2 does
            for (int i = 0; i < jsonObject.length(); i++) {
                String enemyName = "Enemy" + i + "_location";
                if (jsonObject.has(enemyName)) {
                    // Extracts enemy information and adds it to list
                    JSONObject enemyLocObject = jsonObject.getJSONObject(enemyName);
                    int enemyX = enemyLocObject.getInt("x");
                    int enemyY = enemyLocObject.getInt("y");
                    int enemyDir = locationObject.getInt("dir");

                    enemies.add(new Enemy(enemyY, enemyX, enemyDir));
                } else {break;}
            }
        }

        // Constructs Domain and returns it
        return new Domain(levelInfo, inventory, cells, playerX, playerY, treasuresNeeded, gameTimer, enemies);
    }

    /**
     * Constructs a board with given JSON values
     *
     * @param jsonObject board is being constructed from JSONObject
     * @return A fully constructed board
     */
    private static Cell[][] constructBoard(JSONObject jsonObject) {
        Cell[][] cells;
        String level = jsonObject.getString("levelInfo");

        try {
            // Parse the JSON using JSON Tokener
            FileReader fileReader = new FileReader(Recorder.emptyLevel1);

            // Determines what file to read depending on levelInfo
            if (level.equals("This is level 2")) {
                fileReader = new FileReader(Recorder.emptyLevel2);
            }

            // Grabs the empty board from the JSON file
            JSONTokener tokens = new JSONTokener(fileReader);
            JSONObject emptyLevelObj = new JSONObject(tokens);
            JSONArray boardArray = emptyLevelObj.getJSONArray("board");
            char[][] charArray = parseBoardArray(boardArray);
            cells = initialiseCells(charArray);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Loops through all objects in jsonObject
        for (String currentObj : jsonObject.keySet()) {
            JSONObject currentCell;
            int col;
            int row;

            // Checks if any of the keys meets these requirements and adds them onto cells
            if (currentObj.contains("Key") || currentObj.contains("Door")) {
                currentCell = jsonObject.getJSONObject(currentObj);
                col = currentCell.getInt("x");
                row = currentCell.getInt("y");

                // Duplicates keys if necessary
                if (currentObj.contains("GreenKey")) {
                    cells[row][col] = new Cell("GreenKey");
                } else if (currentObj.contains("RedKey")){
                    cells[row][col] = new Cell("RedKey");
                } else if (currentObj.contains("YellowKey")){
                    cells[row][col] = new Cell("YellowKey");
                } else if (currentObj.contains("BlueKey")){
                    cells[row][col] = new Cell("BlueKey");
                } else if (currentObj.contains("GreenDoor")) {
                    cells[row][col] = new Cell("GreenDoor");
                } else if (currentObj.contains("RedDoor")){
                    cells[row][col] = new Cell("RedDoor");
                } else if (currentObj.contains("YellowDoor")){
                    cells[row][col] = new Cell("YellowDoor");
                } else if (currentObj.contains("BlueDoor")){
                    cells[row][col] = new Cell("BlueDoor");
                } else if (currentObj.contains("ExitLockDoor")) {
                    cells[row][col] = new Cell("ExitLockDoor");
                }
            } else if (currentObj.equals("chap_location")) {
                currentCell = jsonObject.getJSONObject(currentObj);
                col = currentCell.getInt("x");
                row = currentCell.getInt("y");

                cells[col][row] = new Cell("Player");
            } else if (currentObj.contains("Treasure")) {
                currentCell = jsonObject.getJSONObject(currentObj);
                col = currentCell.getInt("x");
                row = currentCell.getInt("y");

                cells[row][col] = new Cell("Treasure");
            } else if (currentObj.contains("Enemy")) {
                currentCell = jsonObject.getJSONObject(currentObj);
                col = currentCell.getInt("x");
                row = currentCell.getInt("y");

                cells[row][col] = new Cell("Enemy");
            }
        }

        return cells;
    }

    /**
     * Converts JSON array of inventory into an ArrayList
     * of strings to return.
     * (Taken from Persistency and modified for Recorder)
     *
     * @param jsonArray Array being converted to List of strings
     * @return Inventory in JSON file in ArrayList of strings
     */
    private static ArrayList<String> parseInventoryArray(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<>();

        if (jsonArray != null) {
            int len = jsonArray.length();

            // Grab every item in JSON array and add it to list
            for (int i = 0; i < len; i++) {
                list.add(jsonArray.get(i).toString());
            }
        }
        return list;
    }

    /**
     * Parses board array from JSON file and returns an array of chars.
     * (Taken from Persistency and modified for Recorder)
     *
     * @param boardArray Array from JSON being turned into 2D array of chars
     * @return 2D Array of chars for board
     */
    private static char[][] parseBoardArray(JSONArray boardArray) {
        // Find the dimensions of the 2D array
        int rows = boardArray.length();
        int cols = boardArray.getJSONArray(0).length();

        // Create a Java 2D array to store the items
        char[][] charArray = new char[rows][cols];

        // Loop through JSON array and convert to java
        for (int i = 0; i < rows; i++) {
            JSONArray rowArray = boardArray.getJSONArray(i);
            for (int j = 0; j < cols; j++) {
                char item = rowArray.getString(j).charAt(0);
                charArray[i][j] = item;
            }
        }

        return charArray;
    }

    /**
     * Creates a 2D array of Cells for Domain object
     * (Taken from Persistency and modified for Recorder)
     *
     * @param charArray Array being used to convert to 2D array of cells
     * @return A 2D array of Cells
     */
    private static Cell[][] initialiseCells(char[][] charArray) {
        int boardWidth = charArray.length;

        Cell[][] cellsArray = new Cell[boardWidth][];   // initialise cells
        // loop through cells and columns in board
        for (int row = 0; row < boardWidth; row++) {
            cellsArray[row] = new Cell[boardWidth];
            for (int col = 0; col < boardWidth; col++) {

                char c = charArray[row][col];   // get current cells char
                switch (c) {
                    case '*' -> cellsArray[row][col] = new Cell("Background");
                    case '-' -> cellsArray[row][col] = new Cell("Floor");
                    case '#' -> cellsArray[row][col] = new Cell("Wall");
                    case '+' -> cellsArray[row][col] = new Cell("InvisibleWall");
                    case '=' -> cellsArray[row][col] = new Cell("InvisibleFloor");
                    case 'Q' -> cellsArray[row][col] = new Cell("Question");
                    case 'f' -> cellsArray[row][col] = new Cell("Exit");

                    // Char doesn't exist
                    default -> throw new RuntimeException("Invalid char at " + row + "," + col + "=" + c);
                }
            }
        }

        return cellsArray;
    }

    /**
     * Used for testing JSONToGame.
     *
     * @param obj object being converted
     * @return domain to be tested
     */
    public static Domain testRecorderJSONToGame(JSONObject obj) {
        return JSONToGame(obj);
    }

    /**
     * Used for testing initialiseCells
     *
     * @param charArray Array being converted to board of cells
     * @return Board of cells
     */
    public static Cell[][] testRecorderInitialiseCells(char[][] charArray) {
        return initialiseCells(charArray);
    }

    /**
     * Used for testing cellToJSON method
     *
     * @param x X position of cell
     * @param y Y positiong og cell
     * @return JSONObject of cell with its positions
     */
    public static JSONObject testRecorderCellToJSONObject(int x, int y) {return cellToJSONObject(x, y);}

}