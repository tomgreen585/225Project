package nz.ac.wgtn.swen225.lc.App;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import nz.ac.wgtn.swen225.lc.Domain.Domain;
import nz.ac.wgtn.swen225.lc.Domain.Player;
import nz.ac.wgtn.swen225.lc.Persistency.Load;
import nz.ac.wgtn.swen225.lc.Recorder.Recorder;
import nz.ac.wgtn.swen225.lc.Renderer.Draw;

/**
 * Manages and controls gameplay for a specific game level, handling user input,
 * game state updates, and timers.
 * 
 * @author greenthom
 */
public class LevelHandler extends JPanel {
    
    //initializers
    private static Image bImg; //background image
    private static JPanel levelPanel; //level panel to hold board and info
    private static JPanel wholeBoard; //board panel
    private static JPanel infoPanel; //info panel
    private static Domain gameState; //object of domain for game state
    private static LHander currentLevel; //current level
    public static Timer gameTimer; //game timer to update gamestate
    public static Timer infoPanelTimer; //info panel timer to update inventory and timer
    private KeyHandler keyHandler; //key handler for the frames

    /**
     * Creates a panel for the specified game level, initializes game state, and starts timers.
     *
     * @param level The level to create and play.
     * @return The created level panel.
     */
    public static JPanel createLevelPanel(LHander level) { 
        Recorder.resetTempJSON(); //reset json recorder file
        
        //create a new panel
        levelPanel = new JPanel(new BorderLayout()); //create a new panel
        
        //set the current level
        currentLevel = level;

        gameState = Load.Load_GameState(level.currentPath()); //load the game state (persistency)
        wholeBoard = Draw.makeJPanel(gameState); //draw the game state

        //set the size of the panel
        Dimension panelSize = new Dimension(1200, 700); 
        levelPanel.setPreferredSize(panelSize); 

        infoPanel = createInfoPanel(gameState); //create the info panel

        levelPanel.add(wholeBoard, BorderLayout.WEST); //add the game state to the panel
        levelPanel.add(infoPanel, BorderLayout.EAST); //add the info panel to the panel

        Game.singleton().addMenuBarIfLevelActive(); //add the menu bar to the panel
        bImg = loadImage("AppImages/levelGameBackground.png");

        startTimer(); //start the timer

        Recorder.recGameToTemp(gameState); //record the initial game state

        return levelPanel;
    }

    /**
     * Constructs a new instance of the LevelHandler class, setting up input handling and focus.
     * 
     */
    public LevelHandler() {
        keyHandler = new KeyHandler();
        addKeyListener(keyHandler);
        setFocusable(true); 
        requestFocusInWindow();
    }

    /**
     * Retrieves the current game level being played.
     *
     * @return The current game level.
     */
    public static LHander getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Retrieves the game domain (game state) for the current level.
     *
     * @return The game domain representing the current game state.
     */
    protected static Domain getDomain() {
        return gameState;
    }

