/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.graphics;

import crazyballrun.game.GameLogger;
import crazyballrun.game.GameSettings;
import crazyballrun.game.gui.GUIEngine;
import crazyballrun.game.gui.GUIFrame;
import crazyballrun.game.level.Level;
import crazyballrun.game.level.controllers.Player;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

/**
 * The RenderThread renders 2D images. 
 * @author Timm Hoffmeister
 */
public class RenderThread extends Thread {

    /**
     * Pauses the RenderThread if true. 
     */
    private boolean mPaused = false;
    
    /**
     * Tells the render thread to exit is set to 'true'.
     */
    private boolean mExit = false;
    
    /**
     * If "mInGame" is set to "true" the RenderThread will just paint the
     * existing cameras, otherwise it's painting the current frame of the GUI.
     */
    private boolean mInGame = false;
    
    /**
     * Graphic environment for rendering (hardware information).
     */
    private GraphicsEnvironment mGraphicsEnvironment = null;

    /**
     * Graphic device for rendering. 
     */
    private GraphicsDevice mGraphicsDevice = null;

    /**
     * Original display mode (resolution, color depth and refresh rate) of the
     * screen. 
     */
    private DisplayMode mOriginalDisplayMode = null;
        
    /**
     * Reference to the Window-Frame (where to render graphics). 
     */
    private Frame mFrame = null;
    
    /**
     * BufferStrategy for rendering.
     */
    private BufferStrategy mBufferStrategy = null;
    
    /**
     * Boundaries of the frame. 
     */
    private Rectangle mFrameBounds = null;
    
    /**
     * Reference to the game settings.
     */
    private GameSettings mSettings = null;
    
    /**
     * Reference to the GUIEngine.
     */
    private GUIEngine mGuiEngine = null;
    
    /**
     * Currently loaded level (used if "mInGame" is "true"). 
     */
    private Level mCurrentLevel = null;
    
    /**
     * Ample-Circles for rendering.
     */
    private RoundRectangle2D [] mAmple = new RoundRectangle2D[3];

    /**
     * Frames for the single ample lights.
     */
    private RoundRectangle2D [] mAmpleLightFrames = new RoundRectangle2D[3];
    
    /**
     * Ample-Color-Gradients for rendering.
     */
    private GradientPaint [] mAmpleGradient = new GradientPaint[3];
    
    /**
     * Ample-Frame as rendering-shape.
     */
    private RoundRectangle2D mAmpleFrame = null;
    
    /**
     * Color Gradient for the ample's frame.
     */
    private GradientPaint mAmpleFrameGradient = null;
    
    /**
     * Index defining the state of the ample. 
     */
    private int mAmpleIndex = -1;
    
    /**
     * Constructor for the RenderThread. 
     * @param frame the main rendering window (frame)
     */
    public RenderThread (Frame frame) {
        super();
        mSettings = GameSettings.getInstance();
        mFrame = frame;
        mGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        mGraphicsDevice = mGraphicsEnvironment.getDefaultScreenDevice();
        mOriginalDisplayMode = mGraphicsDevice.getDisplayMode();

        // Initialize Ample
        mAmple[0] = new RoundRectangle2D.Double(10, 9, 40, 40, 20, 20);
        mAmple[1] = new RoundRectangle2D.Double(10, 50, 40, 40, 20, 20);
        mAmple[2] = new RoundRectangle2D.Double(10, 91, 40, 40, 20, 20);
        mAmpleLightFrames[0] = new RoundRectangle2D.Double(9, 8, 42, 42, 20, 20);
        mAmpleLightFrames[1] = new RoundRectangle2D.Double(9, 49, 42, 42, 20, 20);
        mAmpleLightFrames[2] = new RoundRectangle2D.Double(9, 90, 42, 42, 20, 20);
        mAmpleGradient[0] = new GradientPaint(0, 0, Color.RED, 40, 40, new Color(1.0f, 0.2f, 0.2f), true);
        mAmpleGradient[1] = new GradientPaint(0, 0, Color.YELLOW, 40, 40, Color.ORANGE, true);
        mAmpleGradient[2] = new GradientPaint(0, 0, Color.GREEN, 40, 40, new Color(0.2f, 1.0f, 0.2f), true);
        mAmpleFrame = new RoundRectangle2D.Double(5, 3, 50, 134, 10, 10);
        mAmpleFrameGradient = new GradientPaint(0, 0, Color.GRAY, 5, 5, Color.WHITE, true);
    }
    
