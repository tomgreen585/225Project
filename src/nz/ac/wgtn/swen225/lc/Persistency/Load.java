package nz.ac.wgtn.swen225.lc.Persistency;

import nz.ac.wgtn.swen225.lc.Domain.Cell;   // for creating cells
import nz.ac.wgtn.swen225.lc.Domain.Domain; // for creating domain obj (game state)
import nz.ac.wgtn.swen225.lc.Domain.Enemy;  // for creating enemies

import org.json.JSONArray;                  // for parsing JSON
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;                  // for opening json files
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  This class is designed to load player information, level or past game info from JSON format.
 *  when load is called a Game objet will be returned.
 *
 *  App will determine what level is being loaded by specifying the file which is passed, Level1.json or level2.json
 *  or a past save file.
 *
 * @author Phillip 300605858.
 *
 */
public class Load {
    private static int playerX;                 // players starting x location on map
    private static int playerY;                 // players starting y location on map
    private static int gameTimer;               // time to complete level
    private static String levelInfo;            // brief info about level
    private static int treasuresNeeded;         // treasures needed to pass this level
    private static ArrayList<String> inventory; // Players loaded inventory
    private static Cell[][] cellsArray;         // Array of domain cells representing map
    private static int boardSize;               // width of chars array
    private static List<Enemy> enemies = new ArrayList<Enemy>(); // list to hold loaded enemies


    /**
     * This function is responsible for loading json from a file, making and returning a "game state" (Domain obj).
     *
     * @param fileName name of the file we are loading from (just use "loadFile").
     * @return gameState a Domain (obj) "game state" which holds the 2d board array and other game info.
     */
    public static Domain Load_GameState(String fileName) {
        System.out.println("Loading game state from: src/nz/ac/wgtn/swen225/lc/Persistency/levels/" + fileName + ".json");

        // Read input file
        try (FileReader fileReader = new FileReader("src/nz/ac/wgtn/swen225/lc/Persistency/levels/" + fileName + ".json")) {

            // Parse the JSON using JSON Tokener
            JSONTokener tokener = new JSONTokener(fileReader);
            JSONObject jsonObject = new JSONObject(tokener);

            parseJson(jsonObject);      // parse json from file

        }catch (IOException e) {        // catch error and display
            e.printStackTrace();
        }

        // make a game state (game obj) using Domain class and return it to the person asking for load
        Domain gameState = new Domain(levelInfo, inventory, cellsArray, playerX, playerY, treasuresNeeded, gameTimer, enemies);
        gameState.setBoard(cellsArray);

        return gameState;               // return newly created game state to load caller
    }


    /**
     * This function takes in the json obj from the file and pulls out the information needed to make
     * a domain obj (game state).
     * The information taken is assigned to the global variables, so they don't need to be returned.
     *
     * @param jsonObject, json object to be parsed.
     */
    private static void parseJson(JSONObject jsonObject) {
        // Extract data with correct types
        levelInfo = jsonObject.getString("levelInfo");
        treasuresNeeded = jsonObject.getInt("treasuresNeeded");
        gameTimer = jsonObject.getInt("gameTimer");

        // get the player location then strip x and y out
        JSONObject locationObject = jsonObject.getJSONObject("chap_location");
        playerX = locationObject.getInt("x");
        playerY = locationObject.getInt("y");

        // Extract the players inventory
        JSONArray inventoryArray = jsonObject.getJSONArray("inventory");
        inventory = JSONArrayConverter(inventoryArray); // convert to array list

        if(jsonObject.has("Enemy1_location")){      // level 1 doesn't include enemies, but level 2 does
            parseEnemy(jsonObject);                     // parse enemies from json obj
        }

        // Extract 2d board using a function to convert the cells
        JSONArray boardArray = jsonObject.getJSONArray("board");
        parseArray(boardArray);                         // Parse json board to array
    }


