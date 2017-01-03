/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui.controls;

import crazyballrun.game.GameLogger;
import crazyballrun.game.gui.GUIControl;
import crazyballrun.game.gui.interfaces.IControlObserver;
import crazyballrun.game.gui.interfaces.IControlSubscriber;
import crazyballrun.game.level.LevelDescription;
import crazyballrun.game.level.LevelEngine;
import crazyballrun.game.music.MusicEngine;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import javax.naming.directory.InvalidAttributesException;

/**
 * The CBLevelDescription gui control displays information about a selected level.
 * This control has a reference to another gui control (through IControlSubscriber-
 * interface) to get a notification when and which level has been selected. 
 * @author Timm Hoffmeister
 */
public class CBLevelDescription extends GUIControl implements IControlSubscriber {

    /**
     * Maximum width of the minimap for rendering.
     */
    private static final int MINIMAP_MAX_SIZE = 150;
    
    /**
     * Reference to the gui control which provides the currently selected level
     * file. 
     */
    private String mReference = null;
    
    /**
     * Description for the currently selected level. 
     */
    private LevelDescription mCurrentDescription = null;
    
    /**
     * Reference to the LevelEngine.
     */
    private LevelEngine mLevelEngine = null;
    
    /**
     * Sets the currently painted level description.
     * @param filename name of the level-file
     */
    private synchronized void setLevelDescription (String filename) {
        String vLevelName = filename.substring(0, filename.length() - 4);
        mCurrentDescription = mLevelEngine.getLevelDescription(vLevelName);
    }

    /**
     * Gets the currently selected level description.
     * @return a reference to the current LevelDescription.
     */
    private synchronized LevelDescription getLevelDescription () {
        return mCurrentDescription;
    }
    
    @Override
    public void paint(Graphics g, Frame f) {
        LevelDescription vLD = getLevelDescription();
        if (vLD != null) {
            
            int vOffsetTop = 0, vOffsetLeft = 0, vTmpOffset = 0;
            String vTmpLine = null;
            
            // Level Name
            vOffsetTop += mRectangle[1] + 1;
            vOffsetLeft += mRectangle[0] + 1;
            g.setFont(mTitleFont);
            g.setColor(Color.GRAY);
            g.drawString(vLD.mLevelName, mRectangle[0], mRectangle[1]);
            g.setColor(mFontColor);
            g.drawString(vLD.mLevelName, vOffsetLeft, vOffsetTop);
            
            // Author
            vOffsetLeft += 5;
            vOffsetTop += g.getFontMetrics().getHeight() + 5;
            vTmpLine = "Author: " + vLD.mAuthor;
            g.drawString(vTmpLine, vOffsetLeft, vOffsetTop);            
            
            // Size
            vOffsetTop += g.getFontMetrics().getHeight();
            vTmpLine = "Size: " + vLD.mWidth + " x " + vLD.mHeight;
            g.drawString(vTmpLine, vOffsetLeft, vOffsetTop);

            // Layers
            vOffsetTop += g.getFontMetrics().getHeight();
            vTmpLine = "Layers: " + vLD.mLayers;
            g.drawString(vTmpLine, vOffsetLeft, vOffsetTop);

            // Players
            vOffsetTop += g.getFontMetrics().getHeight();
            vTmpLine = "Players: " + vLD.mNumberPlayers;
            g.drawString(vTmpLine, vOffsetLeft, vOffsetTop);
            
            // Minimaps
            if (vLD.mMiniMap != null) {
                vOffsetTop += g.getFontMetrics().getHeight();
                int vWidth = Math.min(MINIMAP_MAX_SIZE, (mRectangle[4] + 5) / vLD.mMiniMap.length );
                int vHeight = (int) (vWidth * ((double)vLD.mHeight / (double) vLD.mWidth));
                for (int i = 0; i < vLD.mMiniMap.length; i++) {
                    g.drawImage(vLD.mMiniMap[i], vOffsetLeft + i * (vWidth + 5), 
                        vOffsetTop, vWidth, vHeight, f);
                }
                vOffsetTop += vHeight;
                vOffsetTop += g.getFontMetrics().getHeight();
            }
            
            // Textual description
            String [] vSplit = vLD.mDescription.split(" ");
            vTmpOffset = 0;
            for (int i = 0; i < vSplit.length; i++) {
                if (vOffsetLeft + vTmpOffset + g.getFontMetrics().stringWidth(" " + vSplit[i]) > mRectangle[2]) {
                    vOffsetTop += g.getFontMetrics().getHeight();
                    g.drawString(vSplit[i], vOffsetLeft, vOffsetTop);
                    vTmpOffset = g.getFontMetrics().stringWidth(vSplit[i]);
                } else {
                    g.drawString(" " + vSplit[i], vOffsetLeft + vTmpOffset, vOffsetTop);
                    vTmpOffset += g.getFontMetrics().stringWidth(" " + vSplit[i]);
                }
            }
        }
    }
    
    @Override
    public void setAttribute(String attr, String value) {
        if (attr.compareTo("reference") == 0) {
            mReference = value;
        } else if (attr.compareTo("font") == 0) {
            setFont(value);
        } else if (attr.compareTo("font color") == 0) {
            String [] vRGB = value.split(",");
            setColor(vRGB[0], vRGB[1], vRGB[2]);
        } 
    }

    @Override
    public void initialize() {
        
        mLevelEngine = LevelEngine.getInstance();
        
        // Registration for "selected level changed" events
        try {
            if (mReference == null)
                throw new InvalidAttributesException ("CBLevelDescription has been initialized without specifying the obligathory 'reference'-attribute. ");
            else {
                GUIControl vControl = mMyFrame.getControl(mReference);
                if (vControl == null)
                    throw new IllegalArgumentException ("CBLevelDescription has been initialized with an invalid 'reference'-attribute. The corresponding GUIControl does not exist.");
                else {
                    if (vControl instanceof IControlObserver) {
                        ((IControlObserver)vControl).register(this);
                    } else {
                        throw new IllegalArgumentException ("The 'reference'-attribute value links an invalid IControlObserver-implementation.");
                    }
                }
            }
        } catch (Exception e) {
            GameLogger.log(e);
        }
        
    }

    @Override
    public boolean notify(Object event, IControlObserver sender) {

        // Notification of change of selected level
        if (event instanceof String) {
            String vSelectedLevel = (String) event;
            setLevelDescription(vSelectedLevel);
            LevelDescription vTmp = getLevelDescription();
            if (vTmp != null)
                MusicEngine.getInstance().playMusicFile(vTmp.mDefaultSound, false, false);
        }
        else
            return false;
        
        return true;
    }
        
}
