/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.controllers;

import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.graphics.Camera;
import crazyballrun.game.level.Level;
import crazyballrun.game.level.LevelEngine;
import crazyballrun.game.level.objects.SimpleRocket;
import crazyballrun.game.physics.bodies.PhysicalBody;
import crazyballrun.game.utilities.GraphGenerator;
import crazyballrun.game.utilities.PathFinder;
import crazyballrun.game.utilities.Vector2D;
import java.util.ArrayList;

/**
 * The Player class simply represents a link between camera and vehicle. The 
 * player updates according to its vehicle's position. In addition, the Player
 * class supports internal force generation on the vehicle as well as weapon
 * usage.
 * 
 * @author Timm Hoffmeister
 */
public class Player extends ObjectController {
    
    /**
     * Reference to the player's camera.
     */
    private Camera mCamera = null;

    /**
     * Reference to the player's vehicle.
     */
    private LevelObject mVehicle = null;

    /**
     * Physical model of the vehicle.
     */
    private PhysicalBody mPhysicalModel = null;
    
    /**
     * Defines if the player has a camera or not. 
     */
    private boolean mHasCamera = false;

    /**
     * Player's start position.
     */
    private Vector2D mStartPosition;
    
    /**
     * Layer on which the player is placed at the beginning. 
     */
    private int mStartLayer;
    
    /**
     * Player number in the player list. 
     */
    private int mPlayerNumber;
    
    /**
     * Creates a new instance of Player. 
     * @param vehicle reference to the player's vehicle
     * @param position player's starting position
     * @param layer layer where the player's vehicle will be placed after chosen
     * @param id player number in player list
     */    
    public Player (LevelObject vehicle, Vector2D position, int layer, int id) 
    {
        mPlayerNumber = id;
        setStartPosition(position, layer);
        setVehicle(vehicle);
    }

    /**
     * Creates a new instance of Player.
     * @param position player's starting position
     * @param layer layer where the player's vehicle will be placed after chosen
     * @param id player number in player list
     */
    public Player (Vector2D position, int layer, int id) 
    {
        mPlayerNumber = id;
        setStartPosition(position, layer);
    }

    /**
     * Sets the starting layer and position of the player. 
     * @param position 2d-position of the player
     * @param layer layer of the player
     */
    public final void setStartPosition (Vector2D position, int layer) 
    {
        mStartPosition = position;
        mStartLayer = layer;
    }
    
    /**
     * Sets the player's vehicle. 
     * @param vehicle reference to the LevelObject (vehicle)
     */
    public final void setVehicle (LevelObject vehicle) 
    {
        mVehicle = vehicle;
        mVehicle.setController(this);
        mPhysicalModel = mVehicle.getObjectModel();
        mPhysicalModel.setPosition(mStartPosition.x, mStartPosition.y);
        mPhysicalModel.setLayer(mStartLayer);
    }
    
    /**
     * Gets a reference to the player's vehicle.
     * @return player's vehicle
     */
    public final LevelObject getVehicle () 
    {
        return mVehicle;
    }
    
    /**
     * Sets the camera of the player. 
     * @param camera reference to the camera object (may be null)
     */
    public final void setCamera (Camera camera) 
    {
        mCamera = camera;
        mHasCamera = (mCamera != null);
    }
    
    /**
     * Gets a reference to the camera of the player.
     * @return camera of the player
     */
    public final Camera getCamera () 
    {
        return mCamera;
    }
    
    /**
     * Finds out if the player has a camera. 
     * @return 'true' if the player has a camera
     */
    public final boolean hasCamera () 
    {
        return mHasCamera;
    }
    
    /**
     * Gets the id of the player. 
     * @return player id
     */
    public final int getPlayerNumber () 
    {
        return mPlayerNumber;
    }

    /**
     * Updates the camera position (in the level) according to the vehicle 
     * position of the player. 
     */
    public void update () 
    {
        mCamera.setCameraCenter(mPhysicalModel.getCenter(), mPhysicalModel.getLayer());
    }

    /**
     * Enumeration of all possible player-actions. 
     */
    public enum Action
    {
        /**
         * Start acceleration of the player's vehicle. 
         */
        START_ACCELERATION,
        /**
         * Start backward-acceleration of the player's vehicle.
         */
        START_ACCELERATION_BACKWARD,
        /**
         * Stop (backward-)acceleration of the player's vehicle. 
         */
        STOP_ACCELERATION,
        /**
         * Start left rotation of the player's vehicle.
         */
        START_ROTATION_LEFT,
        /**
         * Start right rotation of the player's vehicle.
         */
        START_ROTATION_RIGHT,
        /**
         * Stop rotation of the player's vehicle.
         */
        STOP_ROTATION,
        /**
         * Player uses a rocket. 
         */
        USE_ROCKET
    }
    
