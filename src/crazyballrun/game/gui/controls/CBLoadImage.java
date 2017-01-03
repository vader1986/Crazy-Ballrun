/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui.controls;

import crazyballrun.game.GameSettings;
import crazyballrun.game.gui.GUIControl;
import crazyballrun.game.level.LevelDescription;
import crazyballrun.game.level.LevelEngine;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * The CBLoadImage control is a full-screen picture for a particular level's 
 * load-screen. 
 * 
 * @author Timm Hoffmeister
 */
public class CBLoadImage extends GUIControl {

    /**
     * Refernce to the load-screen image. 
     */
    private BufferedImage mLoadImage = null;
    
    /**
     * Screen width in pixels (resolution).
     */
    private int mScreenWidth = 0;

    /**
     * Screen height in pixels (resolution).
     */
    private int mScreenHeight = 0;
    
    @Override
    public void setAttribute(String attr, String value) 
    {
        // do nothing
    }

    @Override
    public void initialize() 
    {
        mScreenWidth =  (Integer) GameSettings.getInstance().getValue("ResolutionWidth");
        mScreenHeight = (Integer) GameSettings.getInstance().getValue("ResolutionHeight");
    }

    @Override
    public void activate () 
    {
        super.activate();
        String vLevel = (String)GameSettings.getInstance().getValue("Level");
        LevelDescription vLevelInfo = LevelEngine.getInstance().getLevelDescription(vLevel);
        mLoadImage = (BufferedImage) vLevelInfo.mLoadScreen;   
    }
    
    @Override
    public void paint(Graphics g, Frame frame) 
    {
        if (mLoadImage != null)
        {
            g.drawImage(mLoadImage, 0, 0, mScreenWidth, mScreenHeight, frame);
        }
    }
    
}
