package nz.ac.wgtn.swen225.lc.App;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import nz.ac.wgtn.swen225.lc.Domain.Domain;
import nz.ac.wgtn.swen225.lc.Domain.Player;
import nz.ac.wgtn.swen225.lc.Persistency.Saver;
import nz.ac.wgtn.swen225.lc.Recorder.Recorder;

/**
 * The central class for the "Chips Challenge" game that represents the game window.
 * The game window contains a menu bar that allows the user to 
 * return to menu, load, save, record, how to play and exit the game.
 * 
 * @author greenthom.
 */
public class Game extends JFrame {
    
    public static Game game; //game object
    public static MenuScreen menuScreen; //menu screen object
    private JMenuBar menuBar; //menu bar for the levels
    private KeyHandler keyHandler; //key handler for the frames

    /**
     * Constructs a Game object and initializes the game's user interface.
     * Adds key listener to all frames.
     * 
     */
    public Game() {
        initUI(); //initialize the user interface
        
        //add keylistener logic to the frame to be present across all screens
        keyHandler = new KeyHandler();
        addKeyListener(keyHandler);
        setFocusable(true);
        requestFocusInWindow();
    }

    /**
     * Initializes the game's user interface by configuring the JFrame properties,
     * setting up the initial menu screen, and making the game window visible.
     * 
     */
    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //close window if exit button is pressed
        setResizable(true);
        setTitle("Chips Challenge"); //title of the window
        setVisible(true); 
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
        menuScreen = new MenuScreen(); //initial menu screen
        getContentPane().add(menuScreen); //add the menu screen to the frame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Retrieves the singleton instance of the Game class.
     * 
     * @return The singleton instance of the Game class.
     */
    public static Game singleton() {
        if (game == null) { 
            game = new Game();
        }
        return game;
    }

    /**
     * Adds a menu bar to the game window if a level is currently active.
     * 
     */
    public void addMenuBarIfLevelActive() {
        if (menuBar == null) { 
            menuBar = createMenuBar();
            setJMenuBar(menuBar);
        }
    }

    /**
     * Removes the menu bar from the game window when no level is active.
     * 
     */
    public void removeMenuBarIfNoLevelActive() {
        if (menuBar != null) {
            setJMenuBar(null);
            menuBar = null;
        }
    }

    /**
     * Creates and configures the menu bar for the game window.
     * menu button to return to menuscreen.
     * load button to load level or saved game.
     * save button to save current game.
     * save recording button to save recording of current game.
     * help button to open explanation frame.
     * exit button to exit game.
     * 
     * @return The created JMenuBar.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("Game");

        //MAIN MENU BUTTON
        JMenuItem menuReturn = new JMenuItem("Main Menu");
        menuReturn.addActionListener(e -> showConfirmationDialog("Return to Menu", this::performMenuAction));
       
        //LOAD GAME BUTTON
        JMenuItem loadMenuItem = new JMenuItem("Load Game");
        loadMenuItem.addActionListener(e -> showConfirmationDialog("Load", this::performLoadAction));

        //SAVE GAME BUTTON
        JMenuItem saveGame = new JMenuItem("Save Game");
        saveGame.addActionListener(e -> showConfirmationDialog("Save", this::performSaveAction));

        //SAVE RECORDING BUTTON
        JMenuItem saveMenuItem = new JMenuItem("Save Recording");
        saveMenuItem.addActionListener(e -> showConfirmationDialog("Record", this::performRSaveAction));
        
        //HELP MENU BUTTON
        JMenu helpMenu = new JMenu("Help");
        JMenuItem explanationMenuItem = new JMenuItem("How to Play");
        explanationMenuItem.addActionListener(e -> openExplanationFrame());
        helpMenu.add(explanationMenuItem);
        
        //EXIT BUTTON
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> showConfirmationDialog("Exit", this::performExitAction));

        //add to JMenu 
        fileMenu.add(menuReturn);
        fileMenu.add(loadMenuItem);
        fileMenu.add(saveGame);
        fileMenu.add(saveMenuItem);
        fileMenu.add(helpMenu);
        fileMenu.add(exitMenuItem);

        //add to JMenuBar
        menuBar.add(fileMenu);

        return menuBar;
    }

    /**
     * Opens the explanation frame that provides information on how to play the game.
     * 
     */
    private void openExplanationFrame() {
        SwingUtilities.invokeLater(() -> {
            ExplanationFrame explanationFrame = new ExplanationFrame();
            explanationFrame.setVisible(true);
        });
    }

    /**
     * Displays a confirmation dialog for a specific action and executes the action if confirmed.
     * 
     * @param actionName The name of the action to be performed.
     * @param action The action to be executed if confirmed.
     */
    private void showConfirmationDialog(String actionName, Runnable action) {
        int result = 
            JOptionPane.showConfirmDialog(this, "Are you sure you want to " +
            actionName.toLowerCase() + "?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            action.run();
        }
    }

    /**
     * Performs the action of saving the current game state.
     * Gets the Domain of the current level.
     * Saves the Domain using Persistency to a file.
     * 
     */
    public void performSaveAction(){
        String fileName = JOptionPane.showInputDialog(
                null,
                "Enter a File Name for the Saved Game:",
                "Save Game",
                JOptionPane.QUESTION_MESSAGE);
    
        if (fileName != null && !fileName.trim().isEmpty()) {
            Domain gameState = LevelHandler.getDomain();
            Saver.Save(gameState, fileName);
        }
    }

    /**
     * Performs the action of returning to the main menu.
     * Uses LevelHandler function
     * 
     */
    private void performMenuAction() {        
        LevelHandler.returnToMainMenu();
    }

    /**
     * Performs the action of loading a game.
     * Can pick between level 1, level 2, or a saved game.
     * 
     */
    public void performLoadAction() {
        String[] levelOptions = { "Level 1", "Level 2", "Saved Level" };
        String selectedLevel = (String) JOptionPane.showInputDialog(
                null,
                "Select a level:",
                "Load Game",
                JOptionPane.QUESTION_MESSAGE,
                null,
                levelOptions,
                levelOptions[0]);

        if (selectedLevel != null) {
            if ("Level 1".equals(selectedLevel)) {
                LevelHandler.gameTimer.stop();
                LevelHandler.infoPanelTimer.stop();
                LevelHandler.getDomain().getEnemies().clear();
                Player.pInventory.clear();
                LHander level = LHander.LEVEL_1;
                LevelHandler.continueGame(level);
            } else if ("Level 2".equals(selectedLevel)) {
                LevelHandler.gameTimer.stop();
                LevelHandler.infoPanelTimer.stop();
                LevelHandler.getDomain().getEnemies().clear();
                Player.pInventory.clear();
                LHander level = LHander.LEVEL_2;
                LevelHandler.continueGame(level);
            } else if ("Saved Level".equals(selectedLevel)) {
                MenuScreen.loadSavedLevel();
            }
        }
    }

    /**
     * Performs the action of saving a game recording.
     * Uses Recorder function to save
     * 
     */
    public void performRSaveAction() {
        String fileName = JOptionPane.showInputDialog(
                null,
                "Enter a File Name for the Recorded Game:",
                "Record Game",
                JOptionPane.QUESTION_MESSAGE);
        if (fileName != null && !fileName.trim().isEmpty()) {
            Recorder.saveJSONFile(fileName);
        }
    }
    
    /**
     * Performs the action of exiting the game.
     * 
     */
    private void performExitAction() {
        System.exit(0);
    }

}
