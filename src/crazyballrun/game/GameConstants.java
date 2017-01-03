/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

/**
 * The GameConstants class contains all available constants used in the game.
 * @author Timm Hoffmeister
 */
public class GameConstants {
    
    /**
     * The main path containing the game-configuration file.
     */
    public static final String MAIN_PATH = System.getProperty("user.dir");
    
    /**
     * Path and filename of the game configuration XML-file. 
     */
    public static final String GAME_CONFIG_FILE_PATH = MAIN_PATH + File.separator + "game.cfg";
    
    /**
     * Path to the log-file of the game.
     */
    public static final String LOG_FILE_PATH = MAIN_PATH + File.separator + "game.log";

    /**
     * Path leading to the Object-xml-files. 
     */
    public static final String OBJECT_RESOURCE_PATH = MAIN_PATH + File.separator + "resources" + File.separator + "configuration" + File.separator + "vehicles";
    
    /**
     * File extension for Object-xml-files.
     */
    public static final String OBJECT_FILE_EXTENSION = ".cfg";
    
    /**
     * Path leading to the level-files of the game.
     */
    public static final String LEVEL_RESOURCE_PATH = MAIN_PATH + File.separator + "resources" + File.separator + "levels";
    
    /**
     * File extension of the level-files. 
     */
    public static final String LEVEL_FILE_EXTENSION = ".cbm";
   
    /**
     * File extension for collision textures (just Bitmaps allowed, because
     * other file formats sometimes get wrong RGB values in Java). 
     */
    public static final String COLLISION_EXTENSION = ".col";
    
    /**
     * File extension for texture-files (have to support transparency!).
     */
    public static final String TEXTURE_FILE_EXTENSION = ".png";
    
    /**
     * File extension for texture-files (have to support transparency!).
     */
    public static final String COLLISION_FILE_EXTENSION = ".bmp";

    /**
     * File extension for image-files (have to support transparency!).
     */
    public static final String IMAGE_FILE_EXTENSION = ".png";

    /**
     * Filename of the default load-screen of levels. 
     */
    public static final String LEVEL_DEFAULT_LOADSCREEN = "load.png";

    /**
     * Filename of the default background music of levels. 
     */
    public static final String LEVEL_DEFAULT_MUSIC = null;

    /**
     * Path leading to the graphics (image files). 
     */
    public static final String GRAPHICS_RESOURCE_PATH = MAIN_PATH + File.separator + "resources" + File.separator + "graphics" + File.separator + "images";
    
    /**
     * Path leading to the texture graphics (image files). 
     */
    public static final String TEXTURE_RESOURCE_PATH = MAIN_PATH + File.separator + "resources" + File.separator + "graphics" + File.separator + "textures";
    
    /**
     * Absolute path to the sound file directory.
     */
    public static final String SOUND_RESOURCE_PATH = MAIN_PATH + File.separator + "resources" + File.separator + "sounds";    

    /**
     * Path leading to the GUI graphics (image files). 
     */
    public static final String GUI_RESOURCE_PATH = MAIN_PATH + File.separator + "resources" + File.separator + "graphics" + File.separator + "gui";

    /**
     * Path leading to the GUI configuration file (XML formate). 
     */
    public static final String GUI_CONFIGURATION_PATH = MAIN_PATH + File.separator + "resources" + File.separator + "configuration" + File.separator + "gui.cfg";
        
    /**
     * Unique string identifier of the menu screen, if no menu should be shown.
     */
    public static final String GUI_NO_FRAME_ID = "none";

    /**
     * Unique string identifier for the in-game GUI.
     */
    public static final String GUI_IN_GAME_ID = "game";
    
    /**
     * Unique string identifier for the initial game menu screen.
     */
    public static final String GUI_START_FRAME_ID = "start";

    /**
     * Unique string identifier for the "pause" menu screen.
     */
    public static final String GUI_BREAK_FRAME_ID = "break";

    /**
     * Default font used for GUI elements.
     */
    public static final Font GUI_DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    
    /**
     * Default font color used for GUI elements.
     */
    public static final Color GUI_DEFAULT_FONT_COLOR = Color.WHITE;
    
    /**
     * Size of a texture in pixels.
     */
    public static final int TEXTURE_SIZE = 128;
    
    /**
     * Number of collision points of a vehicle between its polygon-points. Do
     * NOT change this number if you're not sure what you're doing. This may 
     * have a huge influence on collision-accuracy and performance. 
     */
    public static final int INNER_COLLISION_POINTS = 8;
    
    /**
     * Number of miliseconds used for an animation to change picture. 
     */
    public static final int ANIMATION_TIMESTEP_MSEC = 40000;

    /**
     * Maximum frame-rate for the physics engine. 
     */
    public static final int MAX_FPS = 65;
}
