package nz.ac.wgtn.swen225.lc.App;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import nz.ac.wgtn.swen225.lc.Domain.Domain;
import nz.ac.wgtn.swen225.lc.Recorder.Recorder;
import nz.ac.wgtn.swen225.lc.Renderer.Draw;
import org.json.JSONObject;

/**
 * The ReplayGame class represents the panel for replaying a game using recorder.
 * 
 * @author greenthom
 */
public class ReplayGame extends JPanel {
    private static Image bImg; //background image
    private static JPanel levelPanel; //level panel to hold board and info panel
    private static JPanel wholeBoard; //board panel
    private static JPanel infoPanel; //info panel
    static JButton nextMoveButton; //next move button for step-by-step
    static JButton previousMoveButton; //previous move button for step-by-step
    static JButton increaseSpeedButton; //increase speed button for auto-replay
    static JButton decreaseSpeedButton; //decrease speed button for auto-replay
    private KeyHandler keyHandler; //key handler for the frames
    private static Domain gameState; //current domain gamestate
    private static LHander currentLevel; //current level
    private static int currentMove = 0; //current move in the replay
    private static boolean isStepByStepMode = false; //if the replay is in step-by-step mode
    private static boolean isAutoReplay = false; //if the replay is in auto-replay mode
    
    /**
     * Creates the level panel for replaying the game.
     *
     * @param gameHistory A list of JSON objects representing the game's history.
     * @param level       The current game level.
     * @param movementIncrease The number of movements to skip between each replay step.
     * @return The panel for replaying the game.
     */
    public static JPanel createLevelPanel(ArrayList<JSONObject> gameHistory, LHander level, int movementIncrease) {
        
        levelPanel = new JPanel(new BorderLayout());
        currentLevel = level;

        int moveAmount = gameHistory.size(); //the amount of moves in the game

        //if the current move is less than the move amount
        if (currentMove < moveAmount) {

            //send the game history to the recorder to get the game state
            Domain gameState = Recorder.sendGame(gameHistory, movementIncrease);

            //create the board and info panel
            wholeBoard = Draw.makeWholeBoardJPanel(gameState);
            infoPanel = createInfoPanel(gameState, movementIncrease);

            //add the board and info panel to the level panel
            Dimension panelSize = new Dimension(1200, 700);
            levelPanel.setPreferredSize(panelSize);

            levelPanel.add(wholeBoard, BorderLayout.CENTER);
            levelPanel.add(infoPanel, BorderLayout.EAST);

            //load the background image
            bImg = loadImage("/nz/ac/wgtn/swen225/lc/App/AppImages/levelGameBackground.png");

        }

        return levelPanel;
    }

    /**
     * Constructs a ReplayGame panel.
     *
     * @param currentGameState The current game state to replay.
     */
    public ReplayGame(Domain currentGameState) {
        keyHandler = new KeyHandler();
        addKeyListener(keyHandler);
        setFocusable(true);
        requestFocusInWindow();
        gameState = currentGameState;
    }

    /**
     * Gets the current game level.
     *
     * @return The current game level.
     */
    public static LHander getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Getter for the current game domain.
     *
     * @return The current game domain.
     */
    protected static Domain getDomain() {
        return gameState;
    }

    /**
     * Setter the current game domain.
     *
     * @param domain The game domain to set.
     */
    protected static void setDomain(Domain domain) {
        gameState = domain;
    }

    /**
     * Sets the replay mode to step-by-step or continuous.
     *
     * @param isStepByStepMode `true` for step-by-step mode, `false` for continuous replay mode.
     */
    public void setStepByStepMode(boolean isStepByStepMode) {
        ReplayGame.isStepByStepMode = isStepByStepMode;
    }

    /**
     * Sets the replay mode to auto-replay or manual.
     *
     * @param isAutoReplay `true` for auto-replay mode, `false` for manual replay mode.
     */
    public void setAutoReplay(boolean isAutoReplay){
        ReplayGame.isAutoReplay = isAutoReplay;
    }

