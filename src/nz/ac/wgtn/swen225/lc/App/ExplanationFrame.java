package nz.ac.wgtn.swen225.lc.App;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * A frame that displays instructions on how to play the Chaps Challenge game.
 * Shows the objectives of the game.
 * The keys for the game and how to use the recorder.
 * 
 * @author greenthom.
 */
public class ExplanationFrame extends JFrame {

    /**
     * Constructs an ExplanationFrame to display instructions on how to play the game.
     * 
     */
    public ExplanationFrame() {
        
        setTitle("How To Play");
        setSize(600, 730); //set the size of the frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null); 

        //create a JTextArea
        JTextArea explanationTextArea = new JTextArea(); 
        explanationTextArea.setEditable(false); 
        explanationTextArea.setLineWrap(true); 
        explanationTextArea.setWrapStyleWord(true); 
        explanationTextArea.setBackground(Color.YELLOW);

        //explanation text
        String spaceText = "                                                ";
        String explanationText = "Welcome to the Chaps Challenge! \n\n";
        String objectivesText = " Here are the Objectives of the Game: \n\n";
        String treasureText = "             Collect all the Treasures to Complete the Level. \n\n";
        String keysText = "             Collect the Keys to Unlock the Doors. \n\n";
        String timeLimit = "             Complete the Level Within the Time Limit. \n\n";
        String keyForGameText = " Here are the Keys for the Game: \n\n";
        String movementText = "             You can move Chaps Using the Arrow Keys. \n\n";
        String pauseText = "             To Pause the Game Press the SPACEBAR. \n\n";
        String helperText = "             To Use the Helper Press CTRL-H. \n\n";
        String loadText = "             To Load a Game Press CNTRL-R. \n\n"; 
        String saveText = "             To Save and Exit a Game Press CNTRL-S. \n\n";
        String quickLoadText = "             To Quickly Load a Level Press CNTRL-1/2. \n\n";
        String recordingText = "             To Start Recording a Game Go to Menu Bar. \n\n"; 
        String restartText = "             To Resume the Game Press ESC. \n\n";
        String exitText = "             To Exit the Application Press CNTRL-X. \n\n";
        String recordingStuff = " Recorder: \n\n";
        String rOneText = "             To Start Recording a Game Go to Menu Bar. \n\n";
        String rTwoText = "             To Load a Recording go back to the Menu Screen. \n\n";
        String rThreeText = "             You can Select your Saved Recording. \n\n";
        String rFourText = "             You can select if you want it to Autoplay or Step-By-Step. \n\n";
        String rFiveText = "             For Auto Replay use the buttons to increase/slow down the speed. \n\n";
        String rSixText = "             For Step-By-Step use the buttons to display next/previous move. \n\n";

        // Set the text in the JTextArea
        explanationTextArea.setText(spaceText + explanationText + objectivesText + treasureText + keysText + timeLimit 
        + keyForGameText + movementText + pauseText + restartText + helperText + loadText + quickLoadText + 
        saveText + recordingText + exitText + recordingStuff + rOneText + rTwoText + rThreeText + rFourText + rFiveText + rSixText); 

        //add the JTextArea to the frame
        getContentPane().add(explanationTextArea, BorderLayout.CENTER);
    }
}