    /**
     * Gives a reference to the current level to the RenderThread.
     * @param level reference to the currently loaded level
     */
    public void setCurrentLevel (Level level) {
        mCurrentLevel = level;
    }
    
    /**
     * Initializes the screen (with the given resolution, color depth
     * and refresh rate). 
     */
    public void init() {

        // Create Full-Screen frame
        mFrame.setUndecorated(true);
        mFrame.setIgnoreRepaint(true);
        mGraphicsDevice.setFullScreenWindow(mFrame);
        mGuiEngine = GUIEngine.getInstance();

        // Check if the configured DisplayMode is available
        boolean vIsAvailable = false;
        Integer vResolutionWidth =  (Integer) mSettings.getValue("ResolutionWidth");
        Integer vResolutionHeight = (Integer) mSettings.getValue("ResolutionHeight");
        Integer vColorDepth =       (Integer) mSettings.getValue("ColorDepth");
        Integer vRefreshRate =      (Integer) mSettings.getValue("RefreshRate");
        DisplayMode[] vAvailableModes = mGraphicsDevice.getDisplayModes();
        for (DisplayMode dm : vAvailableModes) {
            if (dm.getWidth() == vResolutionWidth.intValue() && 
                dm.getHeight() == vResolutionHeight.intValue() && 
                dm.getBitDepth() == vColorDepth.intValue() &&
                dm.getRefreshRate() == vRefreshRate.intValue()) {
                vIsAvailable = true;
                break;
            }
        }
        
        // Set resolution/color depth/refresh rate
        if (vIsAvailable && mGraphicsDevice.isDisplayChangeSupported()) {
            try {
                DisplayMode vDisplayMode = new DisplayMode(
                    vResolutionWidth, vResolutionHeight, vColorDepth, vRefreshRate);
                    mGraphicsDevice.setDisplayMode(vDisplayMode);
                System.out.println("Resolution: " + vResolutionWidth + " x " + vResolutionHeight);
            }
            catch (Exception e) {
                System.out.println("Resolution: " + mOriginalDisplayMode.getWidth() + " x " + mOriginalDisplayMode.getHeight());
                mGraphicsDevice.setDisplayMode(mOriginalDisplayMode);
            }
        }
        
        // Create the BufferStragey
        mFrame.createBufferStrategy(2);
        mFrameBounds = mFrame.getBounds();
        mBufferStrategy = mFrame.getBufferStrategy();
    }
    
    /**
     * Pauses the RenderThread. 
     */
    public void doPause() {
        mPaused = true;
    }
    
    /**
     * Continues the RenderThread after pausing. 
     */
    public synchronized void doContinue() {
        mPaused = false;
        this.notify();
    }
    
    /**
     * Tells the render thread to exit. 
     */
    public synchronized void doExit () {
        mExit = true;
    }
    
    /**
     * Switches the current state of the RenderThread (in-game- or GUI-rendering). 
     * @param inGame if 'true' the current level's cameras are rendered, otherwise
     * the current GUI-Frame
     */
    public synchronized void switchState(boolean inGame) {
        mInGame = inGame;
    }
    
    /**
     * Tells the render thread to end.
     * @return 'true' if the render thread is supposed to end.
     */
    private synchronized boolean isExit () {
        return mExit;
    }
        
