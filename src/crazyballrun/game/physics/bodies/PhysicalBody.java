/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.physics.bodies;

import crazyballrun.game.GameConstants;
import crazyballrun.game.level.Level;
import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.utilities.Vector2D;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The PhysicalBody offers an interface for physical objects in the world. This 
 * implies physical interactions between several rigid bodies through collision
 * and applying external and internal forces and torques to move or rotate the
 * object. 
 * 
 * @author Timm Hoffmeister
 */
public abstract class PhysicalBody {
    
    /**
     * x-coordinate of position
     */
    protected double p_x = 0.0;

    /**
     * y-coordinate of position
     */
    protected double p_y = 0.0;

    /**
     * velocity in x-direction
     */
    protected double v_x = 0.0;
    
    /**
     * velocity in y-direction
     */
    protected double v_y = 0.0;
    
    /**
     * angular rotation speed
     */
    protected double v_r = 0.0;

    /**
     * current rotation
     */
    protected double r_c = 0.0;

    /**
     * x-component of orientation
     */
    protected double d_x = 0.0;

    /**
     * y-component of orientation
     */
    protected double d_y = -1.0;

    /**
     * currently invoked acceleration by player
     */
    protected double a_c = 0.0;

    /**
     * currently rotation velocity (angle) invoked by player
     */
    protected double v_rot = 0.0;

    /**
     * currently applied brake force
     */
    protected double a_br_c = 1.0;

    /**
     * maximum velocity
     */
    protected double v_max = 1.0;

    /**
     * constant for aerodynamics of the rigid body
     */
    protected double c_aer = 0.1;
    
    /**
     * constant for road grip of the rigid body
     */
    protected double c_rg = 0.1;

    /**
     * brake force player may invoke
     */
    protected double a_br = 0.2;
    
    /**
     * acceleration player may invoke
     */
    protected double a_in = 0.1;

    /**
     * backward acceleration the player may invoke
     */
    protected double a_in_r = 0.1;

    /**
     * rotation speed player may invoke
     */
    protected double r_in = 0.1;

    /**
     * offset to the center
     */
    protected Vector2D p_off = new Vector2D();
    
    /**
     * List of externally forced rotations for the next timestep.
     */
    private LinkedList<Double> r_ex_list = new LinkedList<Double>();

    /**
     * List of externally forced accelerations for the next timestep.
     */
    private LinkedList<Vector2D> a_ex_list = new LinkedList<Vector2D>();

    /**
     * List of all properties of the object.
     */
    private ArrayList<String> mProperties = new ArrayList<String>();

    /**
     * List of all property values of the object. 
     */
    private ArrayList<String> mPropertyValues = new ArrayList<String>();    
    
    /**
     * Rigid body type. 
     */
    private String mType = null;

    /**
     * Sets the rigid body type. 
     * @param type type name
     */
    public void setType (String type)
    {
        mType = type;
    }
    
