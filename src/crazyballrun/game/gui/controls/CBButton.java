/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui.controls;

import crazyballrun.game.Game;
import crazyballrun.game.GameConstants;
import crazyballrun.game.GameSettings;
import crazyballrun.game.controls.ControlEngine;
import crazyballrun.game.controls.IListener;
import crazyballrun.game.gui.GUIControl;
import crazyballrun.game.gui.GUIEngine;
import crazyballrun.game.gui.interfaces.IControlObserver;
import crazyballrun.game.gui.interfaces.IControlSubscriber;
import crazyballrun.game.level.LevelEngine;
import crazyballrun.game.music.MusicEngine;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * The CBButtonSimple (CB = CrazyBallrun) an optional button control which may
 * be used without appropriate image. 
 * @author Timm Hoffmeister
 */
public class CBButton extends GUIControl implements IListener, IControlSubscriber {

    /**
     * Title of the button. 
     */
    private String mTitle = null;
    
    /**
     * Unique string identifier for the action to perform after pressing
     * the button. 
     */
    private String mAction = null;
    
    /**
     * Special identifier for switching menu screens. If mSwitch is set to a 
     * value not equal to "null", if will use the value as a string identifier
     * for the GUIFrame to which it switches after pressing the button.
     */
    private String mSwitch = null;
    
    /**
     * Game setting which should be added to the GameSettings. 
     */
    private String mGameSetting = null;

    /**
     * Game setting value which should be added to the GameSettings. 
     */
    private String mGameSettingValue = null;
    
    /**
     * Reference to another gui control. This control will provide the 
     * corresponding game settings value. 
     */
    private String mReference = null;
    
    /**
     * Absolute position of the button's text on the screen.
     */
    private int [] mTextPosition = new int[2];
        
    /**
     * Shows if the button is in pressed or released state.
     */
    private boolean mButtonPressed = false;

    /**
     * Rectangle for painting button's background.
     */
    private RoundRectangle2D mButtonRecBG = null;
    
    /**
     * Rectangle for painting button.
     */
    private RoundRectangle2D mButtonRec = null;

    /**
     * Color gradient for painting non-pressed button.
     */
    private GradientPaint mButtonRecGradient = null;

    /**
     * Color gradient for painting pressed button.
     */
    private GradientPaint mButtonRecGradientPressed = null;
    
    /**
     * Contructor of CBButtonSimple.
     */
    public CBButton () {
        
    }
    
    @Override
    public void initialize () 
    {
        // calculation of text and corner position (for event handling)
        int [] vPosition = mRectangle;
        mTextPosition[0] = vPosition[0] + vPosition[4]/2;
        mTextPosition[1] = vPosition[1] + vPosition[5]/2;
        
        // register button for mouse events
        if (mReference != null) {
            mMyFrame.register(mReference, this);
        }
        
        // define title font if not set
        if (mTitleFont == null) mTitleFont = GameConstants.GUI_DEFAULT_FONT;
        if (mFontColor == null) mFontColor = GameConstants.GUI_DEFAULT_FONT_COLOR;

        // render properties
        mButtonRecBG = new RoundRectangle2D.Double(mRectangle[0]-1,mRectangle[1]-1,mRectangle[4]+2,mRectangle[5]+2,21,21);
        mButtonRec = new RoundRectangle2D.Double(mRectangle[0],mRectangle[1],mRectangle[4],mRectangle[5],20,20);
        mButtonRecGradient = new GradientPaint( mRectangle[0],mRectangle[1], Color.blue, mRectangle[0] , mRectangle[3], Color.black );
        mButtonRecGradientPressed = new GradientPaint( mRectangle[0],mRectangle[1], Color.black, mRectangle[0] , mRectangle[1] + mRectangle[5] / 2, Color.gray, true );
    }
    
    @Override
    public void setAttribute(String attr, String value) {

        if (attr.compareTo("name") == 0) {
            mTitle = value;
        } else if (attr.compareTo("action") == 0) {
            mAction = value;
        } else if (attr.compareTo("switch") == 0) {
            mSwitch = value;
        } else if (attr.compareTo("setting") == 0) {
            mGameSetting = value;
        } else if (attr.equals("setting_value")) {
            mGameSettingValue = value;
        } else if (attr.compareTo("reference") == 0) {
            mReference = value;
        } else if (attr.compareTo("font") == 0) {
            setFont(value);
        } else if (attr.compareTo("font color") == 0) {
            String [] vRGB = value.split(",");
            setColor(vRGB[0], vRGB[1], vRGB[2]);
        }
    }
 
