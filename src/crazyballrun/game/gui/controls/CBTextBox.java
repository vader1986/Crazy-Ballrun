/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui.controls;

import crazyballrun.game.GameConstants;
import crazyballrun.game.GameSettings;
import crazyballrun.game.controls.ControlEngine;
import crazyballrun.game.controls.IListener;
import crazyballrun.game.gui.GUIControl;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * CBTextBox is a user interface control for text input. After activision it 
 * registers for all possible input-letters and -signs in form of key-events. 
 * After deactivision it puts its content into the GameSettings as configured
 * in the GUI configuration file. 
 * 
 * @author Timm Hoffmeister
 */
public class CBTextBox extends GUIControl implements IListener
{
    /**
     * Current text input of the user. 
     */
    private String mCurrentText = "";
    
    /**
     * Specifies the type of input (e.g. IP-address).
     */
    private String mInputType = null;
    
    /**
     * Which setting to add to the GameSettings after deactivision. 
     */
    private String mSetting = null;
    
    /**
     * Textbox has the focus, so it allows user input.
     */
    private boolean mFocus = false;

    /**
     * Rectangle for painting textbox's background.
     */
    private RoundRectangle2D mBackBounds = null;
    
    /**
     * Rectangle for painting textbox.
     */
    private RoundRectangle2D mBounds = null;

    /**
     * Color gradient for painting textbox.
     */
    private GradientPaint mGradient = null; 
    
    /**
     * Absolute position of the textbox's text on the screen.
     */
    private int [] mTextPosition = new int[2];
    
    /**
     * Constructor of CBTextBox. 
     */
    public CBTextBox () 
    {
        
    }
    
    /**
     * Gets the focus of the textbox.
     * @return 'true' if the focus is set to the textbox
     */
    private synchronized boolean getFocus ()
    {
        return mFocus;
    }
    
    /**
     * Sets the focus of the textbox.
     * @param focus new focus
     */
    private synchronized void setFocus (boolean focus)
    {
        mFocus = focus;
    }
    
    /**
     * Gets the current text of the text box. 
     * @return current text for rendering
     */
    private synchronized String getText ()
    {
        return mCurrentText;
    }
    
    /**
     * Adds text to the current text of the CBTextBox.
     * @param text text to add
     */
    private synchronized void addText (String text)
    {
        mCurrentText += text;
    }
    
    /**
     * Removes the last character of the text. 
     */
    private synchronized void delText ()
    {
        if (!mCurrentText.isEmpty())
            mCurrentText = mCurrentText.substring(0, mCurrentText.length() - 1);
    }
    
    @Override
    public void setAttribute(String attr, String value) 
    {
        if (attr.equals("input"))
        {
            mInputType = value;
        }
        else if (attr.equals("storage"))
        {
            mSetting = value;
        }
        else if (attr.compareTo("font") == 0) 
        {
            setFont(value);
        } 
        else if (attr.compareTo("font color") == 0) 
        {
            String [] vRGB = value.split(",");
            setColor(vRGB[0], vRGB[1], vRGB[2]);
        }
    }

    @Override
    public void initialize() 
    {
        // calculation of text and corner position (for event handling)
        int [] vPosition = mRectangle;
        mTextPosition[0] = vPosition[0] + vPosition[4]/2;
        mTextPosition[1] = vPosition[1] + vPosition[5]/2;
        
        // define title font if not set
        if (mTitleFont == null) mTitleFont = GameConstants.GUI_DEFAULT_FONT;
        if (mFontColor == null) mFontColor = GameConstants.GUI_DEFAULT_FONT_COLOR;

        // render properties
        mBackBounds = new RoundRectangle2D.Double(mRectangle[0]-1,mRectangle[1]-1,mRectangle[4]+2,mRectangle[5]+2,0,0);
        mBounds = new RoundRectangle2D.Double(mRectangle[0],mRectangle[1],mRectangle[4],mRectangle[5],0,0);
        mGradient = new GradientPaint( mRectangle[0],mRectangle[1], Color.blue, mRectangle[0] , mRectangle[3], Color.black );
    }
    
    @Override
    public void paint(Graphics g, Frame frame) 
    {
        // Render Button
        Graphics2D g2d = (Graphics2D) g;
        if (getFocus())
            g2d.setPaint(Color.yellow);
        else
            g2d.setPaint(Color.lightGray);
        g2d.fill( mBackBounds );       
        g2d.setPaint( mGradient ); 
        g2d.fill( mBounds );       
        
        // Render Button-Title
        g.setFont(mTitleFont);
        g.setColor(mFontColor);
        String text = getText();
        if (mDoubleFont) 
        {
            g.setColor(Color.GRAY);
            g.drawString(text, 
                mTextPosition[0] - g.getFontMetrics().stringWidth(text) / 2 + 1, 
                mTextPosition[1] + 1 + (g.getFontMetrics().getHeight() / 4) );
        }
        g.drawString(text, 
                mTextPosition[0] - g.getFontMetrics().stringWidth(text) / 2, 
                mTextPosition[1] + (g.getFontMetrics().getHeight() / 4) );
    }

    /**
     * Finds out if a point is inside or outside of the text box.
     * @param x point-coordinate
     * @param y point-coordinate
     * @return 'true' if the point is inside the text box on the screen
     */
    private boolean insideTextBox(int x, int y) 
    {
        return x > mRectangle[0] && 
               y > mRectangle[1] &&
               x < mRectangle[2] &&
               y < mRectangle[3];
    }
    
    @Override
    public void notify(Object event, String type, String sender) 
    {
        if (type.equals("mouse.press.left"))
        {
            // receive mouse event
            MouseEvent mouseEvent = (MouseEvent) event;
            
            // set focus to the textbox
            if (insideTextBox(mouseEvent.getX(), mouseEvent.getY()))
            {
                setFocus(true);
            }
            // release focus from the textbox
            else
            {
                setFocus(false);
            }
        }
        else if (getFocus() && type.equals("key.press.all"))
        {
            // receive key event
            KeyEvent keyEvent = (KeyEvent) event;
            if (mInputType == null)
            {
                if (keyEvent.getKeyCode() == 10)
                {
                    // loose focus on RETURN
                    setFocus(false);
                }
                else if (keyEvent.getKeyCode() == 16 ||
                         keyEvent.getKeyCode() == 20)
                {
                    // do nothing on SHIFT
                }
                else if (keyEvent.getKeyCode() == 8)
                {
                    // Backshift
                    delText();
                }
                else
                {
                    // add char of key to the text
                    String newChar = "" + keyEvent.getKeyChar();
                    if (keyEvent.isShiftDown())
                        newChar = newChar.toUpperCase();
                    addText(newChar);
                }
            }
        }
    }
    
    @Override
    public void activate () 
    {
        super.activate();
        GameSettings.getInstance().deleteValue(mSetting);
        ControlEngine.getInstance().register(this, "Keyboard", "key.press.all");
        ControlEngine.getInstance().register(this, "Mouse", "mouse.press.left");
    }

    @Override
    public void deactivate() 
    {
        super.deactivate();
        GameSettings.getInstance().addValue(mSetting, mCurrentText);
        ControlEngine.getInstance().deregister(this, "Keyboard", "key.press.all");
        ControlEngine.getInstance().deregister(this, "Mouse", "mouse.press.left");
    }
    
    
}
