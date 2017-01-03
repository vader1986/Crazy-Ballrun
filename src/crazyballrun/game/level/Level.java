/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level;

import crazyballrun.game.level.controllers.Player;
import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.physics.CollisionTexture.GroundProperty;
import crazyballrun.game.utilities.Vector2D;
import java.util.ArrayList;
import java.util.LinkedList;

// TODO: Create player-specific checkpoints (if a player reached each checkpoint, 
//       the number of rounds is increased by one in the level-statistics).

/**
 * The Level class contains all necessary information for playing on a map. This
 * includes all textures, texture-collision maps and objects of the level.
 * 
 * @author Timm Hoffmeister
 */
public class Level {

    /**
     * Reference to the 3d-map: x, y and layer.
     */
    private Tile [][][] mMap = null;

    /**
     * Name of the level.
     */
    private String mName = null;
    
    /**
     * Width and height of the level (in textures).
     */
    private int mWidth, mHeight;
    
    /**
     * Number of layers.
     */
    private int mLayers;
    
    /**
     * Reference to the level description.
     */
    private LevelDescription mDescription = null;
    
    /**
     * List of all players contained in the level (several players if split-
     * screen mode has been activated, otherwise a single camera). The camera(s)
     * are placed when loading the "Players" through the LevelParser. The game
     * mode (occationally split-screen) is specified in the GameSettings. 
     */
    private ArrayList<Player> mPlayers = new ArrayList<Player>();
    
    /**
     * List of all objects in the level.
     */
    private LinkedList<LevelObject> mObjects = new LinkedList<LevelObject>();
    
    /**
     * Player-specific Level-Statistics.
     */
    private Statistics mStatistics = new Statistics();

    /**
     * Gets a reference to the player-specific statistics of the level. 
     * @return reference to the statistics
     */
    public Statistics getStatistics ()
    {
        return mStatistics;
    }
    
    /**
     * Creates an instance of Level.
     */
    public Level () {
        
    }
    
    /**
     * Initializes the level accoding to its name and description. 
     * @param name level name
     */
    public void create (String name) {
        mName = name;
        mDescription = LevelEngine.getInstance().getLevelDescription(name);
        createMap(mDescription.mWidth, mDescription.mHeight, Integer.parseInt(mDescription.mLayers));
    }
    
    /**
     * Creates an object and puts it into the level-object-list. 
     * @param obj filename of the object (without file extension)
     * @param pos position of the object
     * @param layer layer of the object
     * @param rotation angle of rotation
     * @return reference to the new object
     */
    public synchronized LevelObject createObject (String obj, Vector2D pos, int layer, double rotation) {
        LevelObject vObject = LevelEngine.getInstance().loadObject(obj, mName);
        vObject.setLevel(this);
        vObject.setPosition(pos, layer);
        vObject.setRotation(rotation);
        vObject.initialize();
        mObjects.add(vObject);
        return vObject;
    } 

    /**
     * Removes a (dead/destroyed) object from the level's object-list.
     * @param obj reference to the object
     */
    public synchronized void removeObject (LevelObject obj) {
        mObjects.remove(obj);
    }

    /**
     * Clones the level's object-list threadsavely.
     * @return reference to a copy of the level's object-list
     */
    public synchronized LinkedList<LevelObject> getObjectList () {
        return (LinkedList<LevelObject>) mObjects.clone();
    }
    
    /**
     * Sets a particular Tile.
     * @param tile reference to the Tile
     * @param x position of the tile (in textures)
     * @param y position of the tile (in textures)
     * @param layer layer of the tile
     */
    public void createLevelTile (Tile tile, int x, int y, int layer) {
        mMap[x][y][layer] = tile;
    }
    
    /**
     * Generates a new level map of the given width, height and a particular 
     * number of "layers". 
     * @param width width of the map
     * @param height height of the map
     * @param layers number of layers
     */
    public void createMap (int width, int height, int layers) {
        mWidth = width;
        mHeight = height;
        mLayers = layers;
        mMap = new Tile[width][height][layers];
    }

    /**
     * Creates a player for the level. 
     * @param vehicle name of the vehicle of the player
     * @param pos position of the vehicle
     * @param layer layer of the vehicle
     * @param rotation angle of rotation
     * @return reference to created player
     */
    public Player createPlayer (String vehicle, Vector2D pos, int layer, double rotation) {
        LevelObject vVehicle = null;
        
        // Load vehicle
        if (vehicle != null)
            vVehicle = createObject(vehicle, pos, layer, rotation);

        // Add player to list (occationally the player chooses its vehicle later)
        Player vPlayer = null;
        if (vVehicle != null)
            vPlayer = new Player(vVehicle, pos, layer, mPlayers.size());
        else
            vPlayer = new Player(pos, layer, mPlayers.size());
        mPlayers.add(vPlayer);
        return vPlayer;
    }

    /**
     * Gets the name of the level.
     * @return level name
     */
    public String getName () {
        return mName;
    }
    
    /**
     * Level width.
     * @return level's width (in textures)
     */
    public int getWidth () {
        return mWidth;
    }

    /**
     * Level height.
     * @return level's height (in textures)
     */
    public int getHeight () {
        return mHeight;
    }

    /**
     * Number of layers.
     * @return number of layers
     */
    public int getLayers () {
        return mLayers;
    }

    /**
     * Finds one "tile" of the level at a given position on the given layer. 
     * @param pos position of the tile
     * @param layer layer (z-coordinate) of the tile
     * @return a reference to the Tile
     */
    public Tile getTile (Vector2D pos, int layer) {
        if (pos.x < 0 || (int)pos.x >= mWidth || pos.y < 0 || (int)pos.y >= mHeight || layer < 0 || layer >= mLayers)
            return null;
        return mMap[(int)pos.x][(int) pos.y][layer];
    }
    
    /**
     * Finds one "tile" of the level at a given position on the given layer. Also
     * checks if the position is valid (inside of the bounds).
     * @param x position of the tile
     * @param y position of the tile
     * @param layer layer (z-coordinate) of the tile
     * @return a reference to the Tile
     */
    public Tile getTile (double x, double y, int layer) {
        if (x < 0 || (int)x >= mWidth || y < 0 || (int)y >= mHeight || layer < 0 || layer >= mLayers)
            return null;
        else
            return mMap[(int)x][(int)y][layer];
    }    

    /**
     * Finds one "tile" of the level at a given position on the given layer. 
     * @param x position of the tile
     * @param y position of the tile
     * @param layer layer (z-coordinate) of the tile
     * @return a reference to the Tile 
     */
    public Tile getTile (int x, int y, int layer) {
        return mMap[x][y][layer];
    }
    
    /**
     * Finds the ground property at a particular point in the level. 
     * @param pos coordinate vector to desribe the point
     * @param layer map layer ("z-axis")
     * @return the ground property for a specific point
     */
    public GroundProperty getGroundProperty (Vector2D pos, int layer) {
        Tile vTile = getTile(pos, layer);
        if (vTile != null)
        {
            pos.x = pos.x % 1.0;
            pos.y = pos.y % 1.0;
            return vTile.getGroundProperty(pos);
        }
        return GroundProperty.WALL;
    }
    
    /**
     * Gets a list of all player of the level. 
     * @return reference player list
     */
    public ArrayList<Player> getPlayers () {
        return mPlayers;
    }
    
}
