/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui.interfaces;

/**
 * Each class (usually used for GUIControls) implementing this interface may 
 * register at a IControlObserver by referencing its id (has to be in the same
 * GUIFrame). 
 * @author Timm Hoffmeister
 */
public interface IControlSubscriber {
    
    /**
     * Notifies an IControlSubscriber of an event (like selection changed,
     * scrollbar position changed, ...). 
     * @param event reference to the event description
     * @param sender reference to the sender
     * @return 'true' if the notification has been accepted. 
     */
    public boolean notify (Object event, IControlObserver sender);
    
}
