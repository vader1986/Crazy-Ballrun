/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui;

import crazyballrun.game.gui.interfaces.IControlObserver;
import crazyballrun.game.gui.interfaces.IControlSubscriber;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * The GUIFrame defines on menu-screen with a couple of GUIControls. 
 * @author Timm Hoffmeister
 */
public class GUIFrame {

    /**
     * List of all GUIControls of the frame.
     */
    private ArrayList <GUIControl> mGuiControls = null;

    /**
     * Unique String identifier of the menu frame.
     */
    private String mId = null;
    
    /**
     * Constructor of GUIFrame.
     * @param id string identifier of the menu frame
     */
    public GUIFrame (String id) {
        mGuiControls = new ArrayList<GUIControl>();
        mId = id;
    }
        
    /**
     * Gets the unique string identifier of the frame. 
     * @return the id of the frame.
     */
    public String getId () {
        return mId;
    }
    
    /**
     * Finds a GUIControl by its id.
     * @param id unique string identifier of the control
     * @return the control or 'null' if not found. 
     */
    public GUIControl getControl (String id) {
        for (GUIControl control : mGuiControls) {
            if (control.getId().compareTo(id) == 0) {
                return control;
            }
        }
        return null;
    }

    /**
     * Initializes all its GUIControls. 
     */
    public void initialize () {
        for (GUIControl vControl : mGuiControls)
            vControl.initialize();
    }
    
    /**
     * Registers an IControlSubstriber for a particular IControlObserver.
     * @param observerId string identifier of the observer
     * @param subs reference to the subscriber
     */
    public void register (String observerId, IControlSubscriber subs) {
        GUIControl observer = getControl(observerId);
        if (observer != null && observer instanceof IControlObserver) {
            ((IControlObserver)observer).register(subs);
        }
    }
    
    /**
     * Adds a GUIControl to the menu frame.
     * @param control reference to the GUIControl-instance
     */
    public void addControl (GUIControl control) {
        mGuiControls.add(control);
    }
    
    /**
     * Paints the GUIFrame (the included GUIControls).
     * @param g Graphics-reference
     * @param frame Frame for rendering
     */
    public void paint (Graphics g, Frame frame) {
        for (GUIControl vControl : mGuiControls) {
            vControl.paint(g, frame);
        }
    }
    
    /**
     * Sets the frame and all its controls active (visible).
     */
    public void setActive () {
        for (GUIControl vControl : mGuiControls) {
            vControl.activate();
        }
    }

    /**
     * Sets the frame and all its controls inactive (invisible).
     */
    public void setInActive () {
        for (GUIControl vControl : mGuiControls) {
            vControl.deactivate();
        }
    }

}
