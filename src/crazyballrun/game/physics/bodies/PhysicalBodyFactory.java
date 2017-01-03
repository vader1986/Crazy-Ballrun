/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.physics.bodies;


/**
 * The PhysicalBodyFactory generates PhysicalBody-instances.
 * 
 * @author Timm Hoffmeister
 */
public class PhysicalBodyFactory {
    
    /**
     * Generates an instance of PhysicalBody.
     * @param id type of the rigid body
     * @return reference to the PhysicalBody-instance
     */
    public static PhysicalBody create (String id) 
    {
        PhysicalBody vReturn = null;
        
        if (id.equals("Vehicle")) {
            vReturn = new Vehicle();
        }
        else if (id.equals("SoftBody")) {
            vReturn = new SoftBody();
        }
        
        if (vReturn != null)
            vReturn.setType(id);
        
        return vReturn;
    }
}
