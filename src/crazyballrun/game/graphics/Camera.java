/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.graphics;

import crazyballrun.game.GameConstants;
import crazyballrun.game.level.Level;
import crazyballrun.game.level.Tile;
import crazyballrun.game.utilities.Vector2D;
import java.awt.AlphaComposite;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * The Camera-class has a position, field of view and a rectangle which specifies
 * the display of the camera-content on the screen. The camera has a refernce to
 * the level and is responsible for rendering just a part of the level (frustum 
 * culling). 
 * @author Timm Hoffmeister
 */
public class Camera {

    /**
     * How much is one pixel in texture-coordinates?
     */
    private static final double sPixelInTextures = 1 / ((double)GameConstants.TEXTURE_SIZE);
    
    /**
     * Rectangle describing position and size of the camera-display on the screen.
     * The display is of the same size as the field of view. However the field
     * of view's position may change (mPosition!) and the display remains in the
     * same position on the screen.
     */
    private Rectangle mDisplay = null; 

    /**
     * Offset from the camera position to its center.
     */
    private Vector2D mOffset = null;
    
    /**
     * Number of tiles the camera contains in x-direction.
     */
    private int mTileWidth = 0;
    
    /**
     * Number of tiles the camera contains in y-direction.
     */
    private int mTileHeight = 0;
    
    /**
     * Position of the camera on the map (in textures).
     */
    private Vector2D mPosition = null;

    /**
     * Render-Position of the camera (including display-offset).
     */
    private Vector2D mRenderPosition = null;
    
    /**
     * Layer on which the camera is positioned in the level. 
     */
    private int mLayer = 0;
    
    /**
     * Reference to the level in which the camera is placed. 
     */
    private Level mLevel = null;
    
    /**
     * Specifies if the camera has been created yet. 
     */
    private boolean mCreated = false;
    
    /**
     * Distance from frame to the camera-center.
     */
    private double mDistanceToCenter = 0.0;

    /**
     * Different transparency-values depending on distance of textures on higher
     * layer than the camera-layer. 
     */
    private AlphaComposite [] mAlphaComposites = new AlphaComposite[30];
    
