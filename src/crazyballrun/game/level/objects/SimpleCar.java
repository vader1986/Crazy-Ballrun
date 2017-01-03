/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.objects;

import crazyballrun.game.GameConstants;
import crazyballrun.game.graphics.Animation;
import crazyballrun.game.graphics.GraphicsEngine;
import crazyballrun.game.level.effects.ExternalForce;
import crazyballrun.game.physics.bodies.PhysicalBody;
import crazyballrun.game.utilities.Vector2D;

/**
 * SimpleCar is a LevelObject-implementation of a simple car (like the name says). 
 * It offers idle- and moving-animations.
 * 
 * @author Timm Hoffmeister
 */
public class SimpleCar extends LevelObject {

    /**
     * Animation for an idle car. 
     */
    private Animation mAnimationIdle = null;
    
    /**
     * Animation of a moving car. 
     */
    private Animation mAnimationMoving = null;

    /**
     * Maximum speed of the car. 
     */
    private double mMaxSpeed = 1.0;
    
    @Override
    public Animation getAnimation() {
        switch (mState)
        {
            case MOVING:
                return mAnimationMoving;
            case IDLE:
                return mAnimationIdle;
            default:
                return mAnimationIdle;
        }
    }

    /**
     * Temporarily used variable to store the animation name while parsing (used
     * for initialization). 
     */
    protected String vAnimationIdleRoot;
    
    /**
     * Temporarily used variable to store the animation number while parsing (used
     * for initialization). 
     */
    protected int    vAnimationIdleNumber;

    /**
     * Temporarily used variable to store the animation name while parsing (used
     * for initialization). 
     */
    protected String vAnimationMovingRoot;

    /**
     * Temporarily used variable to store the animation number while parsing (used
     * for initialization). 
     */
    protected int    vAnimationMovingNumber;
    
    @Override
    public void setProperty(String property, String value) {

        if (property.equals("IdleAnimation"))
        {
            vAnimationIdleRoot = value;
        }
        else if (property.equals("IdleAnimationNumber"))
        {
            vAnimationIdleNumber = Integer.parseInt(value);
        }
        else if (property.equals("MovingAnimation"))
        {
            vAnimationMovingRoot = value;
        }
        else if (property.equals("MovingAnimationNumber"))
        {
            vAnimationMovingNumber = Integer.parseInt(value);
        }
    }

    @Override
    public void initialize() {
        mMaxSpeed = mObjectModel.getPhysicalProperty(PhysicalBody.P_MAXIMUM_SPEED);
        mAnimationIdle = GraphicsEngine.getInstance().getAnimation(vAnimationIdleRoot, mLevel.getName(), vAnimationIdleNumber, GameConstants.ANIMATION_TIMESTEP_MSEC);
        mAnimationMoving = GraphicsEngine.getInstance().getAnimation(vAnimationMovingRoot, mLevel.getName(), vAnimationMovingNumber, GameConstants.ANIMATION_TIMESTEP_MSEC);
        mAnimationIdle.setPosition(mObjectModel.getPosition());
        mAnimationMoving.setPosition(mObjectModel.getPosition());
    }
    
    /**
     * External force used for push-back after collision.
     */
    private ExternalForce mCounterForce = new ExternalForce();

    @Override
    public void onCollision(LevelObject obj) 
    {
        if (obj != null)
        {
            if (!mCounterForce.isAttached())
            {
                Vector2D vCounterForce = new Vector2D (mObjectModel.getVelocity());
                if (vCounterForce.length() > 0.001)
                {
                    vCounterForce.normalize();
                    mCounterForce.set(100, vCounterForce);
                    obj.attachEffect(mCounterForce);
                }
            }
        }
    }
    
}
