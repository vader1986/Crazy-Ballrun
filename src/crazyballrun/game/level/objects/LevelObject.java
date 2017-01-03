/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.objects;

import crazyballrun.game.level.effects.ObjectEffect;
import crazyballrun.game.level.controllers.ObjectController;
import crazyballrun.game.graphics.Animation;
import crazyballrun.game.level.Level;
import crazyballrun.game.level.Tile;
import crazyballrun.game.music.MusicEngine;
import crazyballrun.game.physics.bodies.PhysicalBody;
import crazyballrun.game.utilities.Vector2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This abstract class defines basic functionality of static and moving objects
 * in a level. It also represents the relationship between physical and visual
 * model of the object (the physics engine uses the RigidBody for collision 
 * detection, while the graphics engine uses the Animation for rendering).
 * 
 * @author Timm Hoffmeister
 */
public abstract class LevelObject {
    
    /**
     * All possible state of a level object. 
     */
    public enum ObjectState {
        /**
         * Object is idle (not moving).
         */
        IDLE,
        /**
         * Object is moving (not idle).
         */
        MOVING,
        /**
         * Object is dieing (idle and has no physical body anymore).
         */
        DIEING
    }
    
    /**
     * List of all properties of the object.
     */
    private ArrayList<String> mProperties = new ArrayList<String>();

    /**
     * List of all property values of the object. 
     */
    private ArrayList<String> mPropertyValues = new ArrayList<String>();
    
    /**
     * The current level's tile where the center point of the level object is 
     * positioned in.
     */
    private Tile mLevelTile = null;

    /**
     * Reference to the current level.
     */
    protected Level mLevel = null;

    /**
     * Type of object. 
     */
    private String mObjectType = null;
    
    /**
     * Current state of the object. Do not change the state of the object in
     * subclasses (just if you really know what you're doing, which is probably
     * NOT the case!)! 
     */
    protected ObjectState mState = ObjectState.IDLE;

    /**
     * Physical model of the object. 
     */
    protected PhysicalBody mObjectModel = null;
    
    /**
     * Reference to the control unit of the object. 
     */
    private ObjectController mController = null;
    
    /**
     * List of applied effects.
     */
    private LinkedList<ObjectEffect> mAttachedEffects = new LinkedList<ObjectEffect>();
    
    /**
     * Lock for the attached effects.
     */
    private final Object mLockEffects = new Object();
    
    /**
     * Constructor of LevelObject. 
     */
    public LevelObject () {
        
    }

    /**
     * Attaches an effect to the object.
     * @param fx reference to the effect
     */
    public final void attachEffect (ObjectEffect fx) {
        synchronized(mLockEffects)
        {
            mAttachedEffects.add(fx);
        }
    }
    
    /**
     * Sets the type of the object.
     * @param type type name
     */
    public final void setType (String type)
    {
        mObjectType = type;
    }
    
    /**
     * Sets the rigid body of the object.
     * @param body reference to the RigidBody
     */
    public final void setRigidBody (PhysicalBody body) {
        mObjectModel = body;
    }
    
    /**
     * Sets the object controller.
     * @param controller reference to the controller
     */
    public final synchronized void setController (ObjectController controller) {
        mController = controller;
    }
    
    /**
     * Sets the Level-reference of the LevelObject.
     * @param level reference to the level
     */
    public final void setLevel (Level level) {
        mLevel = level;
    }
    
    /**
     * Sets the physical rotation of the object in the level.
     * @param angle angle of rotation
     */
    public final void setRotation (double angle) {
        mObjectModel.setRotation(angle);
    }
    
    /**
     * Sets the physical position of the object in the level. 
     * @param position 2d-position
     * @param layer layer where to place the object
     */
    public final void setPosition (Vector2D position, int layer) {
        mObjectModel.setPosition(position.x, position.y);
        mObjectModel.setLayer(layer);
    }
    
    /**
     * Gets a (threadsafe) reference to the ObjectController. 
     * @return object controlling unit
     */
    public synchronized ObjectController getController () {
        return mController;
    }
    
    /**
     * Forces the object's controller to perform an action. If there is no internal
     * controller this method will anyway perform all of the attached effects. 
     * @param dt time passed since the last call (in miliseconds)
     * @param dt_sec time passed since the last call (in seconds)
     */
    public void control (int dt, double dt_sec) 
    {
        // controller of the object should specify actions
        if (getController () != null)
            mController.control(dt_sec);
        
        // effects attached to the object may perform actions
        synchronized(mLockEffects)
        {
            Iterator<ObjectEffect> vIter = mAttachedEffects.iterator();
            while (vIter.hasNext())
            {
                ObjectEffect vEffect = vIter.next();
                vEffect.effect(this, dt);
                if (vEffect.getLifeTime() <= 0)
                {
                    vIter.remove();
                }
            }
        }
    }
    
