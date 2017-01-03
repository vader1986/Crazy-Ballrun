/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.controls.icontrols;

import crazyballrun.game.controls.ControlEngine;
import crazyballrun.game.controls.IControl;
import crazyballrun.game.controls.IListener;
import crazyballrun.game.graphics.GraphicsEngine;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Implementation of the IControl. The KeyboardControl offers a connection 
 * between keyboard-input and the game. 
 * @author Timm Hoffmeister
 */
public class KeyboardControl implements IControl 
{
    /**
     * List of all possible events which can occure through the keyboard. 
     */
    private ArrayList<String> mEventList = null;
    
    /**
     * This hashmap stores a list of listeners for each event.
     */
    private HashMap < String, ArrayList<IListener> > mEventListenerMap = null;
    
    /**
     * List of event listeners to remove.
     */
    private HashMap <String , LinkedList<IListener> > mRemoveListeners = null;

    /**
     * List of event listeners to add.
     */
    private HashMap <String , LinkedList<IListener> > mAddListeners = null;
    
    /**
     * Reference to the ControlEngine.
     */
    private ControlEngine mControlEngine = ControlEngine.getInstance();
    
    /**
     * Constructor of KeyboardControl. 
     */
    public KeyboardControl () 
    {        
        // Register event listeners to the frame
        GraphicsEngine.getInstance().getFrame().addKeyListener(new CBKeyListener());

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
     * Fills the event-list with all possible events the keyboard supports.
     */
    private void createEvents () 
    {
        mEventList.add("none");
        
        mEventList.add("key.press.a");
        mEventList.add("key.press.A");
        mEventList.add("key.press.b");
        mEventList.add("key.press.B");
        mEventList.add("key.press.c");
        mEventList.add("key.press.C");
        mEventList.add("key.press.d");
        mEventList.add("key.press.D");
        mEventList.add("key.press.e");
        mEventList.add("key.press.E");
        mEventList.add("key.press.f");
        mEventList.add("key.press.F");
        mEventList.add("key.press.g");
        mEventList.add("key.press.G");
        mEventList.add("key.press.s");
        mEventList.add("key.press.S");
        mEventList.add("key.press.w");
        mEventList.add("key.press.W");
        mEventList.add("key.release.a");
        mEventList.add("key.release.A");
        mEventList.add("key.release.b");
        mEventList.add("key.release.B");
        mEventList.add("key.release.c");
        mEventList.add("key.release.C");
        mEventList.add("key.release.d");
        mEventList.add("key.release.D");
        mEventList.add("key.release.e");
        mEventList.add("key.release.E");
        mEventList.add("key.release.f");
        mEventList.add("key.release.F");
        mEventList.add("key.release.g");
        mEventList.add("key.release.G");
        mEventList.add("key.release.s");
        mEventList.add("key.release.S");
        mEventList.add("key.release.w");
        mEventList.add("key.release.W");

        mEventList.add("key.press.escape");
        mEventList.add("key.press.return");
        mEventList.add("key.release.escape");
        mEventList.add("key.release.return");
        
        mEventList.add("key.press.up");
        mEventList.add("key.release.up");
        mEventList.add("key.press.down");
        mEventList.add("key.release.down");
        mEventList.add("key.press.left");
        mEventList.add("key.release.left");
        mEventList.add("key.press.right");
        mEventList.add("key.release.right");
                
        mEventList.add("key.press.all");    // catches all key-press-events
        mEventList.add("key.release.all");  // catches all key-release-events
    }
    
    @Override
    public String getId() {
        return "Keyboard";
    }

    @Override
    public ArrayList<String> getEvents() 
    {
        return mEventList;
    }

    @Override
    public void addListener(IListener listener, String event) 
    {
        mAddListeners.get(event).add(listener);
    }

    @Override
    public void removeListener(IListener listener, String event) 
    {
        mRemoveListeners.get(event).remove(listener);
    }
        
    /**
     * Catches and processes an event set by the KeyListener. 
     * @param event reference to the event
     * @param type type of event
     */
    private synchronized void processEvent(Object event, String type) 
    {        
        // Add all listeners for this event which are on the add-list
        if (!mAddListeners.isEmpty() && mAddListeners.containsKey(type)) {
            for (IListener listener : mAddListeners.get(type)) {
                mEventListenerMap.get(type).add(listener);
            }
            mAddListeners.get(type).clear();
        }
        
        // Remove all listeners for this event which are on the remove-list
        if (!mRemoveListeners.isEmpty() && mRemoveListeners.containsKey(type)) {
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
        mControlEngine.notify(getId(), type, null);
    }
    
    /**
     * Observes the key events and delegates them to the listeners.
     */
    class CBKeyListener extends KeyAdapter 
    {        
        @Override
        public void keyPressed (KeyEvent event)
        {
            processEvent(event,"key.press.all");
            
            // Non-Character-Key
            if (event.getKeyCode() == 27) {
                processEvent(event, "key.press.escape");
            } else if (event.getKeyCode() == 10) {
                processEvent(event, "key.press.return");
            } else if (event.getKeyCode() == 38) {
                processEvent(event, "key.press.up");
            } else if (event.getKeyCode() == 40) {
                processEvent(event, "key.press.down");
            } else if (event.getKeyCode() == 37) {
                processEvent(event, "key.press.left");
            } else if (event.getKeyCode() == 39) {
                processEvent(event, "key.press.right");
            }
            // Unicode-Key (character)
            else {
                String vChar = "" + event.getKeyChar();
                if (event.isShiftDown())
                    vChar = vChar.toUpperCase();
                processEvent(event, "key.press." + vChar);
            }
        }
        
        @Override
        public void keyReleased (KeyEvent event) 
        {
            processEvent(event,"key.release.all");
            
            // Non-Character-Key
            if (event.getKeyCode() == 27) {
                processEvent(event, "key.release.escape");
            } else if (event.getKeyCode() == 10) {
                processEvent(event, "key.release.return");
            } else if (event.getKeyCode() == 38) {
                processEvent(event, "key.release.up");
            } else if (event.getKeyCode() == 40) {
                processEvent(event, "key.release.down");
            } else if (event.getKeyCode() == 37) {
                processEvent(event, "key.release.left");
            } else if (event.getKeyCode() == 39) {
                processEvent(event, "key.release.right");
            }
            // Unicode-Key (character)
            else {
                String vChar = "" + event.getKeyChar();
                if (event.isShiftDown())
                    vChar = vChar.toUpperCase();
                processEvent(event, "key.release." + vChar);
            }
        }
    }    
    
}
