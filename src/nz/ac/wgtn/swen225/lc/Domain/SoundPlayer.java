package nz.ac.wgtn.swen225.lc.Domain;

import java.io.File;
import java.io.InputStream;

import javax.sound.sampled.*;
import javax.swing.JDialog;

/**
 * Soundplayer
 * This is a simple class for playing sounds stored in the .wav format
 *
 * @author Judah Dabora
 */
public class SoundPlayer implements LineListener {
    Clip clip;

    /**
     * playSound
     * This static method plays one of the game sounds.
     *
     * @param sound a string id defining one of the sound files to play
     */
    public static synchronized void playSound(final String sound) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                SoundPlayer s = null;
                try {
                    switch (sound) {
                        case "footstep" -> s = new SoundPlayer("res/sounds/footstep.wav");
                        case "level_finish" -> s = new SoundPlayer("res/sounds/level_finish.wav");
                        case "die" -> s = new SoundPlayer("res/sounds/die.wav");
                        case "key" -> s = new SoundPlayer("res/sounds/item.wav");
                        case "treasure" -> s = new SoundPlayer("res/sounds/treasure.wav");
                        case "question" -> s = new SoundPlayer("res/sounds/questionBlock.wav");
                        case "unlock" -> s = new SoundPlayer("res/sounds/unlock.wav");
                    }
                } catch (Exception e) {
                    System.out.println("sound player error");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * SoundPlayer constructor
     *
     * @param filePath the relative or absolute path of the sound file to play
     * @throws Exception If the input stream does not find a file, this method
     *                   will throw an exception.
     */
    public SoundPlayer(String filePath) throws Exception {

        InputStream input = getClass().getResourceAsStream(filePath);
        //System.out.println("Playing " + filePath);

        Line.Info linfo = new Line.Info(Clip.class);
        Line line = AudioSystem.getLine(linfo);
        clip = (Clip) line;
        clip.addLineListener(this);
        AudioInputStream ais = AudioSystem.getAudioInputStream(input);

        clip.open(ais);

        clip.start();
    }

    /**
     * update
     * This updates the line listener for the audio stream.
     *
     * @param le a line event that describes the change
     */
    public void update(LineEvent le) {
        LineEvent.Type type = le.getType();
        if (type == LineEvent.Type.OPEN) {
            //System.out.println("OPEN");
        } else if (type == LineEvent.Type.CLOSE) {
            //System.out.println("CLOSE");
        } else if (type == LineEvent.Type.START) {
            //System.out.println("START");
        } else if (type == LineEvent.Type.STOP) {
            //System.out.println("STOP");
            clip.close();
        }
    }
}