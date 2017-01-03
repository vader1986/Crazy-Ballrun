/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level;

import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.graphics.Animation;
import crazyballrun.game.graphics.Camera;
import crazyballrun.game.physics.CollisionTexture;
import crazyballrun.game.physics.bodies.PhysicalBody;
import crazyballrun.game.utilities.Vector2D;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The Tile describes a texture visually and also physically (for collision
 * detection and ground properties). Thus, it contains one image-file (or series
 * of image files for animated texture) and one collision map (also an image file
 * with ground-specific colors). The Tile also describes the accustics of 
 * the ground by a tile-specific sound file. 
 * 
 * In addition the Tile has a list of Animation-instances which are currently
 * placed on it. Another list contains all RigidBody-instances used by the 
 * PhysicsEngine. Animations and rigid bodies are usually added and removed by 
 * the corresponding LevelObject. Animations are automatically removed from the
 * list by the Tile if their lifetime equals zero. Only ONE Tile may 
 * contain a particular RigidBody or Animation. 
 * 
 * For Camera-instances the Tile provides a paint-function which renders the
 * animations of the animation-list with respect to the camera-position. In this
 * paint-method an animation may be removed when lifetime is zero. 
 * 
 * @author Timm Hoffmeister
 */
public class Tile {

    /**
     * Collision data for the tile. 
     */
    private CollisionTexture mCollisionTexture = null;

    /**
     * Animation for rendering (may be a single picture if static). 
     */
    private Animation mTexture = null;

    /**
     * List of all animations placed on the level tile. 
     */
    private LinkedList<Animation> mAnimationList = new LinkedList<Animation>();

    /**
     * List of all rigid bodies placed on the level tile.
     */
    private LinkedList<LevelObject> mRigidBodyList = new LinkedList<LevelObject>();

    /**
     * Lock for the Animation-list of the tile.
     */
    private final Object mLockAnimation = new Object();

    /**
     * Lock for the RigidBody-list of the tile.
     */
    private final Object mLockRigidBody = new Object();

    /**
     * Music-Playlist fitting to the Tile.
     */
    private String [] mSoundFiles = null;

    /**
     * Contains collision points which lead to the next layer. 
     */
    private boolean mHasNextLayer = false;
    
    /**
     * Contains collision points which lead to the previous layer. 
     */
    private boolean mHasPreviousLayer = false;
    
    /**
     * Creates an instance of Tile.
     * @param collTexture reference to the collision texture
     * @param texture reference to the (animated) texture
     * @param music filenames for the music-files
     */
    public Tile (CollisionTexture collTexture, Animation texture, String [] music) 
    {
        this.mCollisionTexture = collTexture;
        this.mTexture = texture;
        this.mSoundFiles = music;
    }
    
    /**
     * Gets a reference to the tile's music playlist. 
     * @return tile's music playlist
     */
    public String [] getPlaylist() 
    {
        return mSoundFiles;
    }
    
    /**
     * Adds one animation to the animation list of the tile. 
     * @param animation reference to the animation
     */
    public void addAnimation (Animation animation) 
    {
        synchronized(mLockAnimation)
        {
            mAnimationList.add(animation);
        }
    }
    
    /**
     * Removes one animation from the animation list of the tile.
     * @param animation reference to the animation
     */
    public void delAnimation (Animation animation)
    {
        synchronized(mLockAnimation)
        {
            mAnimationList.remove(animation);
        }
    }
    
    /**
     * Adds a LevelObject to the list of the tile.
     * @param body reference to LevelObject
     */
    public void addRigidBody (LevelObject body) 
    {
        synchronized(mLockRigidBody)
        {
            mRigidBodyList.add(body);
        }
    }

    /**
     * Removes a LevelObject from the list of the tile.
     * @param body reference to the LevelObject
     */
    public void delRigidBody (LevelObject body) 
    {
        synchronized(mLockRigidBody)
        {
            mRigidBodyList.remove(body);
        }
    }

    /**
     * Sets the hasNextLayer-Property.
     * @param next collision texture contains a path to the next layer
     */
    public void setNextLayer (boolean next) 
    {
        mHasNextLayer = next;
    }

    /**
     * Sets the hasNextLayer-Property.
     * @param pre collision texture contains a path to the previous layer
     */
    public void setPreviousLayer (boolean pre) 
    {
        mHasPreviousLayer = pre;
    }
    
    /**
     * Checks if the tile's collision texture has a point which leads to the 
     * next layer. 
     * @return "true" if the collision texture leads to the next layer
     */
    public boolean hasNextLayer () 
    {
        return mHasNextLayer;
    }
    
    /**
     * Checks if the tile's collision texture has a point which leads to the 
     * previous layer. 
     * @return "true" if the collision texture leads to the previous layer
     */
    public boolean hasPreviousLayer () 
    {
        return mHasPreviousLayer;
    }
    
    /**
     * Gets the ground properties at a particular point of the tile. 
     * @param point specific point
     * @return the ground property
     */
    public CollisionTexture.GroundProperty getGroundProperty (Vector2D point) 
    {
        if (mCollisionTexture != null)
            return mCollisionTexture.getGroundProperty(point);
        return CollisionTexture.GroundProperty.WALL;
    }
    
    /**
     * Checks if any rigid body of this tile collides with the given rigid body.
     * @param body reference to the rigid body
     * @return LevelObject-reference of the colliding object (or null if no collision)
     */
    public LevelObject collision (PhysicalBody body) {
        LevelObject vReturn = null;
        synchronized(mLockRigidBody)
        {
            for (LevelObject vRigidBody : mRigidBodyList) 
            {
                if (vRigidBody.getObjectModel() != body && 
                    vRigidBody.getObjectModel().isRigidBody() &&
                    body.isColliding(vRigidBody.getObjectModel()))
                {
                    vReturn = vRigidBody;
                    break;
                }
            }
        }
        return vReturn;
    }
    
    /**
     * Paints the Tile's texture.
     * @param camera reference to the camera
     * @param g reference to the Graphics-instance
     * @param f reference to the frame
     * @param timestep passed time since the last render-frame
     */
    public void paint (Camera camera, Graphics2D g, Frame f, int timestep) {
        if (mTexture != null)
            mTexture.paint(camera, g, f, timestep);
    }
    
    /**
     * Paints the Tile's animations (which are placed on it).
     * @param camera reference to the camera
     * @param g reference to the Graphics-instance
     * @param f reference to the frame
     * @param timestep passed time since the last render-frame
     */    
    public void paintContent (Camera camera, Graphics2D g, Frame f, int timestep) 
    {
        synchronized(mLockAnimation)
        {
            Iterator<Animation> vIter = mAnimationList.iterator();
            while (vIter.hasNext()) 
            {
                Animation vAnimation = vIter.next();
                vAnimation.paint(camera, g, f, timestep);
                if (vAnimation.isDead()) 
                    vIter.remove();
            }
        } 
    }
}