    /**
     * Function to parse enemies from json if they exist
     * to then be given to domain constructor.
     *
     * @param jsonObject, the loaded json object from the file.
     */
    private static void parseEnemy(JSONObject jsonObject) {
        for (int i = 1; i <= jsonObject.length(); i++) {
            String enemyName = "Enemy" + i + "_location";
            if(jsonObject.has(enemyName)) {                 // if it does exist, get info
                JSONObject locationObject = jsonObject.getJSONObject(enemyName);
                enemies.add(new Enemy(locationObject.getInt("y"), locationObject.getInt("x"), locationObject.getInt("dir")));
            }else{break;}                               // if it doesn't exist break loop
        }
    }


    /**
     * Convert the array of chars parsed from Json file into array of domain cells.
     *
     *  loops through each char and finds the corresponding cell name for that char
     *  and create new array containing newly converted cells.
     * @param boardArray, array to be converted.
     */
    private static void parseArray(JSONArray boardArray) {
        boardSize = boardArray.length();            // Find the dimensions of the 2D array
        cellsArray = new Cell[boardSize][];         // initialise cells array size

        // loop through cells and columns in board
        for(int row = 0; row < boardSize; row++){
            JSONArray rowArray = boardArray.getJSONArray(row);
            cellsArray[row] = new Cell[boardSize];  // initialise cells array row size
            for(int col = 0 ; col < boardSize ; col++){

                char item = rowArray.getString(col).charAt(0);  // get the char from the board at given position
                switch (item) {                     // switch statement to find propitiate cell type per char
                    // Door
                    case 'R' -> cellsArray[row][col] = new Cell("RedDoor");
                    case 'G' -> cellsArray[row][col] = new Cell("GreenDoor");
                    case 'B' -> cellsArray[row][col] = new Cell("BlueDoor");
                    case 'Y' -> cellsArray[row][col] = new Cell("YellowDoor");
                    case 'E' -> cellsArray[row][col] = new Cell("ExitLockDoor");

                    // keys
                    case 'r' -> cellsArray[row][col] = new Cell("RedKey");
                    case 'g' -> cellsArray[row][col] = new Cell("GreenKey");
                    case 'b' -> cellsArray[row][col] = new Cell("BlueKey");
                    case 'y' -> cellsArray[row][col] = new Cell("YellowKey");

                    // Other
                    case '*' -> cellsArray[row][col] = new Cell("Background");
                    case '-' -> cellsArray[row][col] = new Cell("Floor");
                    case '#' -> cellsArray[row][col] = new Cell("Wall");
                    case 'T' -> cellsArray[row][col] = new Cell("Treasure");
                    case '+' -> cellsArray[row][col] = new Cell("InvisibleWall");
                    case '=' -> cellsArray[row][col] = new Cell("InvisibleFloor");
                    case 'p' -> cellsArray[row][col] = new Cell("Player");
                    case 'e' -> cellsArray[row][col] = new Cell("Enemy");
                    case 'Q' -> cellsArray[row][col] = new Cell("Question");
                    case 'f' -> cellsArray[row][col] = new Cell("Exit");

                    // Char doesn't exist, print with type and location.
                    default -> throw new RuntimeException("Invalid char at " + row + "," + col + "=" + item);
                }
            }
        }
    }


    /**
     *  Given a Json array type this method wil convert it to java array type
     *  used for player inventory.
     *
     * @param jsonArray, array to ve converted to list.
     * @return list, java list type.
     */
    private static ArrayList<String> JSONArrayConverter(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<>();
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                list.add(jsonArray.get(i).toString());
            }
        }
        return list;
    }


    /**
     * Method to allow access to parseEnemy (a private method) for junit tests.
     *
     * @param jsonObj, object to parse.
     * @return enemies, list of the parsed enemies.
     */
    public static List<Enemy> testParseJson(JSONObject jsonObj){
        parseEnemy(jsonObj);
        return enemies;
    }


    /**
     * Method to access to parseArray (a private method) for junit tests.
     * @param jsonArray, the json array to parse.
     * @return cellsArray, the parsed cells.
     */
    public static Cell[][] testParseArray(JSONArray jsonArray){
        parseArray(jsonArray);
        return cellsArray;
    }
}
