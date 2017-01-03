/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.graphics;

import crazyballrun.game.GameConstants;
import crazyballrun.game.utilities.Vector2D;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * The Animation class is used to render a series of picture in a given frequency.
 * It offers functionality for rendering (paint-method), creation (initializes
 * the animation with several BufferedImage-instances) and configuration (to set
 * lifetime, position, size, animation timesteps, etc.).
 * 
 * @author Timm Hoffmeister
 */
public class Animation {
    
    /**
     * List of images for the animation. 
     */
    private BufferedImage [] mImages = null;

    /**
     * Lifetime of the animation (negative values mean infinity lifetime). 
     * Lifetime is measured in miliseconds. 
     */
    private long mLifeTime = -1;

    /**
     * Position of the animation in the current level. 
     */
    private Vector2D mPosition = null;

    /**
     * Rotation of the animation. 
     */
    private double mRotation = 0;

    /**
     * Time step size of the animation. After 'mFrequency' ms the current
     * picture of the animation changes.
     */
    private int mFrequency = 100;

    /**
     * Last timestamp of the animation in miliseconds, since the picture has
     * changed. 
     */
    private int mTimeStamp = 0;
    
    /**
     * Index of the current animation picture.
     */
    private int mCurrentPicture = 0;
    
    /**
     * Offset from the position to the center point of the animation.
     */
    private Vector2D mOffset = null;
    
    /**
     * Play the animation once only (e.g. for death animations).
     */
    private boolean mPlayOnce = false;
    
    /**
     * The animation has been played already.
     */
    private boolean mPlayed = false;
    
    /**
     * Constructor of Animation.
     */
    public Animation () {
        
    }

    /**
     * Constructor of Animation.
     * @param images reference to the animation's pictures
     */
    public Animation (BufferedImage [] images) {
        setImages(images);
    }

    /**
     * Constructor of Animation.
     * @param images reference to the animation's pictures
     * @param lifetime lifetime of the animation in ms (miliseconds)
     */
    public Animation (BufferedImage [] images, int lifetime) {
        setImages(images);
        mLifeTime = lifetime;
    }
    
    /**
     * Constructor of Animation.
     * @param images reference to the animation's pictures (every image has the same size!)
     * @param lifetime lifetime of the animation in ms (miliseconds)
     * @param position position of the animation
     * @param rotation rotation of the animation
     */
    public Animation (BufferedImage [] images, int lifetime, Vector2D position, double rotation) {
        setImages(images);
        mLifeTime = lifetime;
        mPosition = position;
        mRotation = rotation;
    }
    
    /**
     * Forces the animation to be played once only.
     * @param once play animation just once or not
     */
    public synchronized void setPlayOnce (boolean once) {
        mPlayOnce = once;
    }            
    
    /**
     * Sets the current position of the animation.
     * @param pos new position for the animation
     */
    public synchronized void setPosition (Vector2D pos) {
        mPosition = pos;
    }

    /**
     * Sets the current rotation of the animation.
     * @param rot new rotation angle for the animation
     */
    public synchronized void setRotation (double rot) {
        mRotation = rot;
    }

    /**
     * Sets the time step size for the animation, after how many miliseconds to
     * change picture. 
     * @param stepsize time step size [ms]
     */
    public synchronized void setFrequency (int stepsize) {
        mFrequency = stepsize;
    }

    /**
     * Sets the animation's remaining lifetime in miliseconds.
     * @param lifetime remaining lifetime of the animation in miliseconds
     */
    public synchronized void setLifeTime (long lifetime) {
        mLifeTime = lifetime;
    }

    /**
     * Destroys the animation (remove it from the rendering context by setting
     * lifetime to zero). 
     */
    public void kill () {
        setLifeTime(0);
    }
    
    /**
     * Sets the images of the animation (every image has the same size!). 
     * @param images array of BufferedImages
     */
    public final synchronized void setImages (BufferedImage [] images) {
        mImages = images;
        mOffset = new Vector2D(((double)images[0].getWidth())  / ((double)GameConstants.TEXTURE_SIZE * 2), 
                               ((double)images[0].getHeight()) / ((double)GameConstants.TEXTURE_SIZE * 2));
    }

    /**
     * Method for the RenderThread to know when to delete the animation.
     * @return remaining lifetime of the animation in miliseconds. 
     */
    public synchronized long getLifeTime () {
        return mLifeTime;
    }

    /**
     * Checks if the animation is still alive.
     * @return "true" if the animation is still alive
     */
    public synchronized boolean isAlive () {
        return mLifeTime != 0;
    } 

    /**
     * Checks if the animation is dead and should be removed.
     * @return "true" if the animation is not alive anymore
     */
    public synchronized boolean isDead () {
        return mLifeTime == 0;
    } 
    
    /**
     * Renders the animation into the given render context (g).
     * @param c reference to the camera where to paint the animation
     * @param g reference to the render context
     * @param f reference to the frame
     * @param timestep time passed since the last call (estimation)
     */
    public synchronized void paint (Camera c, Graphics2D g, Frame f, int timestep) {

        if (mPlayed && mPlayOnce)
            return;
        
        // iterate through pictures for animation
        if (mImages.length > 1)
        {
            mTimeStamp += timestep;
            if (mTimeStamp > mFrequency) {
                mTimeStamp -= mFrequency;
                if (mLifeTime > 0)
                    mLifeTime = Math.max(0, mLifeTime - timestep);
                mCurrentPicture = (mCurrentPicture + 1) % mImages.length; 
                if (mPlayOnce && mCurrentPicture == 0)
                {
                    mCurrentPicture = mImages.length - 1;
                    mPlayed = true;
                    mLifeTime = 0;
                }
            }
        }
        
        // calculate rendering position and rotation axis
        Vector2D vRender = new Vector2D(mPosition);
        vRender.sub(c.getRenderPosition());
        Vector2D vCenter = new Vector2D(vRender);
        vCenter.add(mOffset);
        
        // rotate and render current picture
        g.rotate(-mRotation % (Math.PI * 2), vCenter.getX(), vCenter.getY());
        g.drawImage(mImages[mCurrentPicture], (int)vRender.getX(), (int)vRender.getY(), f);
        g.rotate(mRotation % (Math.PI * 2), vCenter.getX(), vCenter.getY());
    }
        
}
