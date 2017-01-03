/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.effects;

import crazyballrun.game.level.objects.LevelObject;

/**
 * The ObjectEffect class represents an effect released uppon a particular level-
 * object for a certain period of time. 
 * 
 * @author Timm Hoffmeister
 */
public abstract class ObjectEffect {

    /**
     * Performs an effect uppon the object.
     * @param obj reference to the affecting object
     * @param dt time passed since the last effect()-call (in miliseconds)
     */
    public abstract void effect (LevelObject obj, int dt);
    
    /**
     * Gets remaining life time of the effect.
     * @return lifetime in miliseconds
     */
    public abstract int getLifeTime();
    
    
}
