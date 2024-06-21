package nz.ac.wgtn.swen225.lc.Persistency;

import nz.ac.wgtn.swen225.lc.Domain.Cell;   // for cells
import nz.ac.wgtn.swen225.lc.Domain.Domain; // for getting info from gameState
import nz.ac.wgtn.swen225.lc.Domain.Enemy;  // for getting enemy information

import org.json.JSONArray;                  // for creating JSON to save
import org.json.JSONObject;

import java.io.File;                        // for writing JSON to save file
import java.io.FileWriter;
import java.io.IOException;

/**
 *  This class is designed to Save the current map and player information to JSON format.
 *  It gets the information from the game state (domain obj) and saves it to the given file name.
 *
 * @author Phillip 300605858.
 *
 */
public class Saver {

    /**
     *  This method can covert the given gameState into json format and dave it to the given file name (+.json).
     *  It will extract all the information needed from the gameState and will call additional methods to
     *  help convert to json before calling the save method.
     *
     * @param gameState, the current game state to be saved.
     * @param saveFileSelected, file to save the json to.
     */
    public static void Save(Domain gameState, String saveFileSelected){
        JSONObject data = new JSONObject();                         // Create a JSON object to hold the game state data

        // Add game state data to json obj to be saved
        data.put("levelInfo", gameState.getLevelInfo());
        data.put("treasuresNeeded", gameState.getTreasuresNeeded());
        data.put("gameTimer", gameState.getGameTimer());

        // Create an inventory array
        JSONArray inventory = new JSONArray();
        if (!gameState.getInventory().isEmpty()) {                  // check that inventory is not empty
            for (String item : gameState.getInventory()) {
                inventory.put(item);                                // add players items to json array
            }
        }
        data.put("inventory", inventory);                           // save array

        // Create a location object
        JSONObject location = new JSONObject();
        location.put("x", gameState.getPlayer().getPlayerX());
        location.put("y", gameState.getPlayer().getPlayerY());
        data.put("chap_location", location);                        // save player location

        // save enemy list
        int enemyCounter = 0;
        for(Enemy currentEnemy : gameState.getEnemies()) {          // loop through all enemies
            JSONObject enemyLocation = new JSONObject();            // json object to hold enemy info
            enemyLocation.put("x", currentEnemy.getY());
            enemyLocation.put("y", currentEnemy.getX());
            enemyLocation.put("dir", currentEnemy.getDir());
            String enemyName = "Enemy" + ++enemyCounter + "_location";  // increment to new name
            data.put(enemyName, enemyLocation);                         // save player location
        }

        // Create the board as a 2D array
        String[][] board = cellsToStrings(gameState.getBoard());    // convert cells to strings to then save
        JSONArray boardArray = new JSONArray();                     // create json array to hold the strings

        // Loop through string array and add to json array
        for (String[] row : board) {
            JSONArray rowArray = new JSONArray();
            for (String cell : row) {
                rowArray.put(cell);                                 // add current cell to json row array
            }
            boardArray.put(rowArray);                               // add row array to 2d array
        }
        data.put("board", boardArray);                              // save the 2d json array to the json obj

        saveDataToJson(saveFileSelected, data);                     // Save the data to the specified file
    }



    /**
     * Method to convert 2d array of cells from the gameState (domain obj) to a readable string array
     * which can be saved to json.
     *
     * @param cellsArray, array of domain cell types to be converted to char representation.
     * @return stringArray array of cell characters eg "*".
     */
    private static String[][] cellsToStrings(Cell[][] cellsArray) {
        int arraySize = cellsArray.length;                              // find size of array passes for loops
        String[][] stringArray = new String[arraySize][arraySize] ;     // make string array to be returned

        // loop through cells and columns in array
        for(int row = 0; row < arraySize; row++){
            stringArray[row] = new String[arraySize];                   // initialise arrays row size
            for(int col = 0 ; col < arraySize ; col++){

                String currentCellType = cellsArray[row][col].getType();// get current cells name (type)
                switch (currentCellType) {                              // switch to convert to cell to correct char
                    // Door
                    case "RedDoor" ->  stringArray[row][col] = "R";
                    case "GreenDoor" -> stringArray[row][col] = "G";
                    case "BlueDoor" -> stringArray[row][col] = "B";
                    case "YellowDoor" -> stringArray[row][col] = "Y";
                    case "ExitLockDoor" -> stringArray[row][col] = "E";

                    // keys
                    case "RedKey" -> stringArray[row][col] = "r";
                    case "GreenKey" -> stringArray[row][col] = "g";
                    case "BlueKey" -> stringArray[row][col] = "b";
                    case "YellowKey" -> stringArray[row][col] = "y";

                    // Other
                    case "Treasure" -> stringArray[row][col] = "T";
                    case "InvisibleWall" -> stringArray[row][col] = "+";
                    case "InvisibleFloor" -> stringArray[row][col] = "=";
                    case "Player" -> stringArray[row][col] = "p";
                    case "Floor" -> stringArray[row][col] = "-";
                    case "Wall" -> stringArray[row][col] = "#";
                    case "Enemy" -> stringArray[row][col] = "e";
                    case "Question" -> stringArray[row][col] = "Q";
                    case "Exit" -> stringArray[row][col] = "f";
                    case "Background" -> stringArray[row][col] = "*";

                    // Char doesn't exist, throw error with name and location for debug
                    default -> throw new RuntimeException("Invalid char at " + row + "," + col + "=" + currentCellType);
                }
            }
        }
        return stringArray;     // return the converted string array
    }


    /**
     * This function will take a file name and json object, it will then save the
     * JSON data to the filename give.
     * If this file does not exist it will make it.
     * If it does exist, it will overwrite it.
     *
     * @param fileName_, name of save file.
     * @param data JSON object containing data to save to file.
     */
    private static void saveDataToJson(String fileName_, JSONObject data) {
        String fileName = fileName_ + ".json";  // add file extension
        // Name where you want to save the JSON data
        File outputFile = new File("src/nz/ac/wgtn/swen225/lc/Persistency/levels/" + fileName);

        // Write the JSON object to the file
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(data.toString(4)); // The '4' parameter for pretty-printing with indentation
            System.out.println("Data saved to " + fileName);    // for conformation
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to access to cellsToStrings (a private method) for junit tests.
     *
     * @param cells, cells to be converted.
     * @return list of parsed cells, converted to Strings.
     */
    public static String[][] testParseArray(Cell[][] cells){
        return cellsToStrings(cells);
    }
}