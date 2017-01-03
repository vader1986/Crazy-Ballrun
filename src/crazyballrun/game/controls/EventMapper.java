/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.controls;

import crazyballrun.game.GameFunctions;
import java.util.HashMap;

/**
 * The EventMapper maps events to actions (like "arrow key pressed" -> "rotate
 * player"). Therefore it offers a method "map" which exacly performs this 
 * task. The EventMapper has to be initialized to build up a map of event-action-
 * pairs. One event may be mapped on just a single action. However, one action
 * may be performed by a list of events (e.g. "press return" and "press space"
 * may both result in "player acceleration"). 
 * 
 * @author Timm Hoffmeister
 * @see ControlEngine
 */
public class EventMapper {

    /**
     * Mapping events (String) to actions (Action-enumeration defined in 
     * GameFunctions). 
     */
    private HashMap<String, GameFunctions.Action> mEventActionMap = null;

    /**
     * Constructor of EventMapper. 
     */
    public EventMapper () {
        mEventActionMap = new HashMap<String, GameFunctions.Action>();
    }
    
    /**
     * Adds an event-action pair to the event-action-map. 
     * @param event name of the event (add each event just once!)
     * @param action string representation of the action (in enum-form)
     */
    public void add(String event, String action) {
        if (!mEventActionMap.containsKey(event)) {
            mEventActionMap.put(event, GameFunctions.translate(action));
        }
    }

    /**
     * Maps the given event to an action (which will be performed).
     * @param event name of the event
     */
    public void map (String event) {
        if (mEventActionMap.containsKey(event))
            GameFunctions.perform(mEventActionMap.get(event));
    }
    
    /**
     * Maps the given event to an action (which will be performed).
     * @param event name of the event
     * @param content content of the event
     */
    public void map (String event, EventContent content) {
        if (mEventActionMap.containsKey(event))
            GameFunctions.perform(mEventActionMap.get(event), content);
    }
    
}
