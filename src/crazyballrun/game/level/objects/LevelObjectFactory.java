/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.objects;

/**
 * The LevelObjectFactory generates instances of different LevelObject-
 * implementations. 
 * 
 * @author Timm Hoffmeister
 */
public class LevelObjectFactory {

    /**
     * Creates a particular type of LevelObject-instance. 
     * @param type type of object
     * @return reference to the LevelObject
     */
    public static LevelObject create (String type) 
    {
        LevelObject vReturn = null;
        
        if (type.equals("SimpleCar"))
        {
            vReturn = new SimpleCar();
        }
        else if (type.equals("SimpleRocket"))
        {
            vReturn = new SimpleRocket();
        }
        
        if (vReturn != null)
            vReturn.setType(type);
            
        return vReturn;
    }
    
}
