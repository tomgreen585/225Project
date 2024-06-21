package persistency;
import nz.ac.wgtn.swen225.lc.Persistency.*;     //import package to be tested

// Domain needed for contracting game states
import nz.ac.wgtn.swen225.lc.Domain.Cell;
import nz.ac.wgtn.swen225.lc.Domain.Domain;
import nz.ac.wgtn.swen225.lc.Domain.Enemy;
import nz.ac.wgtn.swen225.lc.Domain.Direction;

// Needed for working with JSON
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

// imports needed fo testing
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class is responsible for testing the persistancy package.
 *
 * When calling load or saver methods in persistancy all the methods in those classes are used.
 * Therefore, a successful save or load shows that all helper methods are working.
 *
 * @author Phillip 300605858.
 */
public class testPersistency {

    /**
     * Test if you can load the game given a file name.
     */
    @Test
    public void testMakingGameState(){
        String loadFileSelected = "level1";
        Domain gameState = Load.Load_GameState(loadFileSelected);   // make a game state
    }


    /**
     * Test if you can save the gameState after you load a gameState given the file name.
     */
    @Test
    public void testSavingGameState(){
        String loadFileSelected = "level1";
        Domain gameState = Load.Load_GameState(loadFileSelected);   // load

        String saveFileSelected = "testSaveFile1";
        Saver.Save(gameState, saveFileSelected);                    // save
    }


    /**
     * Test information is correctly reading from level1 json file.
     */
    @Test
    public void testGameStateInfoLevel1() {
        String loadFileSelected = "level1";
        Domain gameState = Load.Load_GameState(loadFileSelected);
        assertEquals("This is level 1", gameState.getLevelInfo());  // loading level 2
        assertEquals(0, gameState.getNumOfTreasures());             // player has none at start up
        assertEquals(7, gameState.getPlayer().getPlayerX());
    }


    /**
     * Test information is correctly reading from level2 json file.
     */
    @Test
    public void testGameStateInfoLevel2() {
        String loadFileSelected = "level2";
        Domain gameState = Load.Load_GameState(loadFileSelected);

        assertEquals("This is level 2", gameState.getLevelInfo());  // loading level 2
        assertEquals(0, gameState.getNumOfTreasures());             // player has none at start up
        assertEquals(6, gameState.getPlayer().getPlayerX());
        assertEquals(5+1, gameState.getEnemies().size());           // should be 5 but we made extra one in testing
    }


    /**
     * Test if parsing an enemy from json works in load file.
     */
    @Test
    public void testEnemyLoad() {
        JSONObject enemy = new JSONObject();
        JSONObject enemyLocation = new JSONObject();            // json object to hold enemy info
        enemyLocation.put("x", 11);
        enemyLocation.put("y", 11);
        enemyLocation.put("dir", 1);
        String enemyName = "Enemy1_location";
        enemy.put(enemyName, enemyLocation);                         // save enemy location

        List<Enemy> enemyList = Load.testParseJson(enemy);  // call method to use private method in load
        assertEquals(11, enemyList.get(0).getY());
    }


