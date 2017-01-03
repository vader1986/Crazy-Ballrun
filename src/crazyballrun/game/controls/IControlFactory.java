/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.controls;

import crazyballrun.game.controls.icontrols.*;

/**
 * The IControlFactory provides a list of all implementations of the IControl. 
 * It can create new instances according to a given control identifier string.
 * @author Timm Hoffmeister
 */
public class IControlFactory {

    /**
     * Creates an instance of the specified control. 
     * @param id unqiue string identifier for the type of control
     * @return an instance of the given control. 
     */
    public static IControl create (String id) {
        if (id == null) return null;
        
        if (id.compareTo("Keyboard") == 0) {
            return new KeyboardControl();
        } else if (id.compareTo("Mouse") == 0) {
            return new MouseControl();
        }
        
        return null;
    }
    
}