    /**
     * Updates the object after change of physics. Therefore, the object will be
     * moved into the Tile covered by its center point and the position of 
     * the animation of the object is synchronized with the physical position of
     * the object. This method has to be called by the PhysicsThread. The update
     * method also updates the current state of the object and applies actions
     * due to its controller. 
     */
    public synchronized void update () 
    {        
        Animation vAnimation = getAnimation();

        // destroy object
        if (mState == ObjectState.DIEING)
        {
            mLevelTile.delRigidBody(this);
            mLevelTile.delAnimation(vAnimation);
            return;
        }
        
        // update object state
        boolean vStateChanged = false;
        boolean vHasSpeed = mObjectModel.hasSpeed();
        if (mState == ObjectState.IDLE && vHasSpeed)
        {
            vStateChanged = true;
            mState = ObjectState.MOVING;
        } 
        else if (mState == ObjectState.MOVING && !vHasSpeed)
        {
            vStateChanged = true;
            mState = ObjectState.IDLE;
        }
        
        // State changed
        if (vStateChanged && mLevelTile != null)
        {
            mLevelTile.delAnimation(vAnimation);
            vAnimation = getAnimation();
            mLevelTile.addAnimation(vAnimation);
        }
        
        // updates painting position/layer/rotation
        vAnimation.setPosition(mObjectModel.getPosition());
        vAnimation.setRotation(mObjectModel.getRotation());
        
        // move object to fitting Tile
        Tile vLevelTile = mLevel.getTile(mObjectModel.getCenter(), mObjectModel.getLayer());
        if (mLevelTile != vLevelTile && vLevelTile != null) 
        {
            // play tile-specific music
            if (vLevelTile.getPlaylist() != null)
                MusicEngine.getInstance().playMusic(vLevelTile.getPlaylist(), true, true);

            // remove object from privous tile
            if (mLevelTile != null)
            {
                if (mState != ObjectState.DIEING) 
                {
                    mLevelTile.delRigidBody(this);
                }
                mLevelTile.delAnimation(vAnimation);
            }
            
            // add object to next tile
            mLevelTile = vLevelTile;
            if (mState != ObjectState.DIEING) 
            {
                mLevelTile.addRigidBody(this);
            }
            mLevelTile.addAnimation(vAnimation);
        }
    }
    
    /**
     * Removes the object from the level and creates a death animation if 
     * available.
     */
    public synchronized void destroy () 
    {
        mState = ObjectState.DIEING;
    }
    
    /**
     * Checks if the level-object is still alive or should be removed from the
     * level's object-list. 
     * @return "true" if alive
     */
    public synchronized boolean isAlive() 
    {
        return (mState != ObjectState.DIEING);
    }

    /**
     * Gets the physical model of the object. 
     * @return a reference to the RigidBody-instance of the object.
     */
    public synchronized PhysicalBody getObjectModel () 
    {
        return mObjectModel;
    }
    
    @Override
    public LevelObject clone ()
    {
        // copy object
        LevelObject vCopy = LevelObjectFactory.create(mObjectType);
        for (int i = 0; i < mProperties.size(); i++)
            vCopy.setProperty(mProperties.get(i), mPropertyValues.get(i));

        // copy rigid body
        PhysicalBody vRigidCopy = mObjectModel.clone();

        // connect object-copy with rigid-body-copy
        vRigidCopy.setLevelObject(vCopy);
        vRigidCopy.initialize();
        vCopy.setRigidBody(vRigidCopy);
        
        return vCopy;
    }

    /**
     * Sets a property to a particular value.
     * @param property property name
     * @param value property value
     */
    public void set (String property, String value)
    {
        mProperties.add(property);
        mPropertyValues.add(value);
        setProperty(property, value);
    }
    
    /**
     * Finds the current animation of the object due to its state. 
     * @return the current object animation.
     */
    public abstract Animation getAnimation ();
    
    /**
     * Sets one of the object's properties. 
     * @param property property name
     * @param value value for the property
     */
    public abstract void setProperty (String property, String value);
    
    /**
     * Initializes the LevelObject. This method is called after all of the 
     * properties are set. 
     */
    public abstract void initialize ();

    /** 
     * This method defines effects happening after a collision with another 
     * object or with a texture. If a collision with a texture occured, the
     * obj-argument is null.
     * 
     * @param obj reference to the other rigid body/object
     */
    public abstract void onCollision (LevelObject obj);    
    
}