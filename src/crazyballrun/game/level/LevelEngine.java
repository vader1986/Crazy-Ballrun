/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level;

import crazyballrun.game.level.controllers.Player;
import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.level.objects.LevelObjectParser;
import crazyballrun.game.Game;
import crazyballrun.game.GameConstants;
import crazyballrun.game.GameFunctions;
import crazyballrun.game.GameLogger;
import crazyballrun.game.graphics.GraphicsEngine;
import crazyballrun.game.gui.GUIEngine;
import crazyballrun.game.physics.PhysicsEngine;
import java.io.File;
import java.util.HashMap;

/**
 * The LevelEngine undertakes the task to pre-load all available level-descriptions
 * when the game starts. Later, when the game-settings have been specified via
 * GUI (GUIEngine), the LevelEngine can load whole levels.
 * @author Timm Hoffmeister
 */
public class LevelEngine {

    /**
     * Reference to the graphics engine. 
     */
    private static LevelEngine engine = null;    
    
    /**
     * Hashmap containing all level objects. 
     */
    private HashMap <String, LevelObject> mLevelObjects = null;
    
    /**
     * Hashmap containing all [filename, leveldescription]-pairs. 
     */
    private HashMap <String, LevelDescription> mLevelDescriptions = null;
    
    /**
     * Reference to the level parser.
     */
    private LevelParser mLevelParser = new LevelParser();

    /**
     * Reference to the object parser. 
     */
    private LevelObjectParser mObjectParser = new LevelObjectParser();
    
    /**
     * Currently loaded level.
     */
    private Level mCurrentLevel = null;
    
    /**
     * Provides access to the level engine.
     * @return a reference to the LevelEngine. 
     */
    public static LevelEngine getInstance() {
        if (engine == null)
            engine = new LevelEngine();
        return engine;
    }    
    
    /**
     * Private constructor for LevelEngine. 
     */
    private LevelEngine() {
        mLevelDescriptions = new HashMap<String, LevelDescription>();
        mLevelObjects = new HashMap<String, LevelObject>();
    }

    /**
     * Gets the level description for a particular level.
     * @param levelname name of the level
     * @return a reference to the level description.
     */
    public LevelDescription getLevelDescription(String levelname) {
        if (mLevelDescriptions.containsKey(levelname + GameConstants.LEVEL_FILE_EXTENSION))
            return mLevelDescriptions.get(levelname + GameConstants.LEVEL_FILE_EXTENSION);
        else
            return null;
    }
    
    /**
     * Initializes the LevelEngine by loading all avaiable level descriptions and
     * objects of the global resource-directory.
     */
    public void initialize() {
        try {
            
            // Read Level-Descriptions
            LevelDescriptionParser vParser = new LevelDescriptionParser();
            File f = new File(GameConstants.LEVEL_RESOURCE_PATH);
            for (File vFile : f.listFiles()) {
                if (vFile.isFile()) {
                    String vName = vFile.getName();
                    if (vName.substring(vName.lastIndexOf(".")).equals(GameConstants.LEVEL_FILE_EXTENSION)) 
                    {
                        LevelDescription vDescription = new LevelDescription();
                        vParser.parse(GameConstants.LEVEL_RESOURCE_PATH + File.separator + vName, vDescription);
                        mLevelDescriptions.put(vName, vDescription);
                    }
                }
            }
        }
        catch (Exception e) {
            GameLogger.log(e);
        }        
    }

    /**
     * Gets a reference to the current level.
     * @return current level
     */
    public synchronized Level getCurrentLevel() {
        return mCurrentLevel;
    }
    
