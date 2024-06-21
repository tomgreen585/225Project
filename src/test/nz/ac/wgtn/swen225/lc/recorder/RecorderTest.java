package recorder;

import nz.ac.wgtn.swen225.lc.App.LHander;
import nz.ac.wgtn.swen225.lc.Domain.*;
import nz.ac.wgtn.swen225.lc.Persistency.Load;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static nz.ac.wgtn.swen225.lc.Recorder.Recorder.*;
import static nz.ac.wgtn.swen225.lc.Recorder.Recorder.saveJSONFile;
import static nz.ac.wgtn.swen225.lc.Recorder.RecorderHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecorderTest {
    public static final Path tempPath = Paths.get("src/nz/ac/wgtn/swen225/lc/Recorder/Files/workingFiles/tempJSONFile.json");
    public static final Path saveFilePath = Paths.get("src/nz/ac/wgtn/swen225/lc/Recorder/Files/replayFiles/");

    /**
     * Tests extracting a list of domains from JSON file.
     *
     */
    @Test
    public void testExtractGameObj() {
        LHander level = LHander.LEVEL_1;
        Domain gameState = Load.Load_GameState(level.currentPath());

        resetTempJSON();

        for (int i = 0; i < 5; i++) {
            recGameToTemp(gameState);
        }

        ArrayList<JSONObject> testTempObjs = extractGameObj(new File(String.valueOf(tempPath)));

        assertEquals(5, testTempObjs.size());
    }

    /**
     * Tests if resetTempJSON resets the file
     * back to a JSONArray with 0 elements.
     *
     */
    @Test
    public void testResetTemp() {
        resetTempJSON();

        ArrayList<JSONObject> testTempObjs = extractGameObj(new File(String.valueOf(tempPath)));

        assertEquals(0, testTempObjs.size());
    }

    /**
     * Tests saving of JSON File.
     *
     */
    @Test
    public void testSaveJSONFile() {
        LHander level = LHander.LEVEL_2;
        Domain gameState = Load.Load_GameState(level.currentPath());

        // Resets and adds gameState to tempJSONFile
        resetTempJSON();

        for (int i = 0; i < 3; i++) {
            recGameToTemp(gameState);
        }

        saveJSONFile("testSave");

        File file = new File(String.valueOf(saveFilePath));

        if (file.exists()) {
            assert(true);
        } else {
            assert(false);
        }
    }

    /**
     * Tests initialiseCells method for wall,
     * floor and background.
     */
    @Test
    public void testInitialiseCells1() {
        Cell floor = new Cell("Floor");
        Cell background = new Cell("Background");
        Cell wall = new Cell("Wall");

        char[][] charArray = {
                {'-', '-', '-'},
                {'*', '*', '*'},
                {'#', '#', '#'}
        };

        // Creates expected 2D array of cells
        Cell[][] expectedCells = {
                {floor, floor, floor},
                {background, background, background},
                {wall, wall, wall}
        };

        Cell[][] actualCells = testRecorderInitialiseCells(charArray);

        assertEquals(expectedCells.length, actualCells.length);

        // Checks each cell type matches expected value
        for (int row = 0; row < expectedCells.length; row++) {
            for (int col = 0; col < expectedCells.length; col++) {
                String expected = expectedCells[row][col].getType();
                String actual = actualCells[row][col].getType();

                assertEquals(expected, actual);
            }
        }
    }

    /**
     * Tests initialise cells for invisible wall,
     * invisible floor, question and exit.
     */
    @Test
    public void testInitialiseCells2() {
        Cell invisibleWall = new Cell("InvisibleWall");
        Cell invisibleFloor = new Cell("InvisibleFloor");
        Cell question = new Cell("Question");
        Cell exit = new Cell("Exit");

        char[][] charArray = {
                {'+', '+', '+', '+'},
                {'=', '=', '=', '='},
                {'Q', 'Q', 'Q', 'Q'},
                {'f', 'f', 'f', 'f'}
        };

        // Creates expected 2D array of cells
        Cell[][] expectedCells = {
                {invisibleWall, invisibleWall, invisibleWall, invisibleWall},
                {invisibleFloor, invisibleFloor, invisibleFloor, invisibleFloor},
                {question, question, question, question},
                {exit, exit, exit, exit}
        };

        Cell[][] actualCells = testRecorderInitialiseCells(charArray);

        assertEquals(expectedCells.length, actualCells.length);

        // Checks each cell type matches expected value
        for (int row = 0; row < expectedCells.length; row++) {
            for (int col = 0; col < expectedCells.length; col++) {
                String expected = expectedCells[row][col].getType();
                String actual = actualCells[row][col].getType();

                assertEquals(expected, actual);
            }
        }
    }

    /**
     * Tests cellToJSONObject method
     */
    @Test
    public void testCellToJSONObject() {
        JSONObject expectedObj = new JSONObject();
        expectedObj.put("x", 1);
        expectedObj.put("y", 1);
        Cell floor = new Cell("Floor");

        Cell[][] board = {
                {null, null, null},
                {null, floor, null},
                {null, null, null}
        };

        JSONObject actualObj = new JSONObject();

        // Grabs the cell from board
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                if (board[row][col] != null) {
                    actualObj = testRecorderCellToJSONObject(col, row);
                }
            }
        }

        // Checks if x and y values are expected values
        assertEquals(expectedObj.getInt("x"), actualObj.getInt("x"));
        assertEquals(expectedObj.getInt("y"), actualObj.getInt("y"));
    }
}
