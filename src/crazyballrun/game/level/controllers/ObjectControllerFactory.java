/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.controllers;

/**
 * The ObjectControllerFactory generates instances of difference ObjectController-
 * implementations.
 * 
 * @author Timm Hoffmeister
 */
public class ObjectControllerFactory {

    /**
     * Creates an instance of ObjectController of a particular type. 
     * @param type type of controller
     * @return reference to the ObjectController
     */
    public static ObjectController create (String type) {
        
        // TODO: implement AI-controller
        
        if (type.equals("WaypointFollower")) 
        {
            return new WaypointFollower();
        }
        else if (type.equals("PlayerFinder"))
        {
            return new PlayerFinder();
        }
        
        return null;
    }
    
}
