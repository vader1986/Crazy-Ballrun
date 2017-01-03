/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.controls.icontrols;

import crazyballrun.game.controls.ControlEngine;
import crazyballrun.game.controls.EventContent;
import crazyballrun.game.controls.IControl;
import crazyballrun.game.controls.IListener;
import crazyballrun.game.graphics.GraphicsEngine;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Observer of mouse events, like MouseMove, MouseClick etc. 
 * @author Timm Hoffmeister
 */
public class MouseControl implements IControl {

    /**
     * List of all possible events which can occure through the keyboard. 
     */
    private ArrayList<String> mEventList = null;

    /**
     * List of event listeners to remove.
     */
    private HashMap <String , LinkedList<IListener> > mRemoveListeners = null;

    /**
     * List of event listeners to add.
     */
    private HashMap <String , LinkedList<IListener> > mAddListeners = null;
    
    /**
     * This hashmap stores a list of listeners for each event.
     */
    private HashMap < String, ArrayList<IListener> > mEventListenerMap = null;
    
    /**
     * Reference to the ControlEngine.
     */
    private ControlEngine mControlEngine = ControlEngine.getInstance();
    
    /**
     * Contructor of MouseControl.
     */
    public MouseControl () {
        
        // Register event listeners to the rendering-frame
        CBMouseListener vListener = new CBMouseListener();
        GraphicsEngine.getInstance().getFrame().addMouseListener(vListener);
        GraphicsEngine.getInstance().getFrame().addMouseMotionListener(vListener);
        GraphicsEngine.getInstance().getFrame().addMouseWheelListener(vListener);
        
        // initialize event-/action-/listener-lists
        mEventList = new ArrayList<String>();
        mEventListenerMap = new HashMap<String, ArrayList<IListener>>();
        mRemoveListeners = new HashMap<String, LinkedList<IListener>>();
        mAddListeners = new HashMap<String, LinkedList<IListener>>();

        // fill event-list
        createEvents();
        
        // add events to event-listener-mapping
        for (String event : mEventList) {
            mEventListenerMap.put(event, new ArrayList<IListener>());
            mRemoveListeners.put(event, new LinkedList<IListener>());
            mAddListeners.put(event, new LinkedList<IListener>());
        }
    }

    /**
     * Fills the event-list with all possible events the mouse supports.
     */
    private void createEvents () {
        mEventList.add("none");
        mEventList.add("mouse.move");
        mEventList.add("mouse.click.left");
        mEventList.add("mouse.click.right");
        mEventList.add("mouse.click.middle");
        mEventList.add("mouse.press.left");
        mEventList.add("mouse.press.right");
        mEventList.add("mouse.press.middle");
        mEventList.add("mouse.release.left");
        mEventList.add("mouse.release.right");
        mEventList.add("mouse.release.middle");
        mEventList.add("mouse.wheel.click");
        mEventList.add("mouse.wheel.press");
        mEventList.add("mouse.wheel.release");
        mEventList.add("mouse.wheel.up");
        mEventList.add("mouse.wheel.down");
    }

    @Override
    public ArrayList<String> getEvents() {
        return mEventList;
    }
    
    /**
     * Catches and processes an event set by the MouseListener. 
     * @param event reference to the event
     * @param type type of event
     * @param x x-coordinate of the mouse event's position
     * @param y y-coordinate of the mouse event's position
     */
    private synchronized void processEvent(Object event, String type, int x, int y) {

        // Add all listeners for this event which are on the add-list
        if (mAddListeners.containsKey(type)) {
            for (IListener listener : mAddListeners.get(type)) {
                mEventListenerMap.get(type).add(listener);
            }
            mAddListeners.get(type).clear();
        }
        
        // Remove all listeners for this event which are on the remove-list
        if (mRemoveListeners.containsKey(type)) {
            for (IListener listener : mRemoveListeners.get(type)) {
                mEventListenerMap.get(type).remove(listener);
            }
            mRemoveListeners.get(type).clear();
        }
        
        // Notify event listeners
        if (mEventListenerMap.containsKey(type)) {
            for (IListener listener : mEventListenerMap.get(type)) {
                listener.notify(event, type, getId());
            }
        }
        
        // Notify ControlEngine
        mControlEngine.notify(getId(), type, new EventContent(x, y));
    }    
    
    /**
     * Observes the mouse events and delegates them to the listeners.
     */
    class CBMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                processEvent(event, "mouse.press.left", event.getX(), event.getY());
            }
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                processEvent(event, "mouse.release.left", event.getX(), event.getY());
            }
        }

        @Override
        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                processEvent(event, "mouse.click.left", event.getX(), event.getY());
            }
        }

        @Override
        public void mouseMoved(MouseEvent event){
            processEvent(event, "mouse.move", event.getX(), event.getY());
        }
                
        @Override
        public void mouseWheelMoved(MouseWheelEvent event) {
        } 
    }
    
    @Override
    public String getId() {
        return "Mouse";
    }

    @Override
    public void addListener(IListener listener, String event) {
        mAddListeners.get(event).add(listener);
    }    
    
    @Override
    public void removeListener(IListener listener, String event) {
        mRemoveListeners.get(event).add(listener);
    }
}
