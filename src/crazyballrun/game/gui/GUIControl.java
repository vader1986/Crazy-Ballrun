/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui;

import crazyballrun.game.GameConstants;
import crazyballrun.game.GameLogger;
import crazyballrun.game.GameSettings;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Locale;

/**
 * The abstract class GUIControl provides basic functionality for GUI controls
 * and an interface for the implementation of GUI-elements. 
 * @author Timm Hoffmeister
 */
public abstract class GUIControl 
{
    /**
     * Local copy of "GUIElementPixelSize" (-> GameSettings).
     */
    protected static int sElementPixelSize;
    
    /**
     * Unique identifier of the control inside of its GUIFrame.
     */
    private String mId = null;
    
    /**
     * Width of the control in "GUIElementPixelSize" (-> GameSettings).
     */
    private int mWidth = 0;
    
    /**
     * Height of the control in "GUIElementPixelSize" (-> GameSettings).
     */
    private int mHeight = 0;

    /**
     * Font to use for rendering the control's text. 
     */
    protected Font mTitleFont = GameConstants.GUI_DEFAULT_FONT;
    
    /**
     * Font color of the text.
     */
    protected Color mFontColor = GameConstants.GUI_DEFAULT_FONT_COLOR;

    /**
     * Paint the font with some shadow-background.
     */
    protected boolean mDoubleFont = false;        
    
    /**
     * This explicitly stores the absolute position and size data of the 
     * GUIControl on the screen (in pixels). mRectangle[0/1] is the x/y-
     * coordinate of the position of the left+top corner of the control, 
     * mRectangle[2/3] the x/y-coordinate of the right+bottom corner of the 
     * control. mRectangle[4/5] is the width/height in pixels.
     */
    protected int [] mRectangle = new int [6];    
    
    /**
     * Root filename of the image (without "_[x]_[y].png"-file-ending).
     */
    protected String mImageName = null;
    
    /**
     * Defines if the control is visible/active or not. 
     */
    protected boolean mIsActive = false;

    /**
     * Reference to the GUIFrame containing the GUIControl.
     */
    protected GUIFrame mMyFrame = null;

    /**
     * Sets the font color to the specified rgb-value.
     * @param r red color (0-255)
     * @param g green color (0-255)
     * @param b blue color (0-255)
     */
    protected void setColor (String r, String g, String b) {
        try
        {
            if (r != null && g != null && b != null) {
                mFontColor = new Color(
                        Integer.parseInt(r), 
                        Integer.parseInt(g),
                        Integer.parseInt(b));
            }
        }
        catch (Exception e) 
        {
            GameLogger.log(e);
        }
    }
    
    /**
     * Sets the font of the title. 
     * @param font string representation of the font: [fontname],[fontsize]
     * [,optional:font attribute]
     */
    protected void setFont (String font) {
        String [] vFontAttr = font.split(",");
        int vStyle = Font.PLAIN;

        // optional font attributes
        for (int i = 2; i < vFontAttr.length; i++) {
            String attr = vFontAttr[i].toUpperCase(Locale.getDefault());
            if (attr.compareTo("DOUBLE") == 0) {
                mDoubleFont = true;
                vStyle = Font.BOLD;
            } else if (attr.compareTo("BOLD") == 0) {
                if (vStyle == Font.ITALIC)
                    vStyle = Font.BOLD + Font.ITALIC;
                else
                    vStyle = Font.BOLD;
            } else if (attr.compareTo("ITALIC") == 0) {
                if (vStyle == Font.BOLD)
                    vStyle = Font.ITALIC + Font.BOLD;
                else
                    vStyle = Font.ITALIC;
            }
        }    

        // create font
        mTitleFont = new Font(vFontAttr[0], vStyle, Integer.parseInt(vFontAttr[1]));
    }    
    
