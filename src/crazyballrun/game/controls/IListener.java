/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.controls;

/**
 * The IListener provides an interface for event-listeners of IControls. The 
 * "notify"-method is called by the IControl. Attach an IListener to an IControl
 * (by the "addListener"-method) to get notified by the control. 
 * @author Timm Hoffmeister
 */
public interface IListener 
{    
    /**
     * Notifies the listener of an event.
     * @param event reference describing the type of event
     * @param type string describing the type of event
     * @param sender sender id of the sender (IControl.getId())
     */
    public void notify (Object event, String type, String sender);
    
}