    /**
     * Constructor for Camera. 
     */
    public Camera () {
        mPosition = new Vector2D();
        mRenderPosition = new Vector2D();
        for (int i = 0; i < mAlphaComposites.length; i++)
            mAlphaComposites[i] = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ( ((float)i) / (float)(mAlphaComposites.length-1)) );
    }

    /**
     * Creates a camera inside the given level.
     * @param level reference to the level in which the camera should be placed
     */
    public void create (Level level) {
        if (!mCreated) {
            mLevel = level;
        }
        else 
            throw new IllegalStateException("The camera has been created already.");
    }

    /**
     * Positions the camera in the level (in textures).
     * @param p new camera position
     * @param layer layer where the camera is positioned
     */
    public void setCameraCenter (Vector2D p, int layer)
    {
        p.sub(mOffset);
        setPosition(p, layer);
    }
    
    /**
     * Positions the camera in the level (in textures).
     * @param p new camera position
     * @param layer layer where the camera is positioned
     */
    public synchronized void setPosition (Vector2D p, int layer) {
        mPosition = p;
        mRenderPosition.x = p.x - mDisplay.x * sPixelInTextures;
        mRenderPosition.y = p.y - mDisplay.y * sPixelInTextures;
        mLayer = layer;
    }
        
    /**
     * Sets the current layer where the camera is positioned in the level. 
     * @param layer current layer of the camera
     */
    public synchronized void setLayer (int layer) {
        mLayer = layer;
    }
    
    /**
     * Sets the position of the camera inside of the level (in textures).
     * @param p new camera position
     */
    public synchronized void setPosition (Vector2D p) {
        mPosition = p;
    }
    
    /**
     * Gets the position of the camera (in textures).
     * @return the camera position in the level. 
     */
    public synchronized Vector2D getPosition () {
        return mPosition;
    }

    /**
     * Calculates the render-position of the camera (including the display-offset
     * of the camera).
     * @return rendering-position of the camera
     */
    public synchronized Vector2D getRenderPosition () {
        return mRenderPosition;
    }
    
    /**
     * Creates a display for the camera on the screen. The display shows what
     * the camera tracks in the level (textures, objects, ...). 
     * @param x x-coordinate of the display position on the screen (in pixels)
     * @param y y-coordinate of the display position on the screen (in pixels)
     * @param width display width on the screen (in pixels)
     * @param height display height on the screen (in pixels)
     */
    public void setDisplay (int x, int y, int width, int height) {
        mDisplay = new Rectangle(x, y, width, height);
        mTileWidth = width / GameConstants.TEXTURE_SIZE + 2;
        mTileHeight = height / GameConstants.TEXTURE_SIZE + 2;
        mOffset = new Vector2D();
        mOffset.x = (double) width / (double) (2 * GameConstants.TEXTURE_SIZE);
        mOffset.y = (double) height / (double) (2 * GameConstants.TEXTURE_SIZE);
        mDistanceToCenter = mOffset.length();
    }
    
    /**
     * Gets the display-area of the camera. 
     * @return camera's display
     */
    public Rectangle getDisplay () {
        return mDisplay;
    }
    
    /**
     * Paints whatever is in the camera's field of view. 
     * @param g reference to the Graphics-instance
     * @param f reference to the Frame-instance
     * @param timestep passed miliseconds since the last rendered frame
     */
    public synchronized void paint (Graphics2D g, Frame f, int timestep) {

        // Creates its own Camera-Rendering-Context
        Graphics2D vGraphics = (Graphics2D) g.create();
        vGraphics.clip(mDisplay);

        for (int layer = 0; layer < mLevel.getLayers(); layer++)
        {
            // paint textures inside field of view
            for (int x = (int)mPosition.x; x < (int)mPosition.x + mTileWidth && x < mLevel.getWidth(); x++) 
            {
                for (int y = (int)mPosition.y; y < (int)mPosition.y + mTileHeight && y < mLevel.getHeight(); y++)
                {
                    Tile vTile = null;
                    if (x >= 0 && y >= 0)
                    {
                        if (layer > mLayer)
                        {
                            double dx = (x + 0.5 - mPosition.x - mOffset.x);
                            double dy = (y + 0.5 - mPosition.y - mOffset.y);
                            int vDist = (int)(Math.min((float)(Math.sqrt(dx * dx + dy * dy) / mDistanceToCenter), 1.0f) * (mAlphaComposites.length - 1) ) ;
                            vGraphics.setComposite(mAlphaComposites[ vDist ]);
                        }
                        vTile = mLevel.getTile(x, y, layer);
                    }
                    if (vTile != null) vTile.paint(this, vGraphics, f, timestep);
                }
            }
            
            // paint objects inside current field of view
            for (int x = (int)mPosition.x; x < (int)mPosition.x + mTileWidth && x < mLevel.getWidth(); x++) 
            {
                for (int y = (int)mPosition.y; y < (int)mPosition.y + mTileHeight && y < mLevel.getHeight(); y++)
                {
                    Tile vTile = null;
                    if (x >= 0 && y >= 0)
                    {
                        if (layer > mLayer)
                        {
                            double dx = (x + 0.5 - mPosition.x - mOffset.x);
                            double dy = (y + 0.5 - mPosition.y - mOffset.y);
                            int vDist = (int)(Math.min((float)(Math.sqrt(dx * dx + dy * dy) / mDistanceToCenter), 1.0f) * (mAlphaComposites.length - 1) ) ;
                            vGraphics.setComposite(mAlphaComposites[ vDist ]);
                        }
                        vTile = mLevel.getTile(x, y, layer);
                    }
                    if (vTile != null) vTile.paintContent(this, vGraphics, f, timestep);
                }
            }
        }
        
    }
    
}
