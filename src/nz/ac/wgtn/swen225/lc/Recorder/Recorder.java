package nz.ac.wgtn.swen225.lc.Recorder;

import nz.ac.wgtn.swen225.lc.Domain.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.*;
import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 * Recorder class contains necessary methods for App to use
 * to save and load replay files
 *
 * @author Bernard Del Puerto
 */
public class Recorder {
    // All files paths that Recorder will be using
    /**
     * File location for creating loaded level.
     */
    protected static final File tempJSONFile = new File("src/nz/ac/wgtn/swen225/lc/Recorder/Files/workingFiles/tempJSONFile.json");
    /**
     * File location for creating empty level 1.
     */
    protected static final File emptyLevel1 = new File("src/nz/ac/wgtn/swen225/lc/Recorder/Files/workingFiles/emptyLevel1.json");
    /**
     * File location for creating empty level 2.
     */
    protected static final File emptyLevel2 = new File("src/nz/ac/wgtn/swen225/lc/Recorder/Files/workingFiles/emptyLevel2.json");

    /**
     * Records the game and stores all data after every move
     * to tempJSONFile
     *
     * @param gameState Game object being stored
     */
    public static void recGameToTemp(Domain gameState) {
        try {
            // Parse the JSON using JSON Tokener and extract gameList
            FileReader fileReader = new FileReader(tempJSONFile);
            JSONTokener tokens = new JSONTokener(fileReader);
            JSONObject jsonObject = new JSONObject(tokens);
            JSONObject save = RecorderHelper.saveGameState(gameState);
            JSONArray stateHistory = jsonObject.getJSONArray("gameList");

            stateHistory.put(save);

            // Write the JSON objects to the temporary JSON file
            try (FileWriter fileWriter = new FileWriter(tempJSONFile)) {
                fileWriter.write(jsonObject.toString(4));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes to JSON file that will be used to
     * save the game.
     *
     * @param destFileName File name that replay is being saved into
     */
    public static void saveJSONFile(String destFileName) {
        // Replace with the actual source file path
        Path sourcePath = Path.of("src/nz/ac/wgtn/swen225/lc/Recorder/Files/workingFiles/tempJSONFile.json");
        // Replace with the desired destination file path
        Path destPath = Path.of("src/nz/ac/wgtn/swen225/lc/Recorder/Files/replayFiles/" + destFileName + ".json");

        try {
            // Copy the source file to the destination file
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts all the contents of the given JSON file
     * and stores them in an ArrayList of JSONObjects
     * that is returned.
     *
     * @param file File that objects are being extracted from
     * @return ArrayList of JSONObjects from JSON file
     */
    public static ArrayList<JSONObject> extractGameObj(File file) {
        ArrayList<JSONObject> extractedGames = new ArrayList<>();

        try {
            // Parse the JSON using JSON Tokener
            FileReader fileReader = new FileReader(file);
            JSONTokener tokens = new JSONTokener(fileReader);
            JSONObject jsonObject = new JSONObject(tokens);
            JSONArray gamesList = jsonObject.getJSONArray("gameList");

            // Adds the game objects into the extractedGames list
            for (int i = 0; i < gamesList.length(); i++) {
                extractedGames.add(gamesList.getJSONObject(i));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return extractedGames;
    }

    /**
     * Resets JSON file for temporarily saving game data.
     * Called when player wins or loses.
     */
    public static void resetTempJSON() {
        try {
            JSONObject data = new JSONObject();

            ArrayList<JSONObject> list = new ArrayList<>();
            data.put("gameList", list);

            FileWriter fileWriter = new FileWriter(tempJSONFile.getPath());

            fileWriter.write(data.toString(4)); // The '4' parameter for pretty-printing with indentation
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Responsible for retrieving a Domain object from an ArrayList of
     * JSON objects.
     *
     * @param list List of JSON objects from replay file
     * @param index Requested index of retrieval object
     * @return Domain object at specified index
     */
    public static Domain sendGame(ArrayList<JSONObject> list, int index) {
        return RecorderHelper.JSONToGame(list.get(index));
    }
}