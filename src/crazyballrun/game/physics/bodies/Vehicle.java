/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.physics.bodies;

import crazyballrun.game.GameConstants;
import crazyballrun.game.level.Level;
import crazyballrun.game.physics.CollisionTexture;
import crazyballrun.game.utilities.Vector2D;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * The Vehicle class extens the RigidBody class by specifying collision and 
 * collision reactions as well as implementing explicit euler integration method
 * for updating position, velocity and rotation of the vehicle.
 * 
 * @author Timm Hoffmeister
 */
public class Vehicle extends PhysicalBody {

    /**
     * Texture size in pixels. 
     */
    private static final int sTextureSize = GameConstants.TEXTURE_SIZE;

    /**
     * One pixel in texture-measurement. 
     */
    private static final double sPixelInTexture = 1.0 / (double) sTextureSize;
    
    /**
     * Number of inner collision points (between the polygon-points, to make
     * collision more accurate).
     */
    private static final int N = GameConstants.INNER_COLLISION_POINTS;
            
    /**
     * Stores the original polygon of the vehicle without any rotation (looking up). 
     */
    private Polygon mOriginalPolygon = new Polygon();

    /**
     * Stores the current polygonial structure with applied rotation of the vehicle.
     */
    private Polygon mCurrentPolygon = new Polygon();
    
    /**
     * List of collision points. This list is recreated after each update. 
     */
    private LinkedList<Vector2D> mCollisionPoints = new LinkedList<Vector2D>();
    
    /**
     * Radius of the bounding sphere. 
     */
    private double mBoundingSphere = 0.0;
    
    /**
     * Constructor of Vehicle.
     */
    public Vehicle () {
    }
    
    /**
     * Writes the collision points of the vehicle into the output-image. 
     * @param output reference to the output image
     * @param color in which color to paint the collision points
     */
    protected void paintCollisionPoints(BufferedImage output, Color color)
    {
        if (output != null)
        {
            int x, y;
            for (Vector2D vPoint : mCollisionPoints)
            {
                x = (int)vPoint.getX();
                y = (int)vPoint.getY();
                if (x >= 0 && x < output.getWidth() && y >= 0 && y < output.getHeight())
                {
                    output.setRGB(x, y, color.getRGB());
                }
            }
        }
    }
    
    /**
     * Updates the current collision data with respect to some rotation angle.
     * @param angle rotation angle
     */
    protected void updatePolygon (double angle) {
        int offsetx = (int) p_off.getX();
        int offsety = (int) p_off.getY();
        
        // Apply rotation to polygon data
        for (int i = 0; i < mOriginalPolygon.npoints; i++) {
            int x = mOriginalPolygon.xpoints[i] - offsetx;
            int y = mOriginalPolygon.ypoints[i] - offsety;
            mCurrentPolygon.xpoints[i] = (int)(x * Math.cos(-angle) - y * Math.sin(-angle));
            mCurrentPolygon.ypoints[i] = (int)(x * Math.sin(-angle) + y * Math.cos(-angle));
            mCurrentPolygon.xpoints[i] += offsetx;
            mCurrentPolygon.ypoints[i] += offsety;
        }
        mCurrentPolygon.reset();
        mCurrentPolygon.npoints = mOriginalPolygon.npoints;        
    }
    
    @Override
    public boolean isColliding(double px, double py) {
        double x = (px - p_x) * sTextureSize;
        double y = (py - p_y) * sTextureSize;
        return mCurrentPolygon.contains(x, y);
    }

    /**
     * Creates a list of all collision points of the vehicle. 
     */
    protected void createCollisionPoints () {
        mCollisionPoints.clear();
        
        // Go through all polygon points of the vehicle
        for (int i = 0; i < mCurrentPolygon.npoints; i++) {

            // transformation from vehicle-pixel-coordinates into world-texture-coordinates
            double px = p_x + mCurrentPolygon.xpoints[i] * sPixelInTexture;
            double py = p_y + mCurrentPolygon.ypoints[i] * sPixelInTexture;
            double qx = p_x + mCurrentPolygon.xpoints[(i+1) % mCurrentPolygon.npoints] * sPixelInTexture;
            double qy = p_y + mCurrentPolygon.ypoints[(i+1) % mCurrentPolygon.npoints] * sPixelInTexture;

            int length = Math.abs(mCurrentPolygon.xpoints[i] - mCurrentPolygon.xpoints[(i+1) % mCurrentPolygon.npoints]) 
                    + Math.abs(mCurrentPolygon.ypoints[i] - mCurrentPolygon.ypoints[(i+1) % mCurrentPolygon.npoints]);
            
            // create collision points in between of polygon point p and q
            for (int k = 0; k < length; k++) {
                mCollisionPoints.add(new Vector2D(px + k * (qx - px) / length, py + k * (qy - py) / length));
            }
        }
    }
    
    @Override
    public boolean isColliding(PhysicalBody obj) 
    {
        double dist = obj.getCenter().distance(getCenter());

        // check collision between bounding spheres
        if (dist > obj.getBoundingSphere() + mBoundingSphere)
            return false;
            
        // check collision between polygon and other object
        for (Vector2D vPoint : mCollisionPoints) 
            if (obj.isColliding(vPoint.x, vPoint.y))
                return true;
        
        return false;
    }

    @Override
    public Vector2D isColliding(Level map) {
        for (Vector2D vPoint : mCollisionPoints)
        {
            if ( map.getGroundProperty(vPoint, getLayer()) 
                    == CollisionTexture.GroundProperty.WALL)
            {
                return vPoint;
            }
        }
        return null;
    }

    @Override
    public void updateData ()
    {
        updatePolygon(r_c);
        createCollisionPoints();
    }

    @Override
    public boolean isRigidBody() {
        return true;
    }

    @Override
    public void setProperty(String name, String value) {

        if (name == null) return;
        
        if (name.compareTo("CollisionPoint") == 0) 
        {
            String [] vPoint = value.split(",");
            mOriginalPolygon.addPoint(Integer.parseInt(vPoint[0]), 
                                      Integer.parseInt(vPoint[1]));
        }
        else
        {
            setPhysicalProperty(name, Double.parseDouble(value));
        }
    }

    /**
     * Creates a copy of a polygon.
     * @param p polygon to copy (remains untouched)
     * @return reference to the copy
     */
    private Polygon copyPolygon (Polygon p)
    {
        Polygon vReturn = new Polygon();
        for (int i = 0; i < p.npoints; i++)
            vReturn.addPoint(p.xpoints[i], p.ypoints[i]);
        vReturn.npoints = p.npoints;
        return vReturn;
    }
    
    @Override
    public void initialize() 
    {
        mCurrentPolygon = copyPolygon(mOriginalPolygon);

        // Transform polygon-data into texture-coordinates
        Vector2D [] points = new Vector2D[mCurrentPolygon.npoints];
        for (int i = 0; i < points.length; i++)
        {
            points[i] = new Vector2D(mCurrentPolygon.xpoints[i],mCurrentPolygon.ypoints[i]);
        }
        
        // Create Bounding-Sphere
        for (int i = 0; i < points.length; i++)
        {
            double dist = p_off.distance(points[i]);
            if (dist > mBoundingSphere)
                mBoundingSphere = dist;
        }
    }

    @Override
    public double getBoundingSphere() 
    {
        return mBoundingSphere;
    }
    
}
