/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.controllers;

import crazyballrun.game.physics.bodies.PhysicalBody;
import crazyballrun.game.utilities.GraphGenerator;
import crazyballrun.game.utilities.PathFinder;
import crazyballrun.game.utilities.Vector2D;
import java.util.ArrayList;

/**
 * WaypointFollower is a simple implementation of ObjectController. The Waypoint-
 * Follower is level-specific and needs waypoints specified in the level-file.
 * The WaypointFollower stears LevelObjects in a way that they go from one way-
 * point to the next one. After reaching the last waypoint the LevelObject will
 * go for the first one again (consequently running in a "circle"). 
 * 
 * @author Timm Hoffmeister
 */
public class WaypointFollower extends ObjectController {

    /**
     * List storing the waypoints' positions. 
     */
    private ArrayList<Vector2D> mWayPoints = new ArrayList<Vector2D>();

    /**
     * Current waypoint the object is trying to reach.
     */
    private Vector2D mCurrentWaypoint = null;
    
    /**
     * ID of the current waypoint in the waypoint-list. 
     */
    private int mCurrentWaypointId = 0;

    /**
     * How close the waypoint follower needs to get to a waypoint-position to
     * define it as "reached". This is measured in texture-coordinates. 
     */
    private double mRadius = 0.5;
    
    /**
     * Physical model of the object to control.
     */
    private PhysicalBody mPhysicalModel = null;

    /**
     * Maximum rotation speed of the rigid body.
     */
    private double mRotationSpeed = 0.1;
    
    /**
     * Constructor of WaypointFollower.
     */
    public WaypointFollower () {
        
    }

    @Override
    public void control(double dt) {

        Vector2D vPosition = mPhysicalModel.getCenter();
        
        // Next waypoint
        if (vPosition.distance(mCurrentWaypoint) < mRadius) 
        {
            mCurrentWaypointId = (mCurrentWaypointId + 1) % mWayPoints.size();
            mCurrentWaypoint = mWayPoints.get(mCurrentWaypointId);
        }

        // Angle between object and waypoint
        Vector2D vWaypointDirection = new Vector2D(mCurrentWaypoint);
        vWaypointDirection.sub(vPosition);
        vWaypointDirection.normalize();
        double vAngle = Math.acos(mPhysicalModel.getDirection().dot(vWaypointDirection));
        
        if (vAngle > dt * mRotationSpeed)
        {
            // Rotation into waypoint-direction
            Vector2D d1 = mPhysicalModel.getDirection();
            Vector2D d2 = mPhysicalModel.getCenter();
            d2.sub(mCurrentWaypoint.x, mCurrentWaypoint.y);
            
            if (-d1.x * d2.y + d1.y * d2.x < 0)
                mPhysicalModel.applyRotation(true);
            else 
                mPhysicalModel.applyRotation(false);
      
            mPhysicalModel.detachAcceleration();
            mPhysicalModel.applyBrakeForce();
        }
        else
        {
            // Acceleration to reach waypoint
            mPhysicalModel.detachRotation();
            mPhysicalModel.detachBrakeForce();
            mPhysicalModel.applyAcceleration(true);
        }
    }

    @Override
    public void setProperty(String name, String value) {
        if (name.equals("Waypoint")) {
            String [] vValues = value.split(",");
            Vector2D vWaypoint = new Vector2D(Double.parseDouble(vValues[0]), Double.parseDouble(vValues[1]));
            mWayPoints.add(vWaypoint);
        }
        else if (name.equals("Radius")) {
            mRadius = Double.parseDouble(value);
        }
    }

    @Override
    public void initialize(ArrayList<GraphGenerator> graphs, PathFinder pathfinder) {
        
        // Initialize current waypoint
        mCurrentWaypointId = 0;
        mCurrentWaypoint = mWayPoints.get(mCurrentWaypointId);
        
        // Physical properties
        mPhysicalModel = mObject.getObjectModel();
        mRotationSpeed = mObject.getObjectModel().getPhysicalProperty(PhysicalBody.P_ROTATION);
    }
    
    
    
}
