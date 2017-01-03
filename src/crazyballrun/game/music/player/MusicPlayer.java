/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.music.player;

import crazyballrun.game.GameLogger;
import java.io.FileInputStream;

/**
 * The MusicPlayer uses an external library to implement the IMusicPlayer-
 * functionality, necessary for the game.
 * @author Timm Hoffmeister
 */
public class MusicPlayer implements IMusicPlayer {
    
    /**
     * Player used to play music. 
     */
 //   AdvancedPlayer mPlayer = null;
    
    /**
     * Music file stream.
     */
    FileInputStream mStream = null;
    
    /**
     * Filestream of the music file. 
     */
    String mFilename = null;
    
    /**
     * Constructor for MusicPlayer. 
     * @param filename song file name
     */
    public MusicPlayer (String filename) {
        mFilename = filename;
    }
    
    @Override
    public void play () {
        try {
            if (mStream != null) {
                mStream.close();
            }
            mStream = new FileInputStream(mFilename);
           // mPlayer = new AdvancedPlayer(mStream);
           // mPlayer.play();
        }
        catch (Exception e) {
            GameLogger.log(e);
        }
    }

    @Override
    public void stop () {
        //mPlayer.close();
    }    

}
