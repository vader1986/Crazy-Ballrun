/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.controllers;

import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.utilities.GraphGenerator;
import crazyballrun.game.utilities.PathFinder;
import java.util.ArrayList;

/**
 * The ObjectController is the controlling unit for a LevelObject. It should 
 * mainly do the pathfinding by applying internal forces and torques. The
 * ObjectController supports different sorts of pathfinding, like waypoint-
 * following, navigation meshes and simple goal-following. 
 * 
 * @author Timm Hoffmeister
 */
public abstract class ObjectController {
    
    /**
     * Reference to the LevelObject-instance which is controlled by the 
     * ObjectController. 
     */
    protected LevelObject mObject = null;
    
    /**
     * Sets a reference to the LevelObject controlled by the ObjectController.
     * @param obj reference to the object
     */
    public final void setControlObject (LevelObject obj) {
        mObject = obj;
    }
    
    /**
     * This method is called by the PhysicsThread. The implementation should 
     * perform actions (apply forces, torques or use weapons, for example) for
     * the LevelObject.
     * @param dt time passed since the last call (in seconds)
     */
    public abstract void control (double dt);
    
    /** 
     * Sets one property. This method is used by the LevelObjectParser to support 
     * implementation specific data (e.g. if the control uses waypoints, it needs
     * the position of them, if the control evaluates it's next goal position by
     * heuristic values, they may be specified here). 
     * 
     * @param name name of the property
     * @param value value for the property
     */
    public abstract void setProperty (String name, String value);
    
    /**
     * Initializes the ObjectController after setting all properties. This 
     * method is automatically called and must be overwritten by subclasses.
     * The method gives access to a pathfinding-algorithm and the corresponding
     * graphs (one for each layer). This may be used for AIs/NPCs to get the 
     * appropriate path to their goals. 
     * 
     * @param graphs reference to the GraphGenerators (which already generated the
     * graph (use getNode()-method to find nodes at particular positions)
     * @param pathfinder reference to the PathFinder - give him a start- and
     * goal-node and he returns the shortest path
     */
    public abstract void initialize (ArrayList<GraphGenerator> graphs, PathFinder pathfinder);
}
