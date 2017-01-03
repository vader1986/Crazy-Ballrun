/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.controllers;

import crazyballrun.game.level.LevelEngine;
import crazyballrun.game.level.objects.SimpleRocket;
import crazyballrun.game.physics.bodies.PhysicalBody;
import crazyballrun.game.utilities.GraphGenerator;
import crazyballrun.game.utilities.PathFinder;
import crazyballrun.game.utilities.Vector2D;
import java.util.ArrayList;

/**
 * The PlayerFinder extends the ObjectController so that the controlled object
 * follows the closest player which is not its owner. 
 * 
 * @author Timm Hoffmeister
 */
public class PlayerFinder extends ObjectController {

    /**
     * Maximum rotation speed of the rigid body.
     */
    private double mRotationSpeed = 0.1;
    
    /**
     * Helper member for re-calculating the target. 
     */
    private double mTimeStamp = 0.0;
    
    /**
     * Owner of the "PlayerFinder". The PlayerFinder will not follow its owner
     * but the closest other player. 
     */
    private int mOwner = -1;

    /**
     * Vehicle of the target player. 
     */
    private PhysicalBody mTargetVehicle = null;
    
    /**
     * The controlled object's physical body.
     */
    private PhysicalBody mPhysicalModel = null;
    
    /**
     * List of players in the level. 
     */
    private ArrayList<PhysicalBody> mPlayers = new ArrayList<PhysicalBody>();
    
    /**
     * Sets the owner of the PlayerFinder.
     * @param id player id of the owner
     */
    public void setOwner (int id)
    {
        mOwner = id;
    }
    
    /**
     * Finds the closest target for the PlayerFinder. 
     */
    private void findTarget() 
    {
        // initialization
        double vMin = Double.MAX_VALUE, vDist = 0.0;
        
        // find player with closest distance
        for (int i = 0; i < mPlayers.size(); i++)
        {
            if (i != mOwner)
            {
                vDist = mPlayers.get(i).getCenter().distance(
                        mPhysicalModel.getCenter());
                if (vDist < vMin) 
                {
                    vMin = vDist;
                    mTargetVehicle = mPlayers.get(i);
                }
            }
        }
    }
    
    @Override
    public void control(double dt) 
    {
        // search for target
        mTimeStamp += dt;
        if (mTimeStamp > 1.0 || mTargetVehicle == null)
        {
            findTarget();
            mTimeStamp = 0.0;
        }
        
        // Angle between object and target
        Vector2D vPosition = mPhysicalModel.getCenter();
        Vector2D vTarget = mTargetVehicle.getCenter();
        Vector2D vTargetDirection = new Vector2D(vTarget);
        vTargetDirection.sub(vPosition);
        vTargetDirection.normalize();
        double vAngle = Math.acos(mPhysicalModel.getDirection().dot(vTargetDirection));
        
        // Apply rotation
        if (vAngle > dt * mRotationSpeed)
        {
            Vector2D d1 = mPhysicalModel.getDirection();
            Vector2D d2 = mPhysicalModel.getCenter();
            d2.sub(vTarget.x, vTarget.y);
            
            if (-d1.x * d2.y + d1.y * d2.x < 0)
                mPhysicalModel.applyRotation(true);
            else 
                mPhysicalModel.applyRotation(false);
        }
        else
        {
            mPhysicalModel.detachRotation();
        }
        
        // Apply acceleration
        mPhysicalModel.applyAcceleration(true);
    }

    @Override
    public void setProperty(String name, String value) 
    {
        if (name.equals("owner"))
        {
            mOwner = Integer.parseInt(value);
        }
    }

    @Override
    public void initialize(ArrayList<GraphGenerator> graphs, PathFinder pathfinder) 
    {
        mPhysicalModel = mObject.getObjectModel();
        mRotationSpeed = mObject.getObjectModel().getPhysicalProperty(PhysicalBody.P_ROTATION);

        // Create list of player-vehicles
        ArrayList<Player> vPlayers = LevelEngine.getInstance().getCurrentLevel().getPlayers();
        for (int i = 0; i < vPlayers.size(); i++)
        {
            mPlayers.add(vPlayers.get(i).getVehicle().getObjectModel());
        }
    }
    
    
    
}