    /**
     * Creates the GUIControl from given input.
     * @param id identifier of the control
     * @param frame corresponding reference to the frame
     * @param x1 x-coordinate of the left/top corner (between 0 and 1, resolution independent)
     * @param y1 y-coordinate of the left/top corner (between 0 and 1, resolution independent)
     * @param x2 x-coordinate of the right/bottom corner (between 0 and 1, resolution independent)
     * @param y2 y-coordinate of the right/bottom corner (between 0 and 1, resolution independent)
     */
    public final void create (String id, GUIFrame frame, double x1, double y1, double x2, double y2) {
        setId(id);
        setFrame(frame);
        setRectangle(x1, y1, x2, y2);
    }
    
    /**
     * Sets the reference to the corresponding GUIFrame of the control.
     * @param frame reference to the GUIFrame of the GUIControl
     */
    private void setFrame (GUIFrame frame) {
        mMyFrame = frame;
    }

    /**
     * Gets the height of the control in "GUIElementPixelSize" (-> GameSettings).
     * @return the height of the control.
     */
    protected int getHeight () {
        return mHeight;
    } 
    
    /**
     * Gets the width of the control in "GUIElementPixelSize" (-> GameSettings).
     * @return the width of the control.
     */
    protected int getWidth () {
        return mWidth;
    } 
    
    /**
     * Gets the unique string identifier of the control.
     * @return the control's id.
     */
    public final String getId () {
        return mId;
    }
    
    /**
     * Sets the control's id. 
     * @param id unique identifier inside of the control's frame
     */
    private void setId (String id) {
        mId = id;
    }
    
    /**
     * Sets the height of the control specified in "GUIElementPixelSize" (-> 
     * GameSettings). Minimum height is 2.
     * @param height height of the control
     */
    private void setHeight(int height) {
        if (height > 1)
            mHeight = height;
        else
            mHeight = 2;
    }
    
    /**
     * Sets the width of the control specified in "GUIElementPixelSize" (-> 
     * GameSettings). Minimum width is 2.
     * @param width width of the control
     */
    private void setWidth(int width) {
        if (width > 1)
            mWidth = width;
        else
            mWidth = 2;
    }
        
    /**
     * Sets the position and size of the control.
     * @param x1 x-coordinate of the left/top corner of the control
     * @param y1 y-coordinate of the left/top corner of the control
     * @param x2 x-coordinate of the right/bottom corner of the control
     * @param y2 y-coordinate of the right/bottom corner of the control
     */
    private void setRectangle (double x1, double y1, double x2, double y2) {
        GameSettings vSettings = GameSettings.getInstance();
        sElementPixelSize = (Integer)(vSettings.getValue("GUIElementPixelSize"));
        
        // Position on the screen: [0,1] * Screen-Resolution
        double vResolutionX = (Integer)vSettings.getValue("ResolutionWidth");
        double vResolutionY = (Integer)vSettings.getValue("ResolutionHeight");
        mRectangle[0] = (int) (x1 * vResolutionX);
        mRectangle[1] = (int) (y1 * vResolutionY);
        mRectangle[2] = (int) (x2 * vResolutionX);
        mRectangle[3] = (int) (y2 * vResolutionY);
        
        // Size of the control
        mRectangle[4] = mRectangle[2] - mRectangle[0];
        mRectangle[5] = mRectangle[3] - mRectangle[1];
        setWidth( (mRectangle[2] - mRectangle[0]) / sElementPixelSize );
        setHeight( (mRectangle[3] - mRectangle[1]) / sElementPixelSize );
    }
    
    /**
     * Sets an attribute to the specified value. 
     * @param attr name of the attribute
     * @param value value for the attribute
     */
    public abstract void setAttribute (String attr, String value);
    
    /**
     * After finishing the creation of a gui control this method is called to
     * finish control specific initilization of the GUIControl implementation. 
     */
    public abstract void initialize ();

    /**
     * Activates the GUIControl.
     */
    public void activate () {
        mIsActive = true;
    }

    /**
     * Deactivates the GUIControl.
     */
    public void deactivate() {
        mIsActive = false;
    }
    
    /**
     * Paints the control to the specified Graphics-reference. 
     * @param g Graphics-reference for rendering
     * @param frame Frame-reference where to paint the control
     */
    public abstract void paint(Graphics g, Frame frame);
    
}
