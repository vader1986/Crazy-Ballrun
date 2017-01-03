/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game;

import java.util.HashMap;

/**
 * The GameSettings stores user defines game constants (resolution, color depth, 
 * refresh rate of the screen, etc.). 
 * @author Timm Hoffmeister
 */
public class GameSettings {

    /**
     * Hashmap containing all the settings. 
     */
    private HashMap<String, Object> mSettings = null;
    
    /**
     * Reference to the GameSettings.
     */
    private static GameSettings sInstance = null;
    
    /**
     * Access method for the GameSettings-reference (singleton-implementation).
     * @return the only existing instance of GameSettings.
     */
    public static GameSettings getInstance () {
        if (sInstance == null)
            sInstance = new GameSettings();
        return sInstance;
    }
    
    /**
     * Constructor of GameSettings.
     */
    private GameSettings(){
        mSettings = new HashMap<String, Object>();
    }
    
    /**
     * Adds a new setting to the GameSettings. 
     * @param key name of the setting
     * @param value corresponding setting value
     */
    public synchronized void addValue (String key, Object value) 
    {
        try 
        {
            if (mSettings.containsKey(key)) 
            {
                mSettings.remove(key);
            }
            mSettings.put(key, value);
        }
        catch (Exception e) 
        {
            GameLogger.log(e);
        }
    }
    
    /**
     * Removes a setting from the GameSettings. 
     * @param key name of the setting
     * @return 'true' if the key existed in the hashmap and has been deleted 
     * successfully from there.
     */
    public synchronized boolean deleteValue (String key) 
    {
        if (mSettings.containsKey(key)) 
        {
            mSettings.remove(key);
            return true;
        }
        return false;
    }
    
    /**
     * Looks up a value for a particular setting. 
     * @param key particular setting
     * @return the value of the setting-name
     */
    public synchronized Object getValue (String key){
        Object ret = null;
        try {
            if (mSettings.containsKey(key))
                ret = mSettings.get(key);
            else
                return null;
        }
        catch (Exception e) {
            GameLogger.log(e);
            return null;
        }
        return ret;
    }
    
}
