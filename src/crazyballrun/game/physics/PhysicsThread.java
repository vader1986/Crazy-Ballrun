/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.physics;

import crazyballrun.game.GameConstants;
import crazyballrun.game.GameLogger;
import crazyballrun.game.level.Level;
import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.level.Tile;
import crazyballrun.game.utilities.Vector2D;
import java.util.Iterator;

/**
 * The PhysicsThread handles all kind of matters related to phyics, like collision
 * detection of objects with textures and objects with other objects. Additionally,
 * it updates speed, position and orientation of rigid (and soft) bodies.
 * 
 * @author Timm Hoffmeister
 */
public class PhysicsThread extends Thread {
    
    /**
     * If 'true' the PhysicsThread waits for a notification. Initially the 
     * physics thread is not needed until the first level has started, consequently
     * it's paused. 
     */
    private boolean mIsPaused = true;
    
    /**
     * If 'true' the PhysicsThread is running otherwise it is closed soon.
     */
    private boolean mIsRunning = true;
    
    /**
     * Defines the state of the ample. As long as the ample is active, no object
     * may move. 
     */
    private boolean mAmpleActive = false;
    
    /**
     * Reference to the level.
     */
    private Level mLevel = null;
    
    /**
     * Constructor of PhysicsThread. 
     */
    public PhysicsThread () {
        
    }

