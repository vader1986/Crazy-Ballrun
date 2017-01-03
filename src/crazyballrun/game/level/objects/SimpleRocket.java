/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.objects;

import crazyballrun.game.GameConstants;
import crazyballrun.game.graphics.Animation;
import crazyballrun.game.graphics.GraphicsEngine;
import crazyballrun.game.level.LevelEngine;
import crazyballrun.game.level.Statistics;
import crazyballrun.game.level.Tile;
import crazyballrun.game.level.controllers.Player;
import crazyballrun.game.level.effects.ExternalTorque;

/**
 * The SimpleRocket is an easy implementation of LevelObject that produces a 
 * strange rotation-effect uppon another object when colliding.
 * 
 * @author Timm Hoffmeister
 */
public class SimpleRocket extends LevelObject {

    /**
     * Owner of the rocket. 
     */
    private LevelObject mOwner = null;

    /**
     * Id of the owner of the rocket. 
     */
    private int mOwnerId = -1;
    
    /**
     * Animation of a moving rocket. 
     */
    private Animation mAnimationMoving = null;

    /**
     * Animation of a dieing rocket. 
     */
    private Animation mAnimationDeath = null;
    
    /**
     * Lifetime of the rocket in miliseconds. Infinity if negative.
     */
    private long mLifeTime = -1;
    
    /**
     * Sets the owner of the rocket. 
     * @param owner reference to the owner object
     * @param ownerId id of the owner-player
     */
    public void setOwner (LevelObject owner, int ownerId)
    {
        mOwner = owner;
        mOwnerId = ownerId;
    }
    
    @Override
    public Animation getAnimation() 
    {
        if (mAnimationMoving.getLifeTime() == 0)
        {
            // destroy rocket
            this.destroy();

            // add death-animation to current tile
            Tile vTile = mLevel.getTile(mObjectModel.getCenter(), mObjectModel.getLayer());
            if (vTile != null)
            {
                mAnimationDeath.setPosition(mObjectModel.getPosition());
                mAnimationDeath.setRotation(mObjectModel.getRotation());
                vTile.addAnimation(mAnimationDeath);
            }
        }
        return mAnimationMoving;
    }

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

    /**
     * Temporarily used variable to store the animation name while parsing (used
     * for initialization). 
     */
    protected String vAnimationDeathRoot;

    /**
     * Temporarily used variable to store the animation number while parsing (used
     * for initialization). 
     */
    protected int    vAnimationDeathNumber;
    
    @Override
    public void setProperty(String property, String value) 
    {
        if (property.equals("MovingAnimation"))
        {
            vAnimationMovingRoot = value;
        }
        else if (property.equals("MovingAnimationNumber"))
        {
            vAnimationMovingNumber = Integer.parseInt(value);
        }
        else if (property.equals("DeathAnimation"))
        {
            vAnimationDeathRoot = value;
        }
        else if (property.equals("DeathAnimationNumber"))
        {
            vAnimationDeathNumber = Integer.parseInt(value);
        }
        else if (property.equals("Lifetime"))
        {
            mLifeTime = Long.parseLong(value);
        }
    }

    @Override
    public void initialize() 
    {
        mAnimationMoving = GraphicsEngine.getInstance().getAnimation(vAnimationMovingRoot, mLevel.getName(), vAnimationMovingNumber, GameConstants.ANIMATION_TIMESTEP_MSEC);
        mAnimationDeath = GraphicsEngine.getInstance().getAnimation(vAnimationDeathRoot, mLevel.getName(), vAnimationDeathNumber, GameConstants.ANIMATION_TIMESTEP_MSEC);
        mAnimationMoving.setPosition(mObjectModel.getPosition());
        mAnimationDeath.setPosition(mObjectModel.getPosition());
        mAnimationDeath.setPlayOnce(true);
        mAnimationMoving.setLifeTime(mLifeTime);
    }

    @Override
    public void onCollision(LevelObject obj) 
    {
        // Skip owner of the rocket
        if (mOwner == obj) return;
        
        // destroy rocket
        this.destroy();
        
        // add death-animation to current tile
        Tile vTile = mLevel.getTile(mObjectModel.getCenter(), mObjectModel.getLayer());
        if (vTile != null)
        {
            mAnimationDeath.setPosition(mObjectModel.getPosition());
            mAnimationDeath.setRotation(mObjectModel.getRotation());
            vTile.addAnimation(mAnimationDeath);
        }

        // apply rocket-effect to the object
        obj.getObjectModel().setVelocity(0, 0);
        obj.attachEffect(new ExternalTorque(2000, 15.0, obj.mObjectModel));
        
        // update statistics
        if (obj.getController() instanceof Player)
        {
            // increase kill-counter of rocket-owner
            Statistics vStatistics = LevelEngine.getInstance().getCurrentLevel().getStatistics();
            vStatistics.incValue(mOwnerId, Statistics.ValueType.KILLS);
            
            // increase death-counter of target player
            Player vOpponent = (Player) obj.getController();
            vStatistics.incValue(vOpponent.getPlayerNumber(), Statistics.ValueType.DEATHS);
        }
    }
    
}
