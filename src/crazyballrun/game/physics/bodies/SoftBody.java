/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.physics.bodies;

import crazyballrun.game.level.Level;
import crazyballrun.game.utilities.Vector2D;

/**
 * The SoftBody implements the rigid body. For collision it ignores texture-
 * collisions and does not give any hit-reactions when other objects are crashing
 * it. 
 * 
 * @author Timm Hoffmeister
 */
public class SoftBody extends PhysicalBody {

    /**
     * Collision radius.
     */
    private double mRadius;
    
    /**
     * Constructor of SoftBody.
     */
    public SoftBody () 
    {
        
    }
    
    @Override
    public boolean isRigidBody() 
    {
        return false;
    }

    @Override
    public boolean isColliding(double px, double py) 
    {
        // Other objects cannot crash into 
        // this one because it's not solid.
        return false;
    }

    @Override
    public boolean isColliding(PhysicalBody obj) 
    {
        Vector2D vPos = obj.getCenter();
        vPos.sub(getCenter());
        if (vPos.length() < mRadius)
            return true;
        return false;
    }

    @Override
    public Vector2D isColliding(Level map) 
    {
        // No texture collisions.
        return null;
    }

    @Override
    public void updateData() 
    {
        // Not necessary to update any collision
        // data. There's just the "collision radius"
        // which doesn't has to be updated. For the
        // position there's always used the current
        // position for collision.
    }

    @Override
    public void setProperty(String name, String value) 
    {
        if (name.compareTo("Radius") == 0) 
        {
            mRadius = Double.parseDouble(value);
        }
        else
        {
            setPhysicalProperty(name, Double.parseDouble(value));
        }
    }

    @Override
    public void initialize() 
    {
        // nothing to do here
    }

    @Override
    public double getBoundingSphere() 
    {
        return mRadius;
    }
    
}
