/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui;

import crazyballrun.game.gui.controls.*;

/**
 * The GUIControlFactory is used to create any possible GUI component by a 
 * unique string identifier. 
 * @author Timm Hoffmeister
 */
public class GUIControlFactory 
{
    /**
     * Create an instance of a specified GUIControl. 
     * @param id string identifier for the type of control
     * @return an instance of the specified GUIControl or null.
     */
    public static GUIControl create (String id) 
    {
        if (id == null) return null;
        
        if (id.equals("Button")) 
        {
            return new CBButton();
        } 
        else if (id.equals("Frame")) 
        {
            return new CBFrame();
        } 
        else if (id.equals("ListBox")) 
        {
            return new CBListBox();
        } 
        else if (id.equals("LevelDescription")) 
        {
            return new CBLevelDescription();
        } 
        else if (id.equals("LoadScreen"))
        {
            return new CBLoadImage();
        }
        else if (id.equals("TextBox"))
        {
            return new CBTextBox();
        }
        
        return null;
    }
    
}