    /**
     * Initializes some GameFunctions-variables with level-specific data.
     */
    private void setGameFunctions () 
    {
        // initialize static variables
        int size = mCurrentLevel.getPlayers().size();
        GameFunctions.sPlayer = new Player[size];
        GameFunctions.sPlayerNumber = new int[size];
        GameFunctions.sPlayerAccBackwards = new boolean[size];
        GameFunctions.sPlayerAccelerate = new boolean[size];
        GameFunctions.sPlayerTurnLeft = new boolean[size];
        GameFunctions.sPlayerTurnRight = new boolean[size];

        // put data into static variables
        for (int i = 0; i < size; i++)
        {
            GameFunctions.sPlayer[i] = mCurrentLevel.getPlayers().get(i);
            GameFunctions.sPlayerAccBackwards[i] = false;
            GameFunctions.sPlayerAccelerate[i] = false;
            GameFunctions.sPlayerTurnLeft[i] = false;
            GameFunctions.sPlayerTurnRight[i] = false;
            // TODO: this should have been set in the GUI => read GameSettings.getValue("Player<i>")
            GameFunctions.sPlayerNumber[i] = i;
        }
    }
    
    /**
     * Loads the specified level and starts the game.
     * @param name level name (from level description)
     */
    public synchronized void loadLevel (String name) {
        String vFileName = GameConstants.LEVEL_RESOURCE_PATH + File.separator + name + GameConstants.LEVEL_FILE_EXTENSION;
        Level vLevel = new Level();
        try 
        {
            mLevelParser.parse(vFileName, vLevel);
        }
        catch (Exception e)
        {
            GameLogger.log(e);
            System.exit(1);
        }
        mCurrentLevel = vLevel;

        // Sets level-specific GameFunctions-variables
        setGameFunctions();
        
        // give level-reference to RenderThread and PhysicsThread
        GraphicsEngine.getInstance().setLevel(vLevel);
        PhysicsEngine.getInstance().setLevel(vLevel);
        
        // create camera-displays
        GUIEngine.getInstance().createCameraDisplays(vLevel);

        // Change game state to In-Game-Paused (for ample)
        Game.getInstance().setGameState(Game.GameState.IN_GAME_PAUSED);
        
        try
        {
            GraphicsEngine.getInstance().setAmpleState(0);
            Thread.sleep(1100);
            GraphicsEngine.getInstance().setAmpleState(1);
            Thread.sleep(300);
            GraphicsEngine.getInstance().setAmpleState(2);
            Thread.sleep(1100);
            GraphicsEngine.getInstance().setAmpleState(3);
            Thread.sleep(300);
            GraphicsEngine.getInstance().setAmpleState(4);
            Thread.sleep(1100);
            GraphicsEngine.getInstance().setAmpleState(-1);
        }
        catch (InterruptedException e)
        {
            GameLogger.log(e);
        }

        // Change game state to In-Game
        mCurrentLevel.getStatistics().resume();
        Game.getInstance().setGameState(Game.GameState.IN_GAME);
    }
    
    /**
     * Loads an object from the global object-directory. 
     * @param name object name
     * @return reference to the object (or 'null' if it doesn't exist)
     */
    public LevelObject loadObject (String name) 
    {
        if (mLevelObjects.containsKey(name))
        {
            return mLevelObjects.get(name).clone();
        }

        LevelObject vObject = null;
        
        try 
        {
            vObject = mObjectParser.parse(GameConstants.OBJECT_RESOURCE_PATH + File.separator + name + GameConstants.OBJECT_FILE_EXTENSION);
        }
        catch (Exception e) {
            vObject = null;
        }             
        
        if (vObject != null)
        {
            if (!mLevelObjects.containsKey(name))
            {
                mLevelObjects.put(name, vObject);
            }
        }
        
        return vObject;
    }
    
    /**
     * Loads an object for a particular level. 
     * @param name name of the level object
     * @param level name of the level
     * @return reference to the object (or 'null' if it doesn't exist)
     */
    public LevelObject loadObject (String name, String level) 
    {
        if (mLevelObjects.containsKey(name))
        {
            return mLevelObjects.get(name).clone();
        }

        LevelObject vReturn = null;

        try 
        {
            vReturn = mObjectParser.parse(GameConstants.LEVEL_RESOURCE_PATH + File.separator +
                                          level + File.separator + name + GameConstants.OBJECT_FILE_EXTENSION);
        }
        catch (Exception e) {
            vReturn = null;
        }             

        if (vReturn == null)
            vReturn = loadObject(name);
        
        if (vReturn != null)
        {
            if (!mLevelObjects.containsKey(name))
            {
                mLevelObjects.put(name, vReturn);
            }
        }
        
        return vReturn;
    }
    
}




