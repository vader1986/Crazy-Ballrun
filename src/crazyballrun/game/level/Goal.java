/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level;

import crazyballrun.game.utilities.Rectangle3D;
import java.util.ArrayList;

/**
 * The Goal-class represents one state of the Statistics-instance of a level. 
 * This means, it defines a static goal for each player by defining a minimum
 * value for each attribute and optionally a deadline for time. 
 * 
 * @author Timm Hoffmeister
 */
public class Goal 
{
    /**
     * Id of the player which has to reach the goal.  
     */
    private int mPlayerId = -1;

    /**
     * Game time limit for the player. 
     */
    private long mGameTimeLimit = -1;
    
    /**
     * Minimum statistic values the player has to reach.
     */
    private ArrayList<Double> mStatisticValues = new ArrayList<Double>();
    
    /**
     * Specifies specific checkpoints a player has to reach in the right order
     * to increase its "rounds". 
     */
    private ArrayList<Rectangle3D> mCheckPoints = new ArrayList<Rectangle3D>();
    
    /**
     * Creates a goal for a particular player. 
     * @param player player id
     */
    public Goal (int player)
    {
        mPlayerId = player;
        for (int i = 0; i < Statistics.ValueType.values().length; i++)
            mStatisticValues.add(i, 0.0);
    }

    /**
     * Sets a time limit for the player in miliseconds.
     * @param time time limit
     */
    public void setTimeLimit (long time)
    {
        mGameTimeLimit = time;
    }
    
    /**
     * Sets one minimum value for a specific attribute (statistical value) or 
     * other goal-specific settings (like checkpoints). 
     * @param attr attribute type
     * @param value minimum value
     */
    public void set(String attr, String value)
    {
        if (attr.equals("checkpoint"))
        {
            // checkpoint-goals: x,y,layer
            String [] points = value.split(",");
            Rectangle3D checkpoint = new Rectangle3D(
                    Double.parseDouble(points[0]),
                    Double.parseDouble(points[1]),
                    Double.parseDouble(points[2]),
                    Double.parseDouble(points[3]),
                    Integer.parseInt(points[2]));
            mCheckPoints.add(checkpoint);
        }
        else
        {
            // statistic goals (kills, etc.)
            Statistics.ValueType vType = Statistics.ValueType.valueOf(attr);
            mStatisticValues.set(vType.ordinal(), Double.parseDouble(value));            
        }
    }
    
    /**
     * Gets the id of the player the goal is for. 
     * @return player id
     */
    public int getPlayerId () 
    {
        return mPlayerId;
    }
    
    /**
     * Gets the time limit of the player in miliseconds. 
     * @return player's time limit
     */
    public long getTimeLimit () 
    {
        return mGameTimeLimit;
    }
    
    /**
     * Gets the list of checkpoints for the player. 
     * @return list of checkpoints
     */
    public ArrayList<Rectangle3D> getCheckpoints () 
    {
        return mCheckPoints;
    }
    
    /**
     * Gets the minimum value of an attribute the player needs to reach its goal. 
     * @param attr attribute type
     * @return minimum value for an attribute
     */
    public double get (Statistics.ValueType attr)
    {
        return mStatisticValues.get(attr.ordinal());
    }
}
