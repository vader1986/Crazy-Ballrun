/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level;

import crazyballrun.game.level.objects.LevelObjectParser;
import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.GameConstants;
import crazyballrun.game.GameSettingsParser;
import crazyballrun.game.controls.ControlEngine;
import crazyballrun.game.controls.EventMapper;
import crazyballrun.game.controls.IControl;
import crazyballrun.game.graphics.GraphicsEngine;
import crazyballrun.game.gui.GUIEngine;
import crazyballrun.game.music.MusicEngine;
import crazyballrun.game.physics.PhysicsEngine;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Timm
 */
public class LevelObjectParserTest {
    
    public LevelObjectParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        
            // Create GameSettings
            GameSettingsParser vParser = new GameSettingsParser();
            ArrayList <IControl> vControlList = new ArrayList<IControl>();
            EventMapper vEventMapper = new EventMapper();
            try {
                vParser.parse(GameConstants.GAME_CONFIG_FILE_PATH, vControlList, vEventMapper);
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Initialize Engines
            ControlEngine.getInstance().initialize(vEventMapper, vControlList);
            GraphicsEngine.getInstance().initialize();
            try {
                GUIEngine.getInstance().initialize();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            MusicEngine.getInstance().initialize();
            LevelEngine.getInstance().initialize();
            PhysicsEngine.getInstance().initialize();        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of parse method, of class LevelObjectParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        LevelObjectParser instance = new LevelObjectParser();
        LevelObject result = null;
        try 
        {
            result = instance.parse(GameConstants.OBJECT_RESOURCE_PATH + File.separator + "Truck" + GameConstants.OBJECT_FILE_EXTENSION);
        }
        catch (Exception e)
        {
            fail("Error while parsing: " + e.getMessage());
        }
        assertNotNull(result);
    }

}
