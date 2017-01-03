/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui;

import crazyballrun.game.Game;
import crazyballrun.game.GameConstants;
import crazyballrun.game.GameFunctions;
import crazyballrun.game.GameSettings;
import crazyballrun.game.graphics.Camera;
import crazyballrun.game.level.Level;
import crazyballrun.game.level.controllers.Player;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * The GUIEngine interacts strongly with the GraphicsEngine for rendering of
 * graphical user interface and additionally handles the IControl-GUI-
 * interaction. It also loads the GUI-configuration file which specifies where
 * to place which GUI-Control and actions caused by activision. 
 * @author Timm Hoffmeister
 */
public class GUIEngine 
{
    
    /**
     * List of the GUIFrames of the gui (all menu screens).
     */
    private HashMap <String, GUIFrame> mGuiFrames = null;

    /**
     * Stores the id of the current menu frame. 
     */
    private String mCurrentFrame = null;
        
    /**
     * Reference to the GUI engine. 
     */
    private static GUIEngine engine = null;
    
    /**
     * Provides access to the GUI engine.
     * @return a reference to the GUIEngine. 
     */
    public static GUIEngine getInstance() {
        if (engine == null)
            engine = new GUIEngine();
        return engine;
    }
    
    /**
     * Private constructor for GUIEngine. 
     */
    private GUIEngine() 
    {
        mGuiFrames = new HashMap<String, GUIFrame>();
    }

    /**
     * Initializes the GUIEngine by doing nothing ...
     */
    public void initialize ()
    {

    }
    
    /**
     * Parses the GUI-configuration file and loads the gui elements. 
     * @throws ParserConfigurationException error during xml-parsing
     * @throws SAXException xml-file is not xml-conform
     * @throws IOException file not found
     */
    public void start () throws ParserConfigurationException, SAXException, IOException 
    {
        // parsing the GUI configuration file
        GUIConfigurationParser vParser = new GUIConfigurationParser();
        vParser.parse(GameConstants.GUI_CONFIGURATION_PATH, mGuiFrames);

        // Set current frame to the main menu screen
        setCurrentFrame(GameConstants.GUI_START_FRAME_ID);
    }
    
    /**
     * Creates camera-displays.
     * @param level reference to the level
     */
    public void createCameraDisplays (Level level) 
    {
        int vWidth = (Integer) GameSettings.getInstance().getValue("ResolutionWidth");
        int vHeight = (Integer) GameSettings.getInstance().getValue("ResolutionHeight");

        if (Game.getInstance().getGameMode() != Game.GameMode.SP_SPLITSCREEN)
        {
            // One player camera
            Player vPlayer = level.getPlayers().get(GameFunctions.sPlayerNumber[0]);
            Camera vCamera = new Camera();
            vCamera.create(level);
            vPlayer.setCamera(vCamera);            
            vPlayer.getCamera().setDisplay(
                    (int)(0.1 * vWidth), (int)(0.1 * vHeight), (int)(0.8 * vWidth), (int)(0.8 * vHeight));
        }
        else
        {
            String vPlayers = (String) GameSettings.getInstance().getValue("SplitScreenPlayers");
            
            if (vPlayers.equals("2"))
            {
                // Player 1
                Player vPlayer1 = level.getPlayers().get(GameFunctions.sPlayerNumber[0]);
                Camera vCamera1 = new Camera();
                vCamera1.create(level);
                vPlayer1.setCamera(vCamera1);
                vPlayer1.getCamera().setDisplay(0, 0, vWidth-1, vHeight / 2 - 1);

                // Player 2
                Player vPlayer2 = level.getPlayers().get(GameFunctions.sPlayerNumber[1]);
                Camera vCamera2 = new Camera();
                vCamera2.create(level);
                vPlayer2.setCamera(vCamera2);
                vPlayer2.getCamera().setDisplay(0, vHeight / 2 - 1, vWidth-1, vHeight / 2 - 1);
            }
            else if (vPlayers.equals("4"))
            {
                // Player 1
                Player vPlayer1 = level.getPlayers().get(GameFunctions.sPlayerNumber[0]);
                Camera vCamera1 = new Camera();
                vCamera1.create(level);
                vPlayer1.setCamera(vCamera1);
                vPlayer1.getCamera().setDisplay(0, 0, vWidth / 2 - 1, vHeight / 2 - 1);

                // Player 2
                Player vPlayer2 = level.getPlayers().get(GameFunctions.sPlayerNumber[1]);
                Camera vCamera2 = new Camera();
                vCamera2.create(level);
                vPlayer2.setCamera(vCamera2);
                vPlayer2.getCamera().setDisplay(vWidth / 2 - 1, 0, vWidth / 2 - 1, vHeight / 2 - 1);

                // Player 3
                Player vPlayer3 = level.getPlayers().get(GameFunctions.sPlayerNumber[2]);
                Camera vCamera3 = new Camera();
                vCamera3.create(level);
                vPlayer3.setCamera(vCamera3);
                vPlayer3.getCamera().setDisplay(0, vHeight / 2 - 1, vWidth / 2 - 1, vHeight / 2 - 1);

                // Player 4
                Player vPlayer4 = level.getPlayers().get(GameFunctions.sPlayerNumber[3]);
                Camera vCamera4 = new Camera();
                vCamera4.create(level);
                vPlayer4.setCamera(vCamera4);
                vPlayer4.getCamera().setDisplay(vWidth / 2 - 1, vHeight / 2 - 1, vWidth / 2 - 1, vHeight / 2 - 1);
            }
            else
            {
                throw new IllegalStateException("Invalid number of players for splitscreen-mode: " + vPlayers);
            }
        }
    }
    
    /**
     * Sets the current menu frame by its string identifier.
     * @param id the frame's id
     */
    public synchronized void setCurrentFrame (String id) {
        GUIFrame vCurFrame = null;
        if (mCurrentFrame != null && mCurrentFrame.compareTo(GameConstants.GUI_NO_FRAME_ID) != 0)
            vCurFrame = mGuiFrames.get(mCurrentFrame);

        // deactivate old frame
        if (vCurFrame != null)
            vCurFrame.setInActive();

        // activate new frame
        mCurrentFrame = id;
        vCurFrame = mGuiFrames.get(mCurrentFrame);
        if (vCurFrame != null)
            vCurFrame.setActive();
    }

    /**
     * Finds the currently menu frame to be rendered.
     * @return a reference to the current GUIFrame.
     */
    public synchronized GUIFrame getCurrentFrame () {
        GUIFrame result = null;
        if (mCurrentFrame != null && 
                mCurrentFrame.compareTo(GameConstants.GUI_NO_FRAME_ID) != 0)
            result = mGuiFrames.get(mCurrentFrame);
        return result;
    }
    
}
