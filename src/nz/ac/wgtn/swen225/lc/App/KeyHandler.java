package nz.ac.wgtn.swen225.lc.App;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import nz.ac.wgtn.swen225.lc.Domain.Domain;
import nz.ac.wgtn.swen225.lc.Domain.Direction;
import nz.ac.wgtn.swen225.lc.Domain.Player;
import nz.ac.wgtn.swen225.lc.Persistency.Saver;
import nz.ac.wgtn.swen225.lc.Recorder.Recorder;

/**
 * Handles keyboard input for controlling the game and managing game state.
 * 
 * @author greenthom
 */
public class KeyHandler implements KeyListener {

    private boolean isGamePaused = false; //if the game is paused or not
    private Set<Integer> pressedKeys = new HashSet<>(); //set of pressed keys

    /**
     * Checks if a specific key is currently pressed.
     *
     * @param keyCode The code of the key to check.
     * @return True if the key is currently pressed, false otherwise.
     */
    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    /**
     * Sets the game's pause state.
     *
     * @param isPaused True to pause the game, false to resume.
     */
    public void setGamePaused(boolean isPaused) {
        this.isGamePaused = isPaused;
    }

    /**
     * Handles key pressed events.
     *
     * @param e The key event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeys.add(keyCode);

        //if current level is not null
        if (LevelHandler.getCurrentLevel() != null) {

            //if the game is not paused
            if (!isGamePaused) { 

                //move player left 
                if (keyCode == KeyEvent.VK_LEFT) {
                    Domain gameState = LevelHandler.getDomain();
                    gameState.movePlayer(Direction.Left);
                    Recorder.recGameToTemp(gameState);
                }

                //move player right
                if (keyCode == KeyEvent.VK_RIGHT) {
                    Domain gameState = LevelHandler.getDomain();
                    gameState.movePlayer(Direction.Right);
                    Recorder.recGameToTemp(gameState); 
                }

                //move player up
                if (keyCode == KeyEvent.VK_UP) {
                    Domain gameState = LevelHandler.getDomain();
                    gameState.movePlayer(Direction.Up);
                    Recorder.recGameToTemp(gameState);
                }

                //move player down
                if (keyCode == KeyEvent.VK_DOWN) {
                    Domain gameState = LevelHandler.getDomain();
                    gameState.movePlayer(Direction.Down);
                    Recorder.recGameToTemp(gameState);
                }
            }

            //pause game
            if (keyCode == KeyEvent.VK_SPACE) {
                if (!isGamePaused) {
                    isGamePaused = true;
                    LevelHandler.gameTimer.stop();
                    LevelHandler.infoPanelTimer.stop();
                } else {
                    isGamePaused = false;
                    LevelHandler.gameTimer.start();
                    LevelHandler.infoPanelTimer.start();
                }
            }
               
            //unpause game
            if (keyCode == KeyEvent.VK_ESCAPE) {
                if (isGamePaused) {
                    isGamePaused = false;
                    LevelHandler.gameTimer.start();
                    LevelHandler.infoPanelTimer.start();
                }
            }

            //save game and then exit application
            if (e.isControlDown() && keyCode == KeyEvent.VK_S) {
                String fileName = JOptionPane.showInputDialog(
                    null,
                    "Enter a file name for the saved game:",
                    "Save Game",
                    JOptionPane.QUESTION_MESSAGE);
    
                if (fileName != null && !fileName.trim().isEmpty()) {
                    Domain gameState = LevelHandler.getDomain();
                    Saver.Save(gameState, fileName);
                    exit(); //spot bug throwing here however it is meant to exit... (orange bug)
                }
            }

            //quick load to level 1
            if (e.isControlDown() && keyCode == KeyEvent.VK_1) {
                LevelHandler.gameTimer.stop();
                LevelHandler.infoPanelTimer.stop();
                LevelHandler.getDomain().getEnemies().clear();
                Player.pInventory.clear();
                LHander level = LHander.LEVEL_1;
                Player.pInventory.clear();
                MenuScreen.continueGame(level);
            }
            
            //quick load to level 2
            if (e.isControlDown() && keyCode == KeyEvent.VK_2) {
                LevelHandler.gameTimer.stop();
                LevelHandler.infoPanelTimer.stop();
                LevelHandler.getDomain().getEnemies().clear();
                Player.pInventory.clear();
                LHander level = LHander.LEVEL_2;
                Player.pInventory.clear();
                MenuScreen.continueGame(level);
            }
        }
            
        //exit application
        if (e.isControlDown() && keyCode == KeyEvent.VK_X) {
            exit();
        }
        
        //open up explanation frame
        if (e.isControlDown() && keyCode == KeyEvent.VK_H) {
            MenuScreen.openExplanationFrame();
        }
        
        //load saved level
        if (e.isControlDown() && keyCode == KeyEvent.VK_R) {
            loadSavedLevel();
        }
    }
    
    /**
     * Loads a saved level from a file chosen by the user.
     * 
     */
    private void loadSavedLevel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/nz/ac/wgtn/swen225/lc/Persistency/levels"));
        
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            if (selectedFile != null) {
                LHander level = LHander.SAVED_LEVEL;
                level.setCustomPath(selectedFile.getName().substring(0, selectedFile.getName().indexOf(".")));
                MenuScreen.continueGame(level);
            }
        }
    }

    /**
     * Handles key released events.
     *
     * @param e The key event.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeys.remove(keyCode);
    }

    /**
     * Handles key typed events.
     *
     * @param e The key event.
     */
    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Handles exiting the game when saved
     *
     */
    public static void exit(){
        System.exit(0);
    }
}