    @Override
    public PhysicalBody clone ()
    {
        PhysicalBody vCopy = PhysicalBodyFactory.create(mType);
        for (int i = 0; i < mProperties.size(); i++)
            vCopy.setProperty(mProperties.get(i), mPropertyValues.get(i));
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
     * Stops internal rotation of the rigid body. 
     */
    public void detachRotation () 
    {
        v_rot = 0;
    }
    
    /**
     * Applies a left or right rotation to the rigid body.
     * @param left "true" if left rotation
     */
    public void applyRotation (boolean left) 
    {
        v_rot = left ? r_in : -r_in;
    }
    
    /**
     * Stops internal acceleration of the rigid body.
     */
    public void detachAcceleration () 
    {
        a_c = 0;
    }
    
    /**
     * Applies a forward- or backward-acceleration to the rigid body. 
     * @param forward "true" if forward acceleration
     */
    public void applyAcceleration (boolean forward) 
    {
        a_c = forward ? a_in : -a_in_r;
    }
    
    /**
     * Removes a rotation-value from the external rotations influencing the rigid
     * bodies' orientation. 
     * @param rotation external rotation
     */
    public void detachRotation (Double rotation) 
    {
        r_ex_list.remove(rotation);
    }

    /**
     * Adds a rotation-force to the rigid body.
     * @param rotation external rotation
     */
    public void attachRotation (Double rotation) 
    {
        r_ex_list.add(rotation);
    }
    
    /**
     * Removes a acceleration-vector from the external accelerations influencing 
     * the rigid body. 
     * @param force external acceleration vector
     */
    public void detachAcceleration (Vector2D force) 
    {
        a_ex_list.remove(force);
    }

    /**
     * Attaches an external acceleration to the rigid body. 
     * @param force external acceleration vector
     */
    public void attachAcceleration (Vector2D force) 
    {
        a_ex_list.add(force);
    }

    /**
     * Applies an internal brake-force to the rigid body.
     */
    public void applyBrakeForce () 
    {
        a_br_c = a_br;
    }

    /**
     * Detaches the inner brake from the rigid body.
     */
    public void detachBrakeForce () 
    {
        a_br_c = 1.0;
    }
    
    /**
     * Sets back the position and orientation of the rigid body after a collision. 
     * @param dt timestep size
     */
    public void collisionReactionObject (double dt) 
    {
        if (isRigidBody())
        {
            p_x = p_x - 1.1 * dt * v_x;
            p_y = p_y - 1.1 * dt * v_y;
            v_x = -v_x;
            v_y = -v_y;
            r_c = r_c - dt * v_r;    
        }
    }
    
    /**
     * Sets back the position and orientation of the rigid body after a collision. 
     * @param dt timestep size
     * @param point collision point
     */
    public void collisionReaction (double dt, Vector2D point) 
    {
        if (isRigidBody())
        {
            p_x = p_x - dt * v_x;
            p_y = p_y - dt * v_y;
            v_x = -v_x / 2;
            v_y = -v_y / 2;
            r_c = r_c - dt * v_r;
        }
    }

    /**
     * Updates velocity, orientation and position of the rigid body due to internal 
     * and external forces by performing the explicit euler method (for integration  
     * of velocity and position in time).
     * @param dt timestep size
     * @param c_w air resistance value (location dependend)
     * @param c_c centripital force constant (location dependend)
     */
    public void update (double dt, double c_w, double c_c) {

        // rotation speed (angular velocity)
        v_r = v_rot;
        for (Double r_ex : r_ex_list)
            v_r += r_ex;

        // rotation update (explicit euler)
        r_c = r_c + dt * v_r;

        // update orientation
        d_x = -Math.sin(r_c);
        d_y = -Math.cos(r_c);        
        
        // acceleration = internal acceleration - air resistance
        double a_x = a_c * d_x - c_w * c_aer * v_x;
        double a_y = a_c * d_y - c_w * c_aer * v_y;
        
        // pseudo-torque for velocity-orientation-correction
        double vr_step = -v_r * dt * c_c * c_rg;
        double vx_old = v_x;
        double vy_old = v_y;
        
        v_x = vx_old * Math.cos(vr_step) - vy_old * Math.sin(vr_step);
        v_y = vx_old * Math.sin(vr_step) + vy_old * Math.cos(vr_step);

        // acceleration + external acceleration forces
        for (Vector2D a_ex : a_ex_list)
        {
            a_x += a_ex.x;
            a_y += a_ex.y;
        }
        
        // velocity update (explicit euler)
        v_x = v_x + dt * a_x;
        v_y = v_y + dt * a_y;
        
        // maximum velocity
        double v = Math.sqrt(v_x * v_x + v_y * v_y);
        if (v > v_max)
        {
            v_x = v_x * v_max / v; 
            v_y = v_y * v_max / v; 
        }
        else if (v < a_in_r * dt)
        {
            v_x = v_y = 0.0;
            return;
        }
        
        // Apply pseudo-brake-force
        v_x = v_x * a_br_c;
        v_y = v_y * a_br_c;
        
        // position update (explicit euler)
        p_x = p_x + dt * v_x;
        p_y = p_y + dt * v_y;
    }
    
    /**
     * Applies an offset to the rigid bodies' position (used for collision reaction). 
     * @param x offset in x-direction
     * @param y offset in y-direction
     */
    public void applyOffset (double x, double y)
    {
        p_x += x;
        p_y += y;
    }
            
    /**
     * Sets the position of the rigid body in texture-coordinates.
     * @param x x-coordinate of position
     * @param y y-coordinate of position
     */
    public void setPosition(double x, double y) 
    {
        p_x = x;
        p_y = y;
    }
    
    /**
     * Sets the velocity of the rigid body.
     * @param x x-coordinate of velocity
     * @param y y-coordinate of velocity
     */
    public void setVelocity(double x, double y) 
    {
        v_x = x;
        v_y = y;
    }

    /**
     * Sets the rotation angle of the rigid body.
     * @param angle rotation angle
     */
    public void setRotation (double angle) 
    {
        r_c = angle;
        d_x = -Math.sin(r_c);
        d_y = -Math.cos(r_c);        
    }
    
    /**
     * Gets the velocity of the rigid body. 
     * @return veloity of the rigid body
     */
    public Vector2D getVelocity () 
    {
        return new Vector2D(v_x, v_y);
    }
    
    /**
     * Gets the position (upper left) of the rigid body. 
     * @return position of the rigid body
     */
    public Vector2D getPosition () 
    {
        return new Vector2D(p_x, p_y);
    }
    
    /**
     * Gets the center point of the rigid body. This method is used for pixel
     * collision test with CollisionMaps to apply ground properties to the rigid
     * bodies' physics. 
     * @return the center point of the rigid body.
     */
    public Vector2D getCenter () 
    {
        return new Vector2D(p_x + p_off.x, p_y + p_off.y);
    }
    
    /**
     * Gets the offset from position to the center of the rigid body.
     * @return offset of the rigid body to its center
     */
    public Vector2D getOffset() 
    {
        return p_off;
    }
    
    /**
     * Layer (z-Position) of the rigid body.
     */
    private int mLayer = 0;
    
    /**
     * Gets the layer on which the rigid body is placed, currently.
     * @return current layer of the rigid body
     */
    public int getLayer() 
    {
        return mLayer;
    }

    /**
     * Positions the rigid body on the next layer (on top of current layer). 
     */
    public void nextLayer() 
    {
        mLayer++;
    }    
    
    /**
     * Positions the rigid body on the previous layer (on bottom of current layer). 
     */
    public void previousLayer() 
    {
        mLayer--;
    }
    
    /**
     * Sets the current layer of the rigid body. 
     * @param layer new current layer of the rigid body
     */
    public void setLayer(int layer) 
    {
        mLayer = layer;
    }    

    /**
     * Gets the direction the rigid body is looking into. 
     * @return direction of the rigid body
     */
    public Vector2D getDirection () 
    {
        return new Vector2D(d_x, d_y);
    }    
    
    /**
     * Current rotation angle of the rigid body.
     * @return rotation angle
     */
    public double getRotation () 
    {
        return r_c;
    }
    
    /**
     * Reference to the corresponding level object. 
     */
    protected LevelObject mObject;
    
    /**
     * Sets the reference to the LevelObject. 
     * @param obj reference to the LevelObject
     */
    public void setLevelObject (LevelObject obj) 
    {
        mObject = obj;
    }
    
    /**
     * Gets the current speed of the rigid body.
     * @return current speed
     */
    public double getSpeed () 
    {
        return Math.sqrt(v_x * v_x + v_y * v_y);
    }
    
    /**
     * Specifies if the rigid body is moving or not.
     * @return "true" if the rigid body is moving
     */
    public boolean hasSpeed () 
    {
        return (v_x != 0 || v_y != 0);
    }

    /**
     * Physical property name for maximum speed.
     */
    public final static String P_MAXIMUM_SPEED = "MaxSpeed";

    /**
     * Physical property name for aerodynamics of the physical body. 
     */
    public final static String P_AERODYNAMICS = "Aerodynamics";

    /**
     * Physical property name for road grip of the body. 
     */
    public final static String P_ROAD_GRIP = "RoadGrip";

    /**
     * Physical property name for acceleration of the body (inner force).
     */
    public final static String P_ACCELERATION = "Acceleration";

    /**
     * Physical property name for backward acceleration of the body (inner force).
     */
    public final static String P_BACKWARDS_ACCELERATION = "BackwardAcceleration";

    /**
     * Physical property name for rotation acceleration of the body (inner torque). 
     */
    public final static String P_ROTATION = "Rotation";

    /**
     * Physical property name for pixel offset from left edge to the middle of 
     * the body.
     */
    public final static String P_CENTER_OFFSET_X = "OffsetX";

    /**
     * Physical property name for pixel offset from top edge to the middle of 
     * the body.
     */
    public final static String P_CENTER_OFFSET_Y = "OffsetY";

    /**
     * Physical property name for brake force of the body.
     */
    public final static String P_BRAKE_FORCE = "BrakeForce";
    
    /**
     * Gets the value of a physical property of the rigid body. 
     * @param property name of the property
     * @return value of the property
     */
    public double getPhysicalProperty (String property) 
    {
        if (property.equals(P_MAXIMUM_SPEED))
        {
            return v_max;
        }
        else if (property.equals(P_AERODYNAMICS))
        {
            return c_aer;
        }
        else if (property.equals(P_ROAD_GRIP))
        {
            return c_rg;
        }
        else if (property.equals(P_ACCELERATION))
        {
            return a_in;
        }
        else if (property.equals(P_BACKWARDS_ACCELERATION))
        {
            return a_in_r;
        }
        else if (property.equals(P_ROTATION))
        {
            return r_in;
        }
        else if (property.equals(P_CENTER_OFFSET_X))
        {
            return p_off.x;
        }
        else if (property.equals(P_CENTER_OFFSET_Y))
        {
            return p_off.y;
        }  
        else if (property.equals(P_BRAKE_FORCE))
        {
            return a_br;
        }
        
        throw new IllegalArgumentException("Illegal property: " + property);
    }
    
    /**
     * Sets a physical property due to its name. If property equals 
     * P_CENTER_OFFSET_X or P_CENTER_OFFSET_Y the measurement is "pixels"! In
     * any other case it is textures. 
     * @param property name of the physical property
     * @param value value for the property
     */
    public void setPhysicalProperty (String property, double value) 
    {
        if (property.equals(P_MAXIMUM_SPEED))
        {
            v_max = value;
        }
        else if (property.equals(P_AERODYNAMICS))
        {
            c_aer = 1 - value;
        }
        else if (property.equals(P_ROAD_GRIP))
        {
            c_rg = value;
        }
        else if (property.equals(P_ACCELERATION))
        {
            a_in = value;
        }
        else if (property.equals(P_BACKWARDS_ACCELERATION))
        {
            a_in_r = value;
        }
        else if (property.equals(P_ROTATION))
        {
            r_in = value;
        }
        else if (property.equals(P_CENTER_OFFSET_X))
        {
            p_off.x = value / GameConstants.TEXTURE_SIZE;
        }
        else if (property.equals(P_CENTER_OFFSET_Y))
        {
            p_off.y = value / GameConstants.TEXTURE_SIZE;
        }
        else if (property.equals(P_BRAKE_FORCE))
        {
            a_br = 1 - value;
        }
    }
    
    ///////////////////////////////////////
    //       Collision and Physics      ///
    ///////////////////////////////////////
    
    /**
     * Defines if the rigid body is really "rigid" or rather soft. If this method
     * returns false, that means, that collisions between another rigid body and
     * this body will not be checked. HOWEVER, collision between this body and other
     * rigid bodies will be checked. 
     * 
     * One example for using this function with 'return false' is following: assuming
     * the rigid body is a bonus package, we do not need to check both collisions -
     * vehicle -> bonus package and bonus package -> vehicle. If the vehicle hits 
     * the bonus package, there shouldn't be any physical effect applied to the
     * vehicle (but the bonus-effect). To avoid physical influence of the bonus 
     * package to the vehicle we just check for collisions bonus-package -> vehicle
     * instead and then apply the bonus-effect through the "onCollision(obj)"-method
     * provided by the bonus-package implementation. WHY can we do that? We may do
     * that because if the vehicle crashes into a bonus-package, a collision from
     * bonus-package to vehicle exists for sure, too. 
     * 
     * @return 'false' if collision checks between other rigid bodies and this 
     * object should be avoided. However a collision the other way around (this
     * rigid body -> other rigid bodies) will be still checked. If 'true', collisions
     * in both directions are checked. 
     */
    public abstract boolean isRigidBody ();
    
    /**
     * Calculates and returns the radius of the bodies' bounding sphere for 
     * efficient collision detection. 
     * @return radius of the bounding sphere
     */
    public abstract double getBoundingSphere ();
    
    /**
     * Finds out if the point is inside of the rigid body. This method may be used
     * by other RigidBodies to find out if there's a collision. 
     * @param px 2D collision point
     * @param py 2D collision point
     * @return 'true' if the given point is colliding with the rigid body. 
     */
    public abstract boolean isColliding (double px, double py);    
    
    /**
     * Finds out if there's a collision with another rigid body. This method usually
     * calls the "isCollision(Vector2D)"-method of the other PhysicalBody with all
     * possible collision points this rigid body has. Another possible implementation
     * might just calculate the distance to the other PhysicalBody to check for 
     * collisions. 
     * @param obj reference to another rigid body
     * @return "true" if colliding
     */
    public abstract boolean isColliding (PhysicalBody obj);

    /**
     * Checks for a collision with a texture. This may occure due to a wall inside
     * a texture. The function compare the ground of every collision points with 
     * to the GroundProperty.WALL and GroundProperty.WATER if the object cannot 
     * swim. If a hit has been found the function should return true. For checking
     * the correct texture the method should use "Level.getGroundProperty(position)",
     * where position is the position of all the collision points of the rigid
     * body.
     * @param map reference to the level
     * @return collision point 
     */
    public abstract Vector2D isColliding (Level map);

    /**
     * Updates the collision data of the rigid body due to the new position and
     * orientation. 
     */
    public abstract void updateData ();
    
    /** 
     * Sets one property. This method is used by the LevelObjectParser to support 
     * implementation specific data (e.g. vehicles have a completely different set
     * of properties than not-moving obstacles). 
     * 
     * @param name name of the property
     * @param value value for the property
     */
    public abstract void setProperty (String name, String value);

    /**
     * Initializes the rigid body. This method is called after finishing parsing
     * the object (that means all properties have been set). 
     */
    public abstract void initialize ();
}
