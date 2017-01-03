/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.physics;

import crazyballrun.game.level.Level;

/**
 * The PhysicsEngine is responsible of creating, initializing and starting the
 * PhysicsThread. It handles the thread-safe communication with other threads.
 * 
 * @author Timm Hoffmeister
 */
public class PhysicsEngine {

    /**
     * Reference to the physics engine. 
     */
    private static PhysicsEngine engine = null;

    /**
     * Reference to the PhysicsThread. 
     */
    private PhysicsThread mPhysicsThread = new PhysicsThread();
    
    /**
     * Provides access to the physics engine.
     * @return a reference to the PhysicsEngine. 
     */
    public static PhysicsEngine getInstance() {
        if (engine == null)
            engine = new PhysicsEngine();
        return engine;
    }
    
    /**
     * Private constructor for PhysicsEngine. 
     */
    private PhysicsEngine() {

    }

    /**
     * Initializes the PhysicsEngine. 
     */
    public void initialize() {
        
    }

    /**
     * Starts the PhysicsThread. 
     */
    public void start() {
        mPhysicsThread.doPause();
        mPhysicsThread.start();
    }
    
    /**
     * Ends the PhysicsThread.
     * @throws InterruptedException 
     */
    public void end () throws InterruptedException {
        mPhysicsThread.doExit();
        mPhysicsThread.join();
    }
    
    /**
     * Sets the state of the ample concerning physics.
     * @param state ample state
     */
    public void setAmpleState (boolean state)
    {
        mPhysicsThread.setAmple(state);
    }
    
    /**
     * Pauses the PhysicsThread.
     */
    public void physicsPause() {
        mPhysicsThread.doPause();
    }
 
    /**
     * Continues PhysicsThread.
     */
    public void physicsContinue() {
        mPhysicsThread.doContinue();
    }
    
    /**
     * Sets the level-reference for the PhysicsThread. 
     * @param level reference to the level
     */
    public void setLevel(Level level) {
        mPhysicsThread.setLevel(level);
    }
}
