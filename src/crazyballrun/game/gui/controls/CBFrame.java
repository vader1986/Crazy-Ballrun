/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui.controls;

import crazyballrun.game.GameConstants;
import crazyballrun.game.gui.GUIControl;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

/**
 * The CBFrameSimple (CB = CrazyBallrun) is a basic GUI-Control to combine 
 * several other GUIControls on it visually. 
 * @author Timm Hoffmeister
 */
public class CBFrame extends GUIControl {
    
    /**
     * Title of the frame. 
     */
    private String mTitle = null;
        
    /**
     * Absolute position of the frame's text on the screen.
     */
    private int [] mTextPosition = new int[2];
    
    /**
     * Y-offset of the title's position in "GUIElementPixelSize" (-> GameSettings).
     */
    private int mTitleOffset = 0;
    
    /**
     * Rectangle for painting frame's background.
     */
    private RoundRectangle2D mFrameRecBG = null;
    
    /**
     * Rectangle for painting frames.
     */
    private RoundRectangle2D mFrameRec = null;

    /**
     * Color gradient for painting frames.
     */
    private GradientPaint mFrameRecGradient = null;    
    
    /**
     * Constructor for CBFrameSimple.
     */
    public CBFrame () {
        
    }
    
    @Override
    public void setAttribute(String attr, String value) {
        
        if (attr.compareTo("title") == 0) {
            mTitle = value;
        } else if (attr.compareTo("font") == 0) {
            setFont(value);
        } else if (attr.compareTo("font color") == 0) {
            String [] vRGB = value.split(",");
            setColor(vRGB[0], vRGB[1], vRGB[2]);
        } else if (attr.compareTo("title offset") == 0) {
            mTitleOffset = Integer.parseInt(value);
        } 

    }

    @Override
    public void initialize() {

        // Title position of the frame
        int [] vPosition = mRectangle;
        mTextPosition[0] = vPosition[0] + vPosition[4]/2;
        mTextPosition[1] = vPosition[1] + sElementPixelSize / 2 + mTitleOffset * sElementPixelSize;

        // Define title font if not set
        if (mTitleFont == null) mTitleFont = GameConstants.GUI_DEFAULT_FONT;
        if (mFontColor == null) mFontColor = GameConstants.GUI_DEFAULT_FONT_COLOR;

        // render properties
        Color c1 = new Color (Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), 128);
        Color c2 = new Color (Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), 128);
        mFrameRecBG = new RoundRectangle2D.Double(mRectangle[0]-1,mRectangle[1]-1,mRectangle[4]+2,mRectangle[5]+2,21,21);
        mFrameRec = new RoundRectangle2D.Double(mRectangle[0],mRectangle[1],mRectangle[4],mRectangle[5],20,20);
        mFrameRecGradient = new GradientPaint( mRectangle[0],mRectangle[1], c1, mRectangle[0] + mRectangle[4] / 2, mRectangle[1] + mRectangle[5] / 2, c2, true );
    }

    @Override
    public void paint(Graphics g, Frame frame) {
        
        // Render Button
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.lightGray);
        g2d.fill( mFrameRecBG );
        g2d.setPaint( mFrameRecGradient ); 
        g2d.fill( mFrameRec );         
        
        // Render Frame-Title
        if (mTitle != null) {
            g.setFont(mTitleFont);
            if (mDoubleFont) {
                g.setColor(Color.GRAY);
                g.drawString(mTitle, 
                    mTextPosition[0] - g.getFontMetrics().stringWidth(mTitle) / 2 + 1, 
                    mTextPosition[1] + 1);
            }
            g.setColor(mFontColor);
            g.drawString(mTitle, 
                mTextPosition[0] - g.getFontMetrics().stringWidth(mTitle) / 2, 
                mTextPosition[1]);
        }
    }    
}