    /**
     * Loads an image resource.
     *
     * @param imageName The name of the image resource.
     * @return The loaded image.
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
     * Creates an information panel displaying game status and control buttons.
     *
     * @param domain The current game domain.
     * @param movementIncrease The number of movements to skip between each replay step.
     * @return The information panel.
     */
    private static JPanel createInfoPanel(Domain domain, int movementIncrease) {
        JPanel infoPanel = new JPanel(new BorderLayout()); // Use BorderLayout

        JPanel drawingPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bImg != null) {
                    g.drawImage(bImg, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(Color.RED);
                    g.fillRect(100, 70, 300, 100);
                    g.setFont(new Font("Arial", Font.BOLD, 14));
                    g.setColor(Color.WHITE);
                    String currentlyDrawing = currentLevel.currentName();
                    g.drawString(currentlyDrawing, 120, 100);
                    repaint();
                }
            }
        };

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.BLUE);

        if (isStepByStepMode) {

            //next move button for step by step mode
            nextMoveButton = new JButton("Next Move");
            nextMoveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //increase the current move
                    currentMove++;
                    
                    //redraw the board with the next move
                    ArrayList<JSONObject> gameHistory = MenuScreen.getGameHistory();
                    Domain gameState = Recorder.sendGame(gameHistory, currentMove);
                    levelPanel.remove(wholeBoard);
                    wholeBoard = Draw.makeWholeBoardJPanel(gameState);
                    levelPanel.add(wholeBoard, BorderLayout.CENTER);
                    levelPanel.revalidate();
                    levelPanel.repaint();

                    if (currentMove >= gameHistory.size() - 1) {
                        nextMoveButton.setEnabled(false);
                    }

                    previousMoveButton.setEnabled(true);
                }
            });

            //previous move button for step by step mode
            previousMoveButton = new JButton("Previous Move");
            previousMoveButton.setEnabled(false); 
            previousMoveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //decrease the current move
                    currentMove--;
                    
                    //redraw the board with the previous move
                    ArrayList<JSONObject> gameHistory = MenuScreen.getGameHistory();
                    Domain gameState = Recorder.sendGame(gameHistory, currentMove);
                    levelPanel.remove(wholeBoard);
                    wholeBoard = Draw.makeWholeBoardJPanel(gameState);
                    levelPanel.add(wholeBoard, BorderLayout.CENTER);
                    levelPanel.revalidate();
                    levelPanel.repaint();

                    nextMoveButton.setEnabled(true);

                    if (currentMove == 0) {
                        previousMoveButton.setEnabled(false);
                    }
                }
            });

            //add the buttons to the button panel
            buttonPanel.add(previousMoveButton);
            buttonPanel.add(nextMoveButton);
        }

        else if(isAutoReplay){
            
            //increase speed button for auto replay mode
            increaseSpeedButton = new JButton("Increase Speed");
            increaseSpeedButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MenuScreen.increaseTimerSpeed();
                }
            });

            //decrease speed button for auto replay mode
            decreaseSpeedButton = new JButton("Decrease Speed");
            decreaseSpeedButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MenuScreen.decreaseTimerSpeed();
                }
            });

            //add the buttons to the button panel
            buttonPanel.add(increaseSpeedButton);
            buttonPanel.add(decreaseSpeedButton);
        }

        //return to menu button
        JButton returnToMenuButton = new JButton("Return Menu");
        returnToMenuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(isAutoReplay){
                    MenuScreen.stopTimer();
                    returnToMainMenu();
                }
                else if(isStepByStepMode){
                    returnToMainMenu();
                }
            }
        });

        //add the button to the button panel
        buttonPanel.add(returnToMenuButton);
        
        //add the panels to the info panel
        infoPanel.add(drawingPanel, BorderLayout.CENTER);
        infoPanel.add(buttonPanel, BorderLayout.SOUTH);

        //set the size of the info panel
        Dimension infoPanelSize = new Dimension(500, 650);
        infoPanel.setPreferredSize(infoPanelSize);

        return infoPanel;
    }

    /**
     * Sets the enabled state of speed control buttons.
     *
     * @param enabled `true` to enable the buttons, `false` to disable them.
     */
    public static void setSpeedButtonsEnabled(boolean enabled) {
        if (increaseSpeedButton != null && decreaseSpeedButton != null) {
            increaseSpeedButton.setEnabled(enabled);
            decreaseSpeedButton.setEnabled(enabled);
        }
    }
    
    /**
     * Returns to the main menu screen.
     * Resets the game state.
     * 
     */
    private static void returnToMainMenu() {
        //reset the game state
        gameState.resetBooleans(); //reset the booleans
        wholeBoard.removeAll(); //remove the board
        infoPanel.removeAll(); //remove the info panel
        //redraw the menu screen
        Game game = Game.singleton();
        game.getContentPane().removeAll();
        new MenuScreen();
        game.getContentPane().add(new MenuScreen()); 
        game.pack();
        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }
}