    @Override
    public void paint(Graphics g, Frame frame)
    { 
        // Render Button
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.lightGray);
        g2d.fill( mButtonRecBG );       
        g2d.setPaint( (mButtonPressed) ? mButtonRecGradientPressed : mButtonRecGradient ); 
        g2d.fill( mButtonRec );       
        
        // Render Button-Title
        g.setFont(mTitleFont);
        g.setColor(mFontColor);
        if (mDoubleFont) {
            g.setColor(Color.GRAY);
            g.drawString(mTitle, 
                mTextPosition[0] - g.getFontMetrics().stringWidth(mTitle) / 2 + 1, 
                mTextPosition[1] + 1 + (g.getFontMetrics().getHeight() / 4) );
        }
        g.drawString(mTitle, 
                mTextPosition[0] - g.getFontMetrics().stringWidth(mTitle) / 2, 
                mTextPosition[1] + (g.getFontMetrics().getHeight() / 4) );
    }

    /**
     * Performs the button's defined action. 
     */
    public void performAction () {

        if (mAction != null && mAction.equals("start_selected_level"))
        {
            CBListBox list = (CBListBox) mMyFrame.getControl(mReference); 
            String vLevel = list.getSelectedEntry();
            if (vLevel != null)
            {
                vLevel = vLevel.substring(0, vLevel.length() - 4);
                GameSettings.getInstance().addValue("Level", vLevel);
            }
        }
        
        // Perform switch of menu frame
        if (mSwitch != null) {
            GUIEngine.getInstance().setCurrentFrame(mSwitch);
        }
        
        // Perform action
        if (mAction == null) return;
        
        if (mAction.compareTo("game.exit") == 0) {
            Game.getInstance().endGame();
        } else if (mAction.equals("start_selected_level")) {
            if (mReference != null) 
            {
                String vLevel = (String) GameSettings.getInstance().getValue("Level");
                LevelEngine.getInstance().loadLevel(vLevel);
                mTitle = mGameSettingValue; 
            }
        } else if (mAction.compareTo("setting") == 0) {
            GameSettings.getInstance().addValue(mGameSetting, mGameSettingValue);
        } else if (mAction.compareTo("play") == 0) {
            MusicEngine.getInstance().setMusicState(true);
        } else if (mAction.compareTo("stop") == 0) {
            MusicEngine.getInstance().setMusicState(false);
        }
    }
    
    /**
     * Finds out if a point is inside or outside of the button.
     * @param x point-coordinate
     * @param y point-coordinate
     * @return 'true' if the point is inside the button on the screen.
     */
    private boolean isInButton(int x, int y) {
        return x > mRectangle[0] && 
               y > mRectangle[1] &&
               x < mRectangle[2] &&
               y < mRectangle[3];
    }
    
    @Override
    public synchronized void notify(Object event, String type, String sender) {
        
        // check for left mouse button release
        if (type.compareTo("mouse.release.left") == 0) {
            MouseEvent vEvent = (MouseEvent) event;
            if (mButtonPressed) {
                mButtonPressed = false;
                if (isInButton(vEvent.getX(), vEvent.getY())) {
                    performAction();
                }
            }
        }
        // check for left mouse button press
        else if (type.compareTo("mouse.press.left") == 0) {
            MouseEvent vEvent = (MouseEvent) event;
            if (isInButton(vEvent.getX(), vEvent.getY())) {
                mButtonPressed = true;
            }
        }
        
    }

    @Override
    public boolean notify(Object event, IControlObserver sender) {

        // note GameSetting value selected by another control
        if (mReference != null && 
            mGameSetting != null &&
            event instanceof String) 
        {
                mGameSettingValue = (String) event;
                return true;
            
        }
        
        return false;
    }

    @Override
    public void activate () {
        super.activate();
        ControlEngine.getInstance().register(this, "Mouse", "mouse.release.left");
        ControlEngine.getInstance().register(this, "Mouse", "mouse.press.left");
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ControlEngine.getInstance().deregister(this, "Mouse", "mouse.release.left");
        ControlEngine.getInstance().deregister(this, "Mouse", "mouse.press.left");
    }
    
}
