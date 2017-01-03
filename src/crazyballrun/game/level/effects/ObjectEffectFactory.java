/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.effects;

/**
 * The ObjectEffectFactory generates instances of difference ObjectEffect-
 * implementations.
 * 
 * @author Timm Hoffmeister
 */
public class ObjectEffectFactory {
    
    /**
     * Creates an instance of ObjectEffect of a particular type. 
     * @param type type of effect
     * @return reference to the ObjectEffect
     */
    public static ObjectEffect create (String type) {
        
        if (type.equals("ExternalForce")) 
        {
            return new ExternalForce();
        } 
        else if (type.equals("ExternalTorque")) 
        {
            return new ExternalTorque();
        }
        
        return null;
    }    
}