    /**
     * Shows if the render thread is paused. 
     * @return 'true' if the render thread is paused.
     */
    private synchronized boolean isPaused() {
        return mPaused;
    }
    
    /**
     * If "true" the RenderThread should currently paint all existing cameras
     * in the current level. Otherwise it paints the current GUI-frame.
     * @return current state of the RenderThread (GUI or In-Game)
     */
    private synchronized boolean isInGame () {
        return mInGame;
    }
    
    /**
     * Defines the current state of the ample (-1 means no ample, 0 means red
     * is active, 1 means red and yellow are active, 2 just yellow, 3 yellow and
     * green and 4 just green).
     * @param index ample index (state of the ample)
     */
    public synchronized void setAmple (int index)
    {
        mAmpleIndex = index;
    }
    
    /**
     * Gets the state of the ample.
     * @return ample index
     */
    private synchronized int getAmple ()
    {
        return mAmpleIndex;
    }
    
    @Override
    public void run(){

        long timestep = System.currentTimeMillis();
        int stepsize = 0;
        int ample = -1;
        
        try {
            
            init();

            // Render-Loop
            while (!isExit()) {

                // Pause the render thread
                if (isPaused())
                {
                    boolean vWait = false;
                    while (!vWait) {
                        try {
                            synchronized(this) {
                                this.wait();
                            }
                            vWait = true;
                        }
                        catch (InterruptedException e) {
                            vWait = false;
                            timestep = System.currentTimeMillis();
                        }
                    }
                }
                
                // Render Graphics
                Graphics2D g = null;
                try {
                    g = (Graphics2D)mBufferStrategy.getDrawGraphics();
                    ample = getAmple(); // check ample state

                    // Set Background to black
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, mFrameBounds.width, mFrameBounds.height);

                    // Render in-game cameras
                    if (isInGame() || ample != -1) 
                    {
                        stepsize = (int)(System.currentTimeMillis() - timestep);
                        timestep = System.currentTimeMillis();
                        ArrayList<Player> vPlayers = mCurrentLevel.getPlayers();
                        for (Player vPlayer : vPlayers) {
                            if (vPlayer.hasCamera()) {
                                vPlayer.update();
                                vPlayer.getCamera().paint(g, mFrame, Math.max(1000, stepsize));
                                g.setColor(Color.red);
                                g.draw(vPlayer.getCamera().getDisplay());
                            }
                        }
                    }
                    
                    // Render graphical user interface
                    GUIFrame mGui = mGuiEngine.getCurrentFrame();
                    if (mGui != null) mGui.paint(g, mFrame);
    
                    // Render ample
                    if (ample != -1)
                    {
                        g.setPaint(mAmpleFrameGradient);
                        g.fill(mAmpleFrame);

                        g.setColor(Color.BLACK);
                        g.fill(mAmpleLightFrames[0]);
                        if (ample <= 1) 
                        {
                            g.setPaint(mAmpleGradient[0]);
                            g.fill(mAmple[0]);
                        }
                        
                        g.setColor(Color.BLACK);
                        g.fill(mAmpleLightFrames[1]);
                        if (ample >= 1 && ample <= 3) 
                        {
                            g.setPaint(mAmpleGradient[1]);
                            g.fill(mAmple[1]);
                        }
                        
                        g.setColor(Color.BLACK);
                        g.fill(mAmpleLightFrames[2]);
                        if (ample >= 3) 
                        {
                            g.setPaint(mAmpleGradient[2]);
                            g.fill(mAmple[2]);
                        }
                    }
                    
                    // Render buffers
                    mBufferStrategy.show();
                } 
                finally {
                    if (g != null) {
                        g.dispose();
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            GameLogger.log(e);
        }
        finally 
        {
            try {
                mGraphicsDevice.setDisplayMode(mOriginalDisplayMode);
                mGraphicsDevice.setFullScreenWindow(null);
            } catch (Exception e) {
                GameLogger.log(e);
            }
        }
    }

}
