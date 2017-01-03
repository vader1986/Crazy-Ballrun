/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.physics;

import crazyballrun.game.utilities.Vector2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * The CollisionMap represents the collision data of a texture. 
 * 
 * @author Timm Hoffmeister
 */
public class CollisionTexture {

    /**
     * Defines the result of collisions between rigid bodies and a particular
     * color of the collision texture image. In other words: it's the kind of 
     * ground the texture color represents. 
     */
    public enum GroundProperty {
        /**
         * default friction and coriolis values
         */
        DEFAULT,
        /**
         * collision with rigid bodies
         */
        WALL,
        /**
         * slows down the rigid body by increasing the friction
         */
        HIGH_FRICTION,
        /**
         * friction is set to "0" while the rigid body is here
         */
        LOW_FRICTION,
        /**
         * zero-friction and high coriolis force
         */
        ICE_GROUND,
        /**
         * very high friction and no coriolis force, in addition
         * the rigid body is painted half transparent
         */
        WATER_GROUND,
        /**
         * changes to the next layer if the former color has been "PREVIOUS_LAYER"
         */
        NEXT_LAYER,     
        /**
         * changes to the previous layer if the former color has been "NEXT_LAYER"
         */
        PREVIOUS_LAYER
    };
    
    /**
     * Calculates a centrifugal-force value for a particular ground. Low centrigual
     * force means there is high centrifugal effect (for performance reasons, to
     * avoid "1 - centrifugal" calculation in the physics calculation).
     * @param ground ground properties
     * @return centrifugal force
     */    
    public static double getCentrifugal (GroundProperty ground) {
        switch (ground)
        {
            case DEFAULT:
                return 0.9;
            case HIGH_FRICTION:
                return 0.9;
            case LOW_FRICTION:
                return 0.9;
            case ICE_GROUND:
                return 0.1;
            case WATER_GROUND:
                return 0.9;
            case NEXT_LAYER:
                return 0.9;
            case PREVIOUS_LAYER:
                return 0.9;
            default:
                return 0.0;
        }
    }
    
    /**
     * Calculates a friction-force value for a particular ground.
     * @param ground ground properties
     * @return friction force
     */
    public static double getFriction (GroundProperty ground) {
        switch (ground)
        {
            case DEFAULT:
                return 0.4;
            case HIGH_FRICTION:
                return 1.0;
            case LOW_FRICTION:
                return 0.0;
            case ICE_GROUND:
                return 0.0;
            case WATER_GROUND:
                return 1.0;
            case NEXT_LAYER:
                return 0.4;
            case PREVIOUS_LAYER:
                return 0.4;
            default:
                return 0.0;
        }
    }
    
    /**
     * Maps RGB-Color-Values to GroundProperties. 
     */
    private HashMap<Integer, GroundProperty> mCollisionColorMap = null;
        
    /**
     * Image containing the collision data of the texture. 
     */
    private BufferedImage mCollisionTexture = null;
        
    /**
     * Constructor of CollisionMap. Creates the color-ground-mapping.
     */
    public CollisionTexture () {
        mCollisionColorMap = new HashMap<Integer, GroundProperty>();
        mCollisionColorMap.put(Color.BLACK.getRGB(), GroundProperty.DEFAULT);
        mCollisionColorMap.put(Color.RED.getRGB(), GroundProperty.WALL);
        mCollisionColorMap.put(Color.GREEN.getRGB(), GroundProperty.HIGH_FRICTION);
        mCollisionColorMap.put(Color.GRAY.getRGB(), GroundProperty.LOW_FRICTION);
        mCollisionColorMap.put(Color.WHITE.getRGB(), GroundProperty.ICE_GROUND);
        mCollisionColorMap.put(Color.BLUE.getRGB(), GroundProperty.WATER_GROUND);
        mCollisionColorMap.put(Color.YELLOW.getRGB(), GroundProperty.NEXT_LAYER);
        mCollisionColorMap.put(Color.PINK.getRGB(), GroundProperty.PREVIOUS_LAYER);
    }

    /**
     * Compares two color-values. 
     * @param c1 first color
     * @param c2 second color
     * @return "true" if the colors are equal
     */
    private boolean equalColor (Color c1, Color c2)
    {
        return (c1.getRed() == c2.getRed() &&
                c1.getGreen() == c2.getGreen() &&
                c1.getBlue() == c2.getBlue());
    }
    
    /**
     * Sets the collision texture image for the collision map.
     * @param image BufferedImage representing the collision data
     */
    public void setCollisionTexture (BufferedImage image) {
        mCollisionTexture = image;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int vColorRGB = image.getRGB(x,y);
                Color vColor = new Color(vColorRGB, true);
                if (!equalColor(vColor, Color.BLACK) &&
                    !equalColor(vColor, Color.RED) && 
                    !equalColor(vColor, Color.GREEN) &&
                    !equalColor(vColor, Color.GRAY) &&
                    !equalColor(vColor, Color.WHITE) &&
                    !equalColor(vColor, Color.BLUE) &&
                    !equalColor(vColor, Color.YELLOW) &&
                    !equalColor(vColor, Color.PINK))
                {
                    mCollisionTexture.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
    }
    
    /**
     * Gets the ground property of a particular point on the texture.
     * @param point specified point on the texture (x in [0,1], y in [0,1])
     * @return the ground property of the texture at the specified point. 
     */ 
    public GroundProperty getGroundProperty (Vector2D point) {
        return mCollisionColorMap.get(mCollisionTexture.getRGB((int)(point.x * mCollisionTexture.getWidth()), 
                                                               (int)(point.y * mCollisionTexture.getHeight())));
    }
    
}