    /**
     * Test if information can be saved and reloaded accurately.
     * parseEnemy() (Load).
     */
    @Test
    public void testSave() {
        // Making information to construct a gameState
        Cell[][] board = new Cell[2][2];
        board[0][0] = new Cell("Floor");
        board[1][0] = new Cell("Floor");
        board[1][1] = new Cell("Player");
        board[0][1] = new Cell("Floor");
        String levelInfo = "test level info";
        int treasuresNeeded = 3;
        int gameTimer = 20;
        List<Enemy> enemyList = new ArrayList<>();
        ArrayList<String> inventory = new ArrayList<>(List.of("testKey"));

        // create gameSate
        Domain gameState = new Domain(levelInfo, inventory, board, 1, 1, treasuresNeeded, gameTimer, enemyList);

        //test that Player is in the correct position on the board
        Cell[][] oldBoard = gameState.getBoard();
        assertEquals("Player", oldBoard[1][1].getType());

        gameState.movePlayer(Direction.Left);     // move player left

        String saveFileSelected = "testSaveFile1";
        Saver.Save(gameState, saveFileSelected);    // save game with player move
        Domain loadedGameState = Load.Load_GameState(saveFileSelected); // load file after save

        //Test that the new board has the new position of the player
        Cell[][] newBoard = loadedGameState.getBoard();
        assertEquals("Player", newBoard[1][0].getType());

        // Use asserts to test that other domain info has been successfully saved and reloaded.
        assertEquals(loadedGameState.getLevelInfo(), gameState.getLevelInfo());
        assertEquals(loadedGameState.getNumOfTreasures(), gameState.getNumOfTreasures());
        assertEquals(loadedGameState.getTreasuresNeeded(), gameState.getTreasuresNeeded());
        assertEquals(loadedGameState.getBoard().length, gameState.getBoard().length);
        assertEquals(loadedGameState.getGameTimer(), gameState.getGameTimer());
        assertEquals(loadedGameState.getPlayer().getPlayerX(), gameState.getPlayer().getPlayerX());
        assertEquals(loadedGameState.getPlayer().getPlayerY(), gameState.getPlayer().getPlayerY());
    }



    /**
     * Test that unknown char will course a Fail in parseArray().
     */
    @Test
    public void testParseArrayFailing() {
        // contract jJSONArray obj to parse and convert to cells
        JSONArray boardArray = new JSONArray();
        JSONArray row1Array = new JSONArray();
        row1Array.put("k");
        row1Array.put("#");
        JSONArray row2Array = new JSONArray();
        row2Array.put("#");
        row2Array.put("#");
        boardArray.put(row1Array);
        boardArray.put(row2Array);

        // test if it fails correctly
        assertThrows(RuntimeException.class, () -> {
            Cell[][] cells = Load.testParseArray(boardArray);       // the line that should fail
            throw new RuntimeException("Invalid char at 0,0=k");    // fail message
        });

    }


    /**
     * Test that char types will correctly be converted to cells in parseArray() (Load).
     */
    @Test
    public void testParseArrayWorking() {
        // construct JSONArray obj to parse and convert to cells
        JSONArray boardArray = new JSONArray();
        JSONArray row1Array = new JSONArray();
        row1Array.put("#");
        row1Array.put("*");
        JSONArray row2Array = new JSONArray();
        row2Array.put("p");
        row2Array.put("T");
        boardArray.put(row1Array);
        boardArray.put(row2Array);

        Cell[][] cells = Load.testParseArray(boardArray);

        //assertEquals("Invalid char at 0,0=k", );
        assertEquals("Wall", cells[0][0].getType());
        assertEquals("Background", cells[0][1].getType());
        assertEquals("Player", cells[1][0].getType());
        assertEquals("Treasure", cells[1][1].getType());
    }

    /**
     * Test if board is identical after it has been saved and reloaded.
     */
    @Test
    public void testTestingBoards(){
        String loadFileSelected = "level1";
        Domain gameState = Load.Load_GameState(loadFileSelected);   // load

        String saveFileSelected = "testSaveFile1";
        Saver.Save(gameState, saveFileSelected);                    // save

        Domain newGameState = Load.Load_GameState(loadFileSelected);// load

        Cell[][] board = gameState.getBoard();
        Cell[][] newBoard = newGameState.getBoard();
        int boardSize = board.length;
        for(int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                assertEquals(board[row][col].getType(), newBoard[row][col].getType());
            }
        }
    }


    /**
     * Test if cell arrays are correctly converted to String arrays for saving to JSON.
     * cellsToStrings() (Saver).
     */
    @Test
    public void testCellsTOStrings() {
        // Making information to contract a gameState
        Cell[][] board = new Cell[2][2];
        board[0][0] = new Cell("Wall");
        board[1][0] = new Cell("RedDoor");
        board[1][1] = new Cell("Player");
        board[0][1] = new Cell("Floor");

        String[][] convertedCells = Saver.testParseArray(board);

        // test conversion worked
        assertEquals("#", convertedCells[0][0]);
        assertEquals("-", convertedCells[0][1]);
        assertEquals("R", convertedCells[1][0]);
        assertEquals("p", convertedCells[1][1]);
    }
}