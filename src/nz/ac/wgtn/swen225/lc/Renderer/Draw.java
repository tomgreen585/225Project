package nz.ac.wgtn.swen225.lc.Renderer;

import nz.ac.wgtn.swen225.lc.Domain.*;

import javax.sound.sampled.*;

import javax.swing.*;
import java.awt.*;

import java.io.File;
import java.io.IOException;

/**
 * Draw gets a gamestate from domain and then returns a JPanel.
 * Cell in Domain handle the images for the cells.
 * 
 * @author Tom Wilton
 */
public class Draw {
    /**
     * This class will be given a game state from APP and will get the board and make the camera array
     * it will then return a game panel to be drawn in APP which holds the images from camera array.
     * @param gameState current game state
     * @return panel holding cell images to be drawn in app
     */
    public static JPanel makeJPanel(Domain gameState){
        int camSize = 10;
        Cell[][] board = gameState.getBoard();
        //get players position for the camera
        int playerX = gameState.getPlayer().getPlayerX();
        int playerY = gameState.getPlayer().getPlayerY();
        //calculate the starting position of the camera
        int startX = playerX - camSize/2;
        int startY = playerY - camSize/2;

        camSize = camSize + 1; //this is so the player is in the middle of screen and has 5 cells on each side of the player. making the total board size 11x11

        //create a new JPanel to hold the images
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(camSize,camSize)); // set the layout to the grid layout

        //Loop through the cameraArray and add the imgaes to the panel
        for(int i = 0 ; i < camSize ; i++) {
            for (int j = 0; j < camSize; j++) {
                int boardX = startX + i;
                int boardY = startY + j;
                if (boardX >= 0 && boardX < board.length && boardY >= 0 && boardY < board[0].length) {
                    //Check if the boardX and boardY are within the board bounds
                    Cell cell = board[boardX][boardY];
                    panel.add(cell);
                }
            }
        }
        return panel;
    }

    /**
     * Makes the whole board, returns a JPanel and takes in a Domain argument called gamestate.
     * @param GameState gives the method the ability to draw the current board.
     * @return JPanel of the board.
     */
    public static JPanel makeWholeBoardJPanel(Domain GameState){

        Cell[][] board = GameState.getBoard();      //extract 2D array of cells from game state
        int numRows = board.length;                 //get size of board
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(numRows, numRows));  //get size of board
        numRows = numRows;      //account for index 0

        //Loop through the cameraArray and add th images to the panel
        for(int i = 0 ; i < numRows; i++){
            for(int j = 0 ; j < numRows; j++) {
                Cell cell = board[i][j];        //get cell at board position
                //BufferedImage cellImage = cell.getImageTexture();
                //adds cell to the JPanel
                panel.add(cell);
            }
        }

        return panel;
    }


    /**
     * A sound parameter is given to the method. It then checks the sounds folder in the Renderer package to see if the sound is there and then plays it.
     *
     * @param soundFile the name of the file that is used excluding the .wav.
     * @throws IOException Error checking.
     * @throws UnsupportedAudioFileException Error checking.
     * @throws LineUnavailableException Error checking.
     */
    public static void playSound(String soundFile) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        File f = new File("Chaps_Challenge/src/Renderer/sounds/" + soundFile + ".wav");
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }

}