    /**
     * Defines which actions have been performed.
     */
    private boolean [] mActionPerformed = new boolean[Action.values().length];

    /**
     * Forces the player to perform a particular action. 
     * @param action type of action
     */
    public synchronized void startAction (Action action)
    {
        mActionPerformed[action.ordinal()] = true;
    }
    
    /**
     * Stops a particular action.
     * @param action type of action
     */
    public synchronized void stopAction (Action action)
    {
        mActionPerformed[action.ordinal()] = false;
    }
    
    /**
     * Gets if an action should be performed. 
     * @param action type of action
     * @return 'true' if action is to be performed
     */
    public synchronized boolean isPerformed (Action action)
    {
        return mActionPerformed[action.ordinal()];
    }
    
    /**
     * Resets the acceleration- and brake-force (e.g. after releasing the speed-
     * button).
     */
    public void stopForce () 
    {
        mPhysicalModel.detachAcceleration();
    }
    
    /**
     * Resets the rotation-torque (e.g. after releasing the rotation-button).
     */
    public void stopTorque () 
    {
        mPhysicalModel.detachRotation();
    }
    
    /**
     * Accelerates the vehicle of the player. 
     */
    public void accelerate () 
    {
        mPhysicalModel.applyAcceleration(true);
    }

    /**
     * Accelerates the vehicle of the player backwards. 
     */
    public void backwards () 
    {
        mPhysicalModel.applyAcceleration(false);
    }

    /**
     * Player's vehicle turns left.
     */
    public void turnLeft () 
    {
        mPhysicalModel.applyRotation(true);
    }

    /**
     * Player's vehicle turns right.
     */
    public void turnRight () 
    {
        mPhysicalModel.applyRotation(false);
    }
    
    /**
     * Uses one rocket following the closest opponent player. 
     */
    public void useSimpleRocket () 
    {
        // Create Rocket
        Level vLevel = LevelEngine.getInstance().getCurrentLevel();
        SimpleRocket vRocket = (SimpleRocket) vLevel.createObject("Rocket", mPhysicalModel.getCenter(), mPhysicalModel.getLayer(), mPhysicalModel.getRotation());
        vRocket.setLevel(vLevel);
        vRocket.setOwner(mVehicle, mPlayerNumber);

        // Create Rocket-Controller
        PlayerFinder vFinder = new PlayerFinder();
        vFinder.setOwner(mPlayerNumber);
        vFinder.setControlObject(vRocket);
        vFinder.initialize(null, null);
        vRocket.setController(vFinder);
    }
    
    @Override
    public void control(double dt) 
    {
        // Check which actions have been performed
        for (Action a : Action.values())
        {
            if (isPerformed(a))
            {
                switch (a)
                {
                    case START_ACCELERATION:
                    {
                        accelerate();
                        break;
                    }
                    case START_ACCELERATION_BACKWARD:
                    {
                        backwards();
                        break;
                    }
                    case STOP_ACCELERATION:
                    {
                        stopForce();
                        break;
                    }
                    case START_ROTATION_LEFT:
                    {
                        turnLeft();
                        break;
                    }
                    case START_ROTATION_RIGHT:
                    {
                        turnRight();
                        break;
                    }
                    case STOP_ROTATION:
                    {
                        stopTorque();
                        break;
                    }
                    case USE_ROCKET:
                    {
                        useSimpleRocket();
                        break;
                    }
                }
                stopAction(a);
            }
        }
        
        // Sub-Classes may overwrite this method (e.g. AI uses this method for
        // control-activities like acceleration, rotation, using weapons, ...).
    }

    @Override
    public void setProperty(String name, String value) 
    {
        // Sub-Classes may overwrite this method (e.g. AI uses it for setting 
        // ai-specific parameters). 
    }

    @Override
    public void initialize(ArrayList<GraphGenerator> graphs, PathFinder pathfinder) 
    {
        // Sub-Classes may overwrite this method (e.g. AI uses it for storing its 
        // pathfinding-system in a member variable). 
    }
            
}