    /**
     * Loads an image resource by name.
     *
     * @param imageName The name of the image resource.
     * @return The loaded image, or null if loading failed.
     */
    private static Image loadImage(String imageName) {
        try {
            return ImageIO.read(MenuScreen.class.getResource(imageName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Starts the game timer to update the game state periodically.
     * 
     */
    private static void startTimer() {
        gameTimer = new Timer(100, new ActionListener() {
            
        @Override
        public void actionPerformed(ActionEvent e) {
            //if the game is not complete and the player has not died
            if (!gameState.complete() && !gameState.isPlayerDied()) {
                gameState.moveEnemies(); //move enemies if it is used
                levelPanel.remove(wholeBoard); //remove the game state from the panel
                wholeBoard = Draw.makeJPanel(gameState); //draw the game state
                levelPanel.add(wholeBoard, BorderLayout.CENTER); //add the game state to the panel

                //if play is on question block
                if (gameState.isQuestionTriggered() && gameTimer.isRunning()) {
                    gameTimer.stop();
                    infoPanelTimer.stop();
                    openExplanationFrame();
                }

                //revalidate and repaint the levelPanel
                levelPanel.revalidate();
                levelPanel.repaint();
                
                //if player has died
                } else if (gameState.isPlayerDied()) {
                    gameTimer.stop();
                    infoPanelTimer.stop();
                    displayPlayerFailedMessage();
                //if the game is complete
                } else {
                    gameTimer.stop();
                    infoPanelTimer.stop();
                    displayLevelCompleteMessage();
                }
            }
        });
        
        gameTimer.start();
    }

    /**
     * Opens an explanation frame to provide instructions or information to the player.
     * 
     */
    public static void openExplanationFrame() {
        SwingUtilities.invokeLater(() -> {
            ExplanationFrame explanationFrame = new ExplanationFrame();
            explanationFrame.setVisible(true);
            explanationFrame.addWindowListener(new WindowAdapter() {
                
                @Override
                public void windowClosed(WindowEvent e) {
                    gameTimer.start();
                    infoPanelTimer.start();
                    gameState.closeQuestionBlock();
                }
            });
        });
    }

    /**
     * Displays a message when the player has failed in the game.
     * If yes is selected, the game restarts.
     * If no is selected, the game returns to the main menu.
     * 
     */
    private static void displayPlayerFailedMessage() {
        int option = JOptionPane.showConfirmDialog(null,
        "You Failed.\n Would you like to restart?", "GAME OVER", 
        
        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            continueGame(currentLevel);
        } 
        //else if (option == JOptionPane.NO_OPTION) {
        else {
            returnToMainMenu();
        }
    }

    /**
     * Displays a message when the player has successfully completed a level.
     * If yes is selected, the game proceeds to the next level.
     * If no is selected, the game returns to the main menu.
     * 
     */
    private static void displayLevelCompleteMessage() {
        int option = JOptionPane.showConfirmDialog(null, 
        "Level Complete!!\n Do you want to proceed to the next level?", "Congratulations", 
        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            LHander nextLevel = currentLevel.nextLevel();
            if (nextLevel == LHander.NONE) {
                returnToMainMenu();
            } else {
                continueGame(nextLevel);
            }
        } else if(option == JOptionPane.NO_OPTION) {
            returnToMainMenu();
        }
    }
    
    /**
     * Creates an information panel that displays the current game level details and player inventory.
     *
     * @param domain The game domain to retrieve information from.
     * @return The information panel.
     */
    private static JPanel createInfoPanel(Domain domain) {
        JPanel infoPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); //paint the panel
                if (bImg != null) {
                    g.drawImage(bImg, 0, 0, getWidth(), getHeight(), this); //draw the background image
    
                    g.setColor(Color.RED); 
                    g.fillRect(100, 70, 300, 150);
    
                    g.fillRect(100, 290, 300, 150);
    
                    g.setFont(new Font("Arial", Font.BOLD, 14));
                    g.setColor(Color.WHITE);
                    String currentlyDrawing = currentLevel.currentName();
                    g.drawString("                          " + currentlyDrawing, 120, 100);
    
                    int inventoryX = 120; 
                    int inventoryY = 320; 
                    g.drawString("                Player Inventory", inventoryX, inventoryY);
    
                    int treasureX = 120;
                    int treasureY = 180;
                    g.drawString("           Treasures Collected: " + domain.getNumOfTreasures() 
                    + "/" + domain.getTreasuresNeeded() , treasureX, treasureY);
    
                    ArrayList<String> inventory = domain.getInventory(); 
    
                    for (String item : inventory) {
                        inventoryY += 20; 
                        String itemText = item;
                        g.drawString(itemText, inventoryX, inventoryY);
                    }
    
                    int timerX = 120;
                    int timerY = 140;
                    g.drawString("           Time Left: " + domain.getGameTimer() + " seconds", timerX, timerY);
    
                    repaint();
                }
            }
        };

        //handles updating of the game timer
        infoPanelTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                int currentTimerValue = domain.getGameTimer(); //get timer from domain
                if (currentTimerValue > 0) { //if timer is greater than 0
                    domain.decrementTimer(); //decrement timer
                    infoPanel.repaint(); //repaint panel
                } else if (currentTimerValue <= 0) { //if timer is less than or equal to 0
                    infoPanelTimer.stop(); //stop timer
                    displayPlayerFailedMessage(); //display player failed message
                }
            }
        });
        infoPanelTimer.start(); //start timer
    
        Dimension infoPanelSize = new Dimension(500, 650); //set the size of the panel
        infoPanel.setPreferredSize(infoPanelSize); //set the size of the panel
    
        return infoPanel;
    }
    
    /**
     * Returns to the main menu, resetting game state.
     * 
     */
    public static void returnToMainMenu() {
        gameState.resetBooleans(); //reset game booleans used in the game state
        wholeBoard.removeAll(); //remove the game state from the panel
        infoPanel.removeAll(); //remove the info panel from the panel
        currentLevel = null; //set the current level to null
        gameState.getEnemies().clear(); //clear the enemies
        gameState.inventory.clear(); //clear the inventory
        Player.pInventory.clear(); //clear the player inventory
        gameState.getInventory().clear();
        LevelHandler.gameTimer.stop();
        LevelHandler.infoPanelTimer.stop();

        //return to main menu logic
        Game game = Game.singleton();
        game.removeMenuBarIfNoLevelActive();
        game.getContentPane().removeAll();
        new MenuScreen();
        game.getContentPane().add(new MenuScreen()); 
        game.pack();
        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }

    /**
     * Continues the game with the specified next game level, clearing inventory and resetting game state.
     *
     * @param nextLevel The next game level to continue playing.
     */
    public static void continueGame(LHander nextLevel) {
        //clear level inventories
        gameState.resetBooleans(); //reset game booleans used in the game state
        gameState.getInventory().clear(); //clear the inventory
        gameState.getEnemies().clear(); //clear the enemies
        gameState.inventory.clear(); //clear the inventory
        Player.pInventory.clear(); //clear the player inventory
        wholeBoard.removeAll(); //remove the game state from the panel
        infoPanel.removeAll(); //remove the info panel from the panel

        //return to main menu logic
        Game game = Game.singleton();
        game.getContentPane().removeAll(); 
        game.getContentPane().add(createLevelPanel(nextLevel));
        game.pack(); 
        game.setLocationRelativeTo(null); 
        game.setVisible(true); 
    }
}