    /**
     * Sets the level-reference for the PhysicsThread. 
     * @param level reference to the level
     */
    public void setLevel (Level level) {
        mLevel = level;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // FPS Calculation
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Number of frames passed. 
     */
    private int mFrames = 0;

    /**
     * Frames-per-second. 
     */
    private int mFPS = 0;
    
    /**
     * Timestamp of current frame. 
     */
    private long mCurrentFrame = 0;

    /**
     * Timestamp of the first frame.
     */
    private long mFirstFrame = 0;

    /**
     * Timestamp of the previous frame. 
     */
    private long mLastFrame = 0;
    
    /**
     * Method initializing the FPS-rate.
     */
    private void initializeFPS()
    {
        mFrames = 0;
        mFirstFrame = System.currentTimeMillis();
        mFPS = 65;
    }
    
    /**
     * Method calculating the frames-per-second. If the frame-rate exceed the
     * maximum frame rate this method sleeps what's left from 1 second.
     */
    private void calcuateFPS()
    {
        mFrames++;
        mCurrentFrame = System.currentTimeMillis();

        // Estimate time to keep maximum framerate:
        // f = mFrames, t = current time, t* = time of first frame, max = max fps
        // 
        // This equation has to hold: f * 1000 / (t - t*) = max
        // => search for a k = (t - t*) which fulfills the equation
        // => k = f * 1000 / max
        // 
        // We already used (t - t*) time, so we need to sleep "k - (t - t*)" ms 
        // to fulfill the equation. Only sleep if the estimated frame-rate is 
        // larger than max: 
        // e = f * 1000 / (t - t*)
        int passed = (int) (mCurrentFrame - mLastFrame);
        int sleeptime = 0;
        
        if (passed > 0)
        {
            if ((1000 / passed) > GameConstants.MAX_FPS)
            {
                sleeptime = 1000 / GameConstants.MAX_FPS - passed;
            }
        }
        else
        {
            sleeptime = 1000 / GameConstants.MAX_FPS;
        }
        
        if(mCurrentFrame > mFirstFrame + 1000)
        {
            mFirstFrame = mCurrentFrame;
            mFPS = mFrames;
            mFrames = 0;
        }

        try
        {
            if (sleeptime > 0)
                Thread.sleep(sleeptime);
        }
        catch (InterruptedException e)
        {
            GameLogger.log(e);
        }
        mLastFrame = mCurrentFrame;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Thread access methods
    ////////////////////////////////////////////////////////////////////////////    
        
    /**
     * Pauses the PhysicsThread.
     */
    public synchronized void doPause() {
        mIsPaused = true;
    }

    /**
     * Finds out if the PhysicsThread is paused or not. 
     * @return if the PhysicsThread is paused or not. 
     */
    public synchronized boolean isPaused () {
        return mIsPaused;
    }

    /**
     * Continues the PhysicsThread after pausing. 
     */
    public synchronized void doContinue() {
        mIsPaused = false;
        this.notify();
    }

    /**
     * Exits the PhysicsThread. 
     */
    public synchronized void doExit() {
        mIsRunning = false;
    }
    
    /**
     * Finds out if the PhysicsThread is still running.
     * @return if the PhysicsThread is still runnning or not. 
     */
    public synchronized boolean isRunning () {
        return mIsRunning;
    }
    
    /**
     * Activates or deactivates the ample mode. 
     * @param state state of the ample
     */
    public synchronized void setAmple (boolean state)
    {
        mAmpleActive = state;
    }

    /**
     * Gets the state of the ample
     * @return ample state
     */
    private synchronized boolean getAmple ()
    {
        return mAmpleActive;
    }
        
    /**
     * Checks for collision of the object with another object in the level. The
     * method checks each tile around the tile the object is placed on (also one
     * layer above and under the object, if there's a layer-transition).
     * @param obj reference to the object
     * @return reference to the colliding object
     */
    private LevelObject collisionWithObject (LevelObject obj) 
    {
        Vector2D vPosition = obj.getObjectModel().getCenter();
        int vLayer = obj.getObjectModel().getLayer();
        
        // possible collision on layer-transitions
        for (int l = -1; l < 2; l++)
        {
            // possible collision 1 texture in all directions
            for (int x = -1; x < 2; x++) 
            {
                for (int y = -1; y < 2; y++)
                {
                    // get tile for collision detection
                    Tile vTile = mLevel.getTile(vPosition.x + x, vPosition.y + y, vLayer + l);
                    
                    // skip impossible collisions
                    if ( (vTile != null) && (l == 0 ||  
                         (l < 0 && vTile.hasNextLayer()) ||
                         (l > 0 && vTile.hasPreviousLayer()) ))
                    {
                        // detect possible object-object collisions on the selected tile
                        LevelObject vCollisionObject = vTile.collision(obj.getObjectModel());

                        if (vCollisionObject != null)
                            return vCollisionObject;

                    }
                }
            }        
        }
        return null;
    }
    
    @Override
    public void run()
    {
        int     stepsize    = 0;
        double  dt          = 0.0;
        boolean ample       = false;
        
        initializeFPS();
        
        // Main Physics Loop
        while (isRunning()) 
        {            
            // Pause the PhysicsThread
            if (isPaused())
            {
                // update game-time
                if (mLevel != null) {
                    mLevel.getStatistics().update();
                }

                // wait for resume
                boolean vWait = false;
                while (!vWait) {
                    try {
                        synchronized (this) {
                            this.wait();
                        }
                        vWait = true;
                    } catch (InterruptedException e) {
                        vWait = false;
                    }
                }

                initializeFPS();

                // resume game
                if (mLevel != null) {
                    mLevel.getStatistics().resume();
                }
            }

            // check ample state
            ample = getAmple(); 
            
            // Calculate timestep-size dt
            calcuateFPS();
                        
            dt = 1.0 / (double) mFPS;
            stepsize = (int) (dt * 1000.0);
            
            // this avoids problems at the beginning of the game
            if (dt > 1.0) continue;
            
            // perform PhysicsThread-Tasks
            if (mLevel != null)
            {
                // update game-time
                mLevel.getStatistics().update();

                // check is game has finished (player reached goal)
                if (mLevel.getStatistics().finished())
                {
                    // TODO: change to GUI -> show statistics-GUIFrame
                }
                
                // Clone current object-list
                Iterator<LevelObject> vIter = mLevel.getObjectList().iterator();
                LevelObject vObject;
                
                // Iterate through all objects
                while (vIter.hasNext())
                {
                    vObject = vIter.next();
                    
                    // Remove dead objects
                    if (!vObject.isAlive())
                    {
                        vIter.remove();
                        continue;
                    }
                    
                    if (!ample)
                    {
                        // Apply internal forces/torques and effects
                        vObject.control(stepsize, dt);

                        // Get ground-properties due to the object's position
                        Vector2D vPosition = vObject.getObjectModel().getCenter();
                        int vLayer = vObject.getObjectModel().getLayer();
                        CollisionTexture.GroundProperty vGround = mLevel.getGroundProperty(vPosition, vLayer);
                        double friction =       vObject.getObjectModel().isRigidBody() ? CollisionTexture.getFriction(vGround)    : 0.0;
                        double centrifugal =    vObject.getObjectModel().isRigidBody() ? CollisionTexture.getCentrifugal(vGround) : 1.0;

                        // Layer-change of object (up/down in z-direction)
                        if (vGround == CollisionTexture.GroundProperty.NEXT_LAYER)
                            vObject.getObjectModel().nextLayer();
                        if (vGround == CollisionTexture.GroundProperty.PREVIOUS_LAYER)
                            vObject.getObjectModel().previousLayer();

                        // Update velocity, position and orientation of the physical model
                        vObject.getObjectModel().update(dt, friction, centrifugal);
                        vObject.getObjectModel().updateData();

                        // Object collisions
                        LevelObject vCollisionObject = collisionWithObject(vObject);
                        if (vCollisionObject != null) {
                            vObject.onCollision(vCollisionObject);
                            vObject.getObjectModel().collisionReactionObject(dt);
                            vObject.getObjectModel().updateData();
                        }
                        // Texture collisions
                        else
                        {
                            Vector2D vCollisionPoint = vObject.getObjectModel().isColliding(mLevel);
                            if (vCollisionPoint != null)
                            {
                                vObject.onCollision(null);
                                vObject.getObjectModel().collisionReaction(dt, vCollisionPoint);
                                vObject.getObjectModel().updateData();
                            }
                        }
                    }
                    
                    // Update the object-status (e.g. move to next tile)
                    vObject.update();
                }
            }
            else
            {
                try
                {
                    sleep(200);
                }
                catch (InterruptedException e)
                {
                    return;
                }
            }
        }        
    }    
    
}
