/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.controls;

import java.util.ArrayList;

/**
 * The IControl is an interface for game controllers like the mouse and keyboard. 
 * It will be initialized by the game engine and gets a reference to the EventMapper 
 * which has to be informed when events occur.
 * 
 * For external classes, like GUIControls, the IControl also has to offer a method
 * to register and remove event listeners. The GUIControl should register when
 * being activated by the frame and deregister when being deactivated by its frame.
 * 
 * @author Timm Hoffmeister
 */
public interface IControl 
{    
    /**
     * Returns the control's identifier describing the type of control.  
     * @return the controller's unique string identifier.
     */
    public String getId ();

    /**
     * Produces a list of all possible events the control supports. 
     * @return an array of event-ids of the control. 
     */
    public ArrayList<String> getEvents();

    /**
     * Subscribes an event listener to the control. 
     * @param listener reference to the event listener
     * @param event name of the event
     */
    public void addListener (IListener listener, String event);
    
    /**
     * Removes an event listener from the control.
     * @param listener reference to the event listener
     * @param event name of the event
     */
    public void removeListener (IListener listener, String event);
}
