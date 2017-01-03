/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level;

import crazyballrun.game.GameLogger;
import crazyballrun.game.level.controllers.Player;
import crazyballrun.game.physics.bodies.PhysicalBody;
import crazyballrun.game.utilities.Rectangle3D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Statistics class contains (player specific) data of the current level
 * which may be used to check "player reached goal?" or to show some statistical
 * values for each player after finishing the game (e.g. kill/getting-killed 
 * ratio). This class might also be used by the AI to determine how close it is  
 * to its goal and how an action influences the distance to the goal. 
 * 
 * Additionally the "Statistics" store the time passed since the level start in
 * miliseconds to allow a time-based game. 
 * 
 * @author Timm Hoffmeister
 */
public class Statistics {
    
    /**
     * Defines statistical attributes observed by the game for the final statistic
     * shown after finishing a level. 
     */
    public enum ValueType 
    {
        /**
         * Number of "kills" (used weapons which successfully hit another player).
         */
        KILLS,
        /**
         * Number of "deaths" (number of times player has been hit by weapons). 
         */
        DEATHS,
        /**
         * Number of rounds completed by the player.
         */
        ROUNDS
    }

    /**
     * Time passed since level started [miliseconds]. 
     */
    private long mGameTime = 0;

    /**
     * Time stamp since start/resume of the game. 
     */
    private long mTimeStamp;

    /**
     * Defines if any of the players has reached its goal.
     */
    private boolean mGoalReached = false;
    
    /**
     * This HashMap maps player-ids to Double-lists which contain the statistical
     * values with respect to a value-type-id (arraylist-position). 
     */
    private HashMap<Integer, ArrayList<Double>> mValues = null;

    /**
     * One goal-state for each player. 
     */
    private Goal [] mGoals = null;
    
    /**
     * Id of the current checkpoint for each player.
     */
    private int [] mCurCheckPoint = null;
    
    /**
     * List of players. 
     */
    private ArrayList<Player> mPlayers = null;
    
    /**
     * Creates a table for statistical values. The table size depends on the 
     * number of players and statistical attributes. 
     * @param goals player goals (array place = player id)
     * @param players list of players
     */
    public void initialize (Goal [] goals, ArrayList<Player> players)
    {
        // Create player-statistics
        mValues = new HashMap<Integer, ArrayList<Double>>();
        for (int i = 0; i < goals.length; i++)
        {
            ArrayList<Double> vInitialList = new ArrayList<Double>();
            for (int j = 0; j < ValueType.values().length; j++)
                vInitialList.add(0.0);
            mValues.put(i, vInitialList);
        }
        
        // Defines player-goals
        mGoals = goals;
        mCurCheckPoint = new int[goals.length];
        for (int i = 0; i < goals.length; i++) 
            mCurCheckPoint[i] = 0;
        mPlayers = players;
    }

    /**
     * Checks if a particular player has reached its goal and updates the number 
     * of rounds if the player reaches a checkpoint. 
     * @param player player id
     */
    private void check (int player)
    {
        boolean vReached = true;
        ArrayList<Double> vAttributes = mValues.get(player);
        
        // check checkpoints
        if (!mGoals[player].getCheckpoints().isEmpty())
        {
            PhysicalBody obj = mPlayers.get(player).getVehicle().getObjectModel();
            Rectangle3D chk = mGoals[player].getCheckpoints().get(mCurCheckPoint[player]);
            if (chk.inside(obj.getPosition(), obj.getLayer()))
            {
                // update rounds
                mCurCheckPoint[player]++;
                if (mCurCheckPoint[player] == mGoals[player].getCheckpoints().size())
                {
                    incValue(player, ValueType.ROUNDS);
                    mCurCheckPoint[player] = 0;
                }
            }
        }
        
        // check for time-out
        if (mGameTime < mGoals[player].getTimeLimit())
            vReached = false;
        
        // check all attributes of the player
        if (vReached)
            for (int i = 0; i < ValueType.values().length && vReached; i++)
                if (vAttributes.get(i) < mGoals[player].get(ValueType.values()[i]))
                    vReached = false;
        
        // update "goal-reached"
        mGoalReached = mGoalReached || vReached;
    }
    
    /**
     * Checks if one of the players has reached its goal. 
     * @return 'true' if the level should end
     */
    public synchronized boolean finished () 
    {
        return mGoalReached;
    }
    
    /**
     * Resumes the time-counting for the game-time (time passed since level start). 
     * This method has to be called after resuming the game from "pause". 
     */
    public synchronized void resume () 
    {
        mTimeStamp = System.currentTimeMillis();
    }
    
    /**
     * Updates the game time (time which has passed since beginning of the level).
     */
    public synchronized void update ()
    {
        // add passed time to game-time
        final long vCurrentTime = System.currentTimeMillis();
        mGameTime = mGameTime + (vCurrentTime - mTimeStamp);
        mTimeStamp = vCurrentTime;

        // check if any player reached its goal
        for (int i = 0; i < mGoals.length; i++) check(i);
    }
    
    /**
     * Outputs the time passed since level start in ms. 
     * @return time passed since level start [miliseconds]
     */
    public synchronized long getGameTime () 
    {
        return mGameTime;
    }
    
    /**
     * Reads a statistical value from the table. 
     * @param player player id
     * @param attr statistical attribute type
     * @return value of the attribute with respect to the player number
     */
    public synchronized double getValue (int player, ValueType attr)
    {
        double vReturn = 0.0;
        
        try
        {
            vReturn = mValues.get(player).get(attr.ordinal());
        }
        catch (Exception e)
        {
            GameLogger.log(e);
        }
        
        return vReturn;
    }
    
    /**
     * Sets one attribute value for a particular player. 
     * @param player player id
     * @param attr attribute type
     * @param value new value for the attribute
     */
    public synchronized void setValue (int player, ValueType attr, double value)
    {
        try
        {
            mValues.get(player).set(attr.ordinal(), value);
            check(player);
        }
        catch (Exception e)
        {
            GameLogger.log(e);
        }
    }

    /**
     * Adds a value to an attribute for a player. 
     * @param player player id
     * @param attr attribute type
     * @param value value to add
     */
    public synchronized void addValue (int player, ValueType attr, double value)
    {
        try
        {
            final double cur = mValues.get(player).get(attr.ordinal());
            mValues.get(player).set(attr.ordinal(), cur + value);
            check(player);
        }
        catch (Exception e)
        {
            GameLogger.log(e);
        }
    }
    
    /**
     * Increase the value of an attribute for a particular player by 1.
     * @param player player id 
     * @param attr attribute type
     */
    public synchronized void incValue (int player, ValueType attr)
    {
        try
        {
            final double cur = mValues.get(player).get(attr.ordinal());
            mValues.get(player).set(attr.ordinal(), cur + 1.0);
        }
        catch (Exception e)
        {
            GameLogger.log(e);
        }
    }
    
}
