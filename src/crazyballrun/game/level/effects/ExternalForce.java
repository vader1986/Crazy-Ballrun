/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.effects;

import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.utilities.Vector2D;

/**
 * ExternalForce is an implementation of ObjectEffect and generates external
 * forces uppon a LevelObject for a period of time. 
 * 
 * @author Timm Hoffmeister
 */
public class ExternalForce extends ObjectEffect {
    
    /**
     * Remaining lifetime of the effect.
     */
    private int mLifeTime = 0;
    
    /**
     * Force-vector.
     */
    private Vector2D mForce = new Vector2D();
    
    /**
     * Force is attached to the rigid body.
     */
    private boolean mAttached = false;

    /**
     * Contructor of ExternalForce.
     */
    public ExternalForce () {
    }
    
    /**
     * Contructor of ExternalForce.
     * @param lifetime lifetime of the effect
     * @param vForce force-vector
     */
    public ExternalForce (int lifetime, Vector2D vForce) {
        mLifeTime = lifetime;
        mForce = vForce;
    }

    /**
     * Initializes the external-force-effect.
     * @param lifetime lifetime of the effect
     * @param force force vector
     */
    public final void set(int lifetime, Vector2D force) {
        mLifeTime = lifetime;
        mForce = force;
    }
    
    /**
     * Initializes the external-force-effect.
     * @param lifetime lifetime of the effect
     * @param x x-force-component
     * @param y y-force-component
     */
    public final void set(int lifetime, double x, double y) {
        mLifeTime = lifetime;
        mForce.x = x;
        mForce.y = y;
    }

    /**
     * Finds out if the effect is attached to an object. 
     * @return "true" if the effect is attached to an object
     */
    public boolean isAttached () {
        return mAttached;
    }
    
    @Override
    public void effect(LevelObject obj, int dt) 
    {
        if (!mAttached && mLifeTime > 0)
        {
            obj.getObjectModel().attachAcceleration(mForce);
            mAttached = true;
        }
        
        mLifeTime -= dt;
        
        if (mAttached && mLifeTime <= 0)
        {
            obj.getObjectModel().detachAcceleration(mForce);
            mAttached = false;
        }
    }

    @Override
    public int getLifeTime() {
        return mLifeTime;
    }
    
    
}
