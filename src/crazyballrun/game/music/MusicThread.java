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
 * The music thread is used to play background music. You can add a list of 
 * InputFileStreams which are supposed to be played by this thread.
 * @author Timm Hoffmeister
 */
public class MusicThread extends Thread {

    /**
     * music-player used to play the current music-file.
     */
    private IMusicPlayer mMp3Player = null;

    /**
     * This songlist contains players for each song. 
     */
    private LinkedList <IMusicPlayer> mSongs = null;
    
    /**
     * Music thread plays songs in a loop or not. 
     */
    private boolean mLoopMode = false;
   
    /**
     * Music thread should stop playing. 
     */
    private boolean mStopped = false;
    
    /**
     * Constructor of MusicPlayer.
     */
    public MusicThread () {
        mSongs = new LinkedList<IMusicPlayer>();
    }
    
    /**
     * Plays the given music-files, optionally in a loop.
     * @param songs reference to the FileInputStream-Song-List
     * @param loop if the songs should be played in a loop
     * @param wait waits for the current song to stop before starting the next 
     * playlist
     */
    public void startPlaying(String [] songs, boolean loop, boolean wait) {
        IMusicPlayer [] vPlayer = new IMusicPlayer[songs.length];
        if (mMp3Player != null) {
            setLoopMode(false);
            setStopMode(true);
            if (!wait)
                mMp3Player.stop();
        }
        try {
            for (int i = 0; i < songs.length; i++) {
                vPlayer[i] = new MusicPlayer(songs[i]);
            }
            addSongs(vPlayer);
            setLoopMode(loop);
            setStopMode(false);
        } catch (Exception ex) {
            GameLogger.log(ex);
        }
    }
    
    /**
     * Gets the next song for playing.
     * @return a reference to the song-player of the next song in the queue. 
     */
    private synchronized IMusicPlayer getNextSong () {
        if (mSongs.size() > 0) {
            return mSongs.removeFirst();
        } else {
            return null;
        }
    }
    
    /**
     * Sets the "mStopped" member variable.
     * @param stop 'true' if the music thread should stop playing
     */
    private synchronized void setStopMode (boolean stop) {
        mStopped = stop;
    }
    
    /**
     * Gets if the music thread should stop playing currently. 
     * @return the value of the "mStopped"-member variable.
     */
    private synchronized boolean getStopMode () {
        return mStopped;
    }
    
    /**
     * Clears the list and adds a list of songs to the songlist.
     * @param players 
     */
    private synchronized void addSongs (IMusicPlayer [] players) {
        mSongs.clear();
        for (int i = 0; i < players.length; i++)
            mSongs.addLast(players[i]);
    }
    
    /**
     * Adds a song to the song list.
     * @param player song-player
     */
    private synchronized void addSong (IMusicPlayer player) {
        mSongs.addLast(player);
    }
    
    /**
     * Sets the loop-mode.
     * @param loop songs are played in a loop
     */
    private synchronized void setLoopMode (boolean loop) {
        mLoopMode = loop;
    }

    /**
     * Gets the loop mode.
     * @return the loop mode.
     */
    private synchronized boolean getLoopMode() {
        return mLoopMode;
    }
    
    /**
     * Stops playing the current music-file. 
     */
    public void stopPlaying() {
        setStopMode(true);
        if (mMp3Player == null) return;
        mMp3Player.stop();
    }

    /**
     * Continues playing the current playlist.
     */
    public void continuePlaying () {
        setStopMode(false);
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                if (!getStopMode()) {
                    mMp3Player = getNextSong();
                    if (mMp3Player != null) {
                        mMp3Player.play();
                        if (getLoopMode()) {
                            addSong(mMp3Player);
                        }
                    }
                    else
                    {
                        sleep(200);
                    }
                }
                else 
                {
                    sleep(200);
                }
            }
        } catch (InterruptedException e) {
            // end thread on interrupt
        } catch (Exception e) {
            GameLogger.log(e);
        }

    }
}
