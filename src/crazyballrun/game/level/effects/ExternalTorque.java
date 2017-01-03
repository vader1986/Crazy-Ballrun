/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.effects;

import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.physics.bodies.PhysicalBody;

/**
 * The ExternalTorque effect applies some rotation speed uppon an object for
 * a particular period of time.
 * 
 * @author Timm Hoffmeister
 */
public class ExternalTorque extends ObjectEffect {
    
    /**
     * Remaining lifetime of the effect.
     */
    private int mLifeTime = 0;
    
    /**
     * Torque applied to the object.
     */
    private Double mTorque;
    
    /**
     * Force is attached to the rigid body.
     */
    private boolean mAttached = true;

    /**
     * Contructor of ExternalTorque.
     */
    public ExternalTorque () 
    {
    }
    
    /**
     * Contructor of ExternalTorque.
     * @param lifetime lifetime of the effect
     * @param vTorque
     * @param obj reference of the object uppon which to apply the effect
     */
    public ExternalTorque (int lifetime, Double vTorque, PhysicalBody obj) 
    {
        mLifeTime = lifetime;
        mTorque = vTorque;
        obj.attachRotation(mTorque);
    }

    @Override
    public void effect(LevelObject obj, int dt) 
    {
        mLifeTime -= dt;
        if (mAttached && mLifeTime <= 0)
        {
            obj.getObjectModel().detachRotation(mTorque);
            mAttached = false;
        }
    }

    @Override
    public int getLifeTime() 
    {
        return mLifeTime;
    }    
    
}
