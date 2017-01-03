/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui.interfaces;

/**
 * IControlObserver offers an interface for other gui controls to register for 
 * events (like Listbox-events: selection changes, scrollbar changes, ...).
 * @author Timm Hoffmeister
 */
public interface IControlObserver {
    
    /**
     * Registers a listener for IControlObserver-events. 
     * @param listener reference to the IControlObserver-implementation-instance
     */
    public void register (IControlSubscriber listener);
    
}
