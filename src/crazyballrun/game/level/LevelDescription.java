/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level;

import java.awt.Image;

/**
 * The LevelDescription class used for the level description shown in the 
 * GUI (pre-information of the level, like author, level name, textual and
 * visual level description, ...). 
 * @author Timm Hoffmeister
 */
public class LevelDescription {

    /**
     * Level name. 
     */
    public String mLevelName = null;

    /**
     * Name of the creator of the level.
     */
    public String mAuthor = null;

    /**
     * Image name of the load screen. 
     */
    public Image mLoadScreen = null;

    /**
     * Image name of the minimap. 
     */
    public Image [] mMiniMap = null;
    
    /**
     * Defines the number of layers of the level.
     */
    public String mLayers = null; 
    
    /**
     * Number of players.
     */
    public int mNumberPlayers = 0;
    
    /**
     * Width of the level (measurement in 'textures').
     */
    public int mWidth = 0;
    
    /**
     * Height of the level (measurement in 'textures').
     */
    public int mHeight = 0;

    /**
     * Filename of the standard background music of the level.
     */
    public String mDefaultSound = null;
    
    /**
     * Additional level description text.
     */
    public String mDescription = null;
    
}
