/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.music;

import crazyballrun.game.GameConstants;
import crazyballrun.game.GameLogger;
import java.io.File;
import java.util.HashMap;

/**
 * MusicEngine handles all stuff for playering background music and forcing
 * sound effects (e.g. due to a button click).
 * @author Timm Hoffmeister
 */
public class MusicEngine {
    
    /**
     * Hashmap containing sound files of the reource folder for sounds. 
     * Mapping: "<Filename>" on "<Pathname>/<Filename>"
     */
    private HashMap <String, String> mSongFiles = null;
    
    /**
     * Reference to the music engine. 
     */
    private static MusicEngine engine = null;
    
    /**
     * Thread for playing songs. 
     */
    private MusicThread mMusicThread = null;

    /**
     * Thread for playing sound effects. 
     */
    private SoundThread mSoundEffectThread = null;
    
    /**
     * Background music for the current position of the main camera. 
     */
    private String [] mCurrentMusicList = null;
    
    /**
     * Music state: on/off.
     */
    private boolean mMusicState = true;
    
    /**
     * Sound state: on/off.
     */
    private boolean mSoundState = true;
    
    /**
     * Provides access to the music engine.
     * @return a reference to the MusicEngine. 
     */
    public static MusicEngine getInstance() {
        if (engine == null)
            engine = new MusicEngine();
        return engine;
    }
    
    /**
     * Private constructor for MusicEngine. 
     */
    private MusicEngine() {
        mSongFiles = new HashMap<String, String>();
    }    
    
    /**
     * Initializes the MusicEngine, e.g. by loading music files from the resource-
     * folder.
     */
    public void initialize () {
        try
        {
            // load all music files in the resources/sounds folder
            File folder = new File(GameConstants.SOUND_RESOURCE_PATH);
            String vFilePath = null;
            for (File file : folder.listFiles()) {            
                if (file.isFile())
                    vFilePath = file.getAbsolutePath();
                else
                    vFilePath = null;
                if (vFilePath != null) {
                    mSongFiles.put(file.getName(), vFilePath);
                }
            }

            // Create and start the music thread
            mMusicThread = new MusicThread();
            mSoundEffectThread = new SoundThread();
        } 
        catch (Exception e) 
        {
            GameLogger.log(e);
        }
    }

    /**
     * Starts the music engine by running the music thread.
     */
    public void start () {
        mMusicThread.start();
        mSoundEffectThread.start();
    }

    /**
     * Ends all threads the MusicEngine controls. 
     * @throws InterruptedException problems with interruptions might occur
     */
    public void end() throws InterruptedException
    {
        mMusicThread.stopPlaying();
        mMusicThread.interrupt();
        mSoundEffectThread.interrupt();
        mMusicThread.join();
        mSoundEffectThread.join();
    }
    
    /**
     * Turns music on or off.
     * @param state wished state of music
     */
    public synchronized void setMusicState (boolean state) {
        mMusicState = state;
        if (!state) 
            stopMusic();
        else {
            if (mCurrentMusicList != null)
                playMusicFile(mCurrentMusicList, true, true);
        }
    }
    
    /**
     * Returns the state of music (on or off).
     * @return the state of music (on/off).
     */
    public synchronized boolean getMusicState () {
        return mMusicState;
    }

    /**
     * Turns sound on or off.
     * @param state new sound state (on/off)
     */
    public synchronized void setSoundState (boolean state) {
        mSoundState = state;
        if (!state) mSoundEffectThread.clearFX();
    }
    
    /**
     * Gets the current state of the sound (on or off).
     * @return sound state (on/off).
     */
    public synchronized boolean getSoundState () {
        return mSoundState;
    }
    
    /**
     * Gets the absolute path of a music file from the resource-folder for the 
     * sounds. 
     * @param filename filename of the song
     * @return the absolute path of the music file.
     */
    public String getMusic (String filename) {
        if (filename == null)
            return null;
        return mSongFiles.get(filename);
    }

    /**
     * Plays a sound effect. 
     * @param name filename of the sound effect
     */
    public void playSound (String name) {
        if (!getSoundState() || name == null) return;
        try {
            mSoundEffectThread.addSoundEffect(getMusic(name));
        } catch (Exception e) {
            GameLogger.log(e);
        }
    }
    
    /**
     * Finds out if a playlist equals the currently played playlist of the
     * MusicEngine.
     * @param playlist reference to the new playlist
     * @return "true" if the playlist equals the current playlist
     */
    private boolean isPlayed(String [] playlist) {
        if (mCurrentMusicList == playlist)
            return true;
        if (playlist == null || mCurrentMusicList == null || playlist.length != mCurrentMusicList.length)
            return false;
        for (int i = 0; i < playlist.length; i++)
            if (!playlist[i].equals(mCurrentMusicList[i]))
                return false;
        return true;
    }

    /**
     * Plays a song, optionally looped.  
     * @param name absolute path + filename of the music file
     * @param loop play the song in a loop
     * @param wait wait for the current song before starting the song
     */
    public void playMusicFile (String name, boolean loop, boolean wait) 
    {
        if (name == null) return;
        String [] vFile = new String[1];
        vFile[0] = name;
        playMusicFile(vFile, loop, wait);
    }
    
    /**
     * Plays one or several songs, optionally looped.  
     * @param names absolute path + filename of the music files
     * @param loop play the song in a loop
     * @param wait wait for the current song before starting the song
     */
    public void playMusicFile (String [] names, boolean loop, boolean wait) 
    {
        if (isPlayed(names) || names == null) return;
        mCurrentMusicList = names;
        if (!getMusicState()) return;
        try {
            mMusicThread.startPlaying(names, loop, wait);
        } catch (Exception e) {
            GameLogger.log(e);
        }
    }
    
    /**
     * Plays a song once.  
     * @param name string identifier of the music file
     */
    public void playMusic (String name) {
        playMusic(name, false, false);
    }
    
    /**
     * Plays a song, optionally looped.  
     * @param name string identifier of the music file
     * @param loop play the song in a loop
     * @param wait wait for the current song before starting the song
     */
    public void playMusic (String name, boolean loop, boolean wait) {
        if (name == null) return;
        String [] vNames = new String[1];
        vNames[0] = name;
        playMusic(vNames, loop, wait);
    }

    /**
     * Plays a list of songs, optionally in a loop.
     * @param names song filenames
     * @param loop play songs in a loop
     * @param wait wait for the current song before starting the new playlist
     */
    public void playMusic (String [] names, boolean loop, boolean wait) {
        if (names == null) return;
        String [] vNames = new String[names.length];
        for (int i = 0; i < vNames.length; i++)
            vNames[i] = getMusic(names[i]);
        if (isPlayed(vNames)) return;
        mCurrentMusicList = vNames;
        if (!getMusicState()) return;
        try {
            mMusicThread.startPlaying(vNames, loop, wait);
        } catch (Exception e) {
            GameLogger.log(e);
        }
    }
    
    /**
     * Stops playing the current song.
     */
    public void stopMusic () {
        mMusicThread.stopPlaying();
    }
    
}
