/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.music;

import crazyballrun.game.GameLogger;
import crazyballrun.game.music.player.IMusicPlayer;
import crazyballrun.game.music.player.MusicPlayer;
import java.util.LinkedList;

/**
 * The MusicFXThread plays incoming sound effects. 
 * @author Timm Hoffmeister
 */
public class SoundThread extends Thread {

    /**
     * Queue containing all sound effects which should be played. 
     */
    private LinkedList <IMusicPlayer> mSoundEffectQueue = null;

    /**
     * Constructor for MusicFXThread.
     */
    public SoundThread () {
        mSoundEffectQueue = new LinkedList<IMusicPlayer>();
    }
    
    /**
     * Adds a new sound effect to the play queue.
     * @param fx file input stream of the sound
     */
    public void addSoundEffect (String fx) {
        try {
            IMusicPlayer player = new MusicPlayer(fx);
            addFX(player);
        } catch (Exception ex) {
            GameLogger.log(ex);
        }
    }
    
    /**
     * Clears the sound effect queue.
     */
    public synchronized void clearFX () {
        mSoundEffectQueue.clear();
    }
    
    /**
     * Adds a new sound effect to the queue.
     * @param fx reference to the sound effect's player
     */
    private synchronized void addFX (IMusicPlayer fx) {
        mSoundEffectQueue.addLast(fx);
    }

    /**
     * Removes and returns the next sound effect to be played.
     * @return a reference to the next sound effect's player. 
     */
    private synchronized IMusicPlayer getNextFX () {
        if (mSoundEffectQueue.size() > 0)
            return mSoundEffectQueue.removeFirst();
        else
            return null;
    }
    
    @Override
    public void run() {
        IMusicPlayer vFX = null;
        try {
            while (true) {
                vFX = getNextFX();
                if (vFX != null) {
                    vFX.play();
                } else {
                    sleep(100);
                }
            }        
        } catch (InterruptedException e) {
            // end thread on interrupt
        } catch (Exception e) {
            GameLogger.log(e);
        }
    }
    
    
}
