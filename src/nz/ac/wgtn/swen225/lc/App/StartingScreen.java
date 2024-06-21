package nz.ac.wgtn.swen225.lc.App;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import nz.ac.wgtn.swen225.lc.Recorder.Recorder;
import nz.ac.wgtn.swen225.lc.Domain.Player;

/**
 * The `MenuScreen` class represents the main menu screen of the game.
 * 
 * @author greenthom
 */
class MenuScreen extends JPanel {

    private JLabel animatedBLabel; //animated background label
    private ImageIcon animatedB; //animated background image 
    private static Timer displayTimer; //timer for the replay display
    public static int currentMove = 0; //current move of the replay
    public static ArrayList<JSONObject> gameHistory; //game history

    /**
     * Constructs the main menu screen with buttons.
     * for starting, how to play, loading, loading recording, 
     * and exiting the game.
     * 
     */
    public MenuScreen() {
        
        //set the layout of the menu screen
        setLayout(new BorderLayout());

        //set the animated background with the image and size
        animatedB = new ImageIcon(getClass().getResource("AppImages/background.gif"));
        animatedBLabel = new JLabel(animatedB);
        add(animatedBLabel, BorderLayout.CENTER);
        animatedBLabel.setPreferredSize(new Dimension(1200, 700));
        
        //set the buttons for the menu screen
        JPanel buttonPanel = new JPanel();
        int buttonSpacing = 25;
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, buttonSpacing, 10));
        buttonPanel.setBackground(new Color(255, 165, 0));
        add(buttonPanel, BorderLayout.SOUTH);

        //add start button to the menu screen with functionality
        JButton startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                continueGame(LHander.LEVEL_1); 
            }
        });

        //add load game button to the menu screen with functionality
        JButton loadGameButton = new JButton("Load Game");
        loadGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadGame();
            }
        });

        //add load recording button to the menu screen with functionality
        JButton loadRecordingButton = new JButton("Load Recording");
        loadRecordingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(); 
                fileChooser.setCurrentDirectory(new File("src/nz/ac/wgtn/swen225/lc/Recorder/Files/replayFiles"));
                int result = fileChooser.showOpenDialog(null); 

                if (result == JFileChooser.APPROVE_OPTION) { 
                    File selectedFile = fileChooser.getSelectedFile();  

                    LHander level = LHander.REPLAY_LEVEL;
                    String[] levelOptions = { "Auto-Replay", "Step-by-Step"};
                    String selectedLevel = (String) JOptionPane.showInputDialog(
                    null,
                    "Select:",
                    "Recorder Options",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                        levelOptions,
                        levelOptions[0]);

                    if (selectedLevel != null) {
                        if ("Auto-Replay".equals(selectedLevel)) {
                            gameHistory = Recorder.extractGameObj(selectedFile);
                            autoRecorder(selectedFile, level);} 
                        else if ("Step-by-Step".equals(selectedLevel)) {
                            gameHistory = Recorder.extractGameObj(selectedFile);
                            stepRecorder(selectedFile, level);
                        }
                    }
                }
            }
        });

        //add how to play button to the menu screen with functionality
        JButton helperButton = new JButton ("How To Play");
        helperButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openExplanationFrame();
            }
        });

        //add exit button to the menu screen with functionality
        JButton quitGameButton = new JButton("Exit");
        quitGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });

        //add buttons to the menu screen
        buttonPanel.add(startGameButton);
        buttonPanel.add(helperButton);
        buttonPanel.add(loadGameButton);
        buttonPanel.add(loadRecordingButton);
        buttonPanel.add(quitGameButton);
    }

    /**
     * Opens the explanation frame for how to play the game.
     * 
     */
    public static void openExplanationFrame() {
        SwingUtilities.invokeLater(() -> {
            ExplanationFrame explanationFrame = new ExplanationFrame();
            explanationFrame.setVisible(true);
        });
    }

    /**
     * Increases the speed of the replay display timer.
     * 
     */
    public static void increaseTimerSpeed() {
        int currentDelay = displayTimer.getDelay();
        int newDelay = Math.max(currentDelay - 100, 100); 
        displayTimer.setDelay(newDelay);
    }

    /**
     * Decreases the speed of the replay display timer.
     * 
     */
    public static void decreaseTimerSpeed() {
        int currentDelay = displayTimer.getDelay();
        int newDelay = currentDelay + 100; 
        displayTimer.setDelay(newDelay);
    }

    /**
     * Stops the replay display timer.
     * 
     */
    public static void stopTimer() {
        displayTimer.stop();
    }

    /**
     * Continues the game from the specified game level.
     *
     * @param level The game level to start.
     */
    public static void continueGame(LHander level) {
        Player.pInventory.clear(); 
        Game game = Game.singleton();
        game.getContentPane().removeAll();
        new LevelHandler();
        game.getContentPane().add(LevelHandler.createLevelPanel(level));
        game.pack();
        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }

    /**
     * Initiates auto-replay of a recorded game.
     *
     * @param selectedFile The selected recorded game file.
     * @param level        The game level to replay.
     */
    public static void autoRecorder(File selectedFile, LHander level) {
        //set up the game
        Game game = Game.singleton();
        game.getContentPane().removeAll();

        //set up the timer
        displayTimer = new Timer(1000, new ActionListener() {
            private int currentMove = 0;

            //set up the timer to display the replay
            @Override
            public void actionPerformed(ActionEvent e) {
                //if the current move is less than the size of the game history
                if (currentMove < gameHistory.size()) {
                    //remove all the content from the game
                    game.getContentPane().removeAll();
                    //create a new replay game
                    ReplayGame autoGame = new ReplayGame(Recorder.sendGame(gameHistory, currentMove));
                    //set the auto replay to true
                    autoGame.setAutoReplay(true);
                    game.getContentPane().add(ReplayGame.createLevelPanel(gameHistory, level, currentMove));
                    game.pack();
                    game.setLocationRelativeTo(null);
                    game.setVisible(true);
                    //increment the current move
                    currentMove++;
                } else {
                    //stop the timer and disable the speed buttons
                    ((Timer) e.getSource()).stop();
                    ReplayGame.setSpeedButtonsEnabled(false);
                }
            }
        });

        //set the timer to repeat and start the timer
        displayTimer.setRepeats(true);
        displayTimer.start();
    }

    /**
     * Gets the game history data.
     *
     * @return The list of JSON objects representing the game history.
     */
    public static ArrayList<JSONObject> getGameHistory() {
        return gameHistory;
    }

    /**
     * Initiates step-by-step replay of a recorded game.
     *
     * @param selectedFile The selected recorded game file.
     * @param level        The game level to replay.
     */
    public static void stepRecorder(File selectedFile, LHander level){
        //set up the game
        Game game = Game.singleton();
        game.getContentPane().removeAll();
  
        //set up the timer
        if (currentMove < gameHistory.size()) {
            //remove all the content from the game
            game.getContentPane().removeAll();
            //create a new replay game
            ReplayGame replayGame = new ReplayGame(Recorder.sendGame(gameHistory, currentMove));
            //set the step replay to true
            replayGame.setStepByStepMode(true);
            game.getContentPane().add(ReplayGame.createLevelPanel(gameHistory, level, currentMove));
            game.pack();
            game.setLocationRelativeTo(null);
            game.setVisible(true);   
        }
    }

    /**
     * Loads a saved game or level.
     * 
     */
    public static void loadGame() {
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
            //if the selected level is level 1, level 2, or saved level
            if ("Level 1".equals(selectedLevel)) {
                continueGame(LHander.LEVEL_1);
            } else if ("Level 2".equals(selectedLevel)) {
                continueGame(LHander.LEVEL_2);
            } else if ("Saved Level".equals(selectedLevel)) {loadSavedLevel();}
        }
    }

    /**
     * Loads a previously saved game level.
     * 
     */
    public static void loadSavedLevel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/nz/ac/wgtn/swen225/lc/Persistency/levels"));
        
        int result = fileChooser.showOpenDialog(null);

        //if the result is approved
        if (result == JFileChooser.APPROVE_OPTION) {
            //get the selected file save file
            File selectedFile = fileChooser.getSelectedFile();

            if (selectedFile != null) {
                //stop the game timer and info panel timer
                LevelHandler.gameTimer.stop();
                LevelHandler.infoPanelTimer.stop();
                LevelHandler.getDomain().getEnemies().clear();
                //clear the player inventory
                Player.pInventory.clear();
                //set the level to the saved level
                LHander level = LHander.SAVED_LEVEL;
                level.setCustomPath(selectedFile.getName().substring(0, selectedFile.getName().indexOf(".")));
                //continue the game
                continueGame(level);
            }
        }
    }

    public void exit(){
        System.exit(0);
    }
    
}