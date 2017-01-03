/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.physics.bodies;

import java.io.File;
import crazyballrun.game.physics.PhysicsEngine;
import crazyballrun.game.level.LevelEngine;
import crazyballrun.game.music.MusicEngine;
import crazyballrun.game.gui.GUIEngine;
import crazyballrun.game.graphics.GraphicsEngine;
import crazyballrun.game.controls.ControlEngine;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import crazyballrun.game.GameConstants;
import crazyballrun.game.controls.EventMapper;
import crazyballrun.game.controls.IControl;
import java.util.ArrayList;
import crazyballrun.game.GameSettingsParser;
import crazyballrun.game.level.Level;
import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.utilities.Vector2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing class for Vehicle-class.
 * 
 * @author Timm Hoffmeister
 */
public class VehicleTest {
    
    public VehicleTest() {
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
     * Test of isColliding method, of class Vehicle.
     */
    @Test
    public void testIsColliding_double_double() {
        System.out.println("isColliding");
        LevelObject vObj = LevelEngine.getInstance().loadObject("Truck");
        Level vLevel = new Level();
        vLevel.create("SimpleMap");
        vObj.setLevel(vLevel);
        vObj.setPosition(new Vector2D(0.0,0.0), 0);
        vObj.initialize();

        Vehicle instance = (Vehicle)vObj.getObjectModel();
        instance.updateData();

        // some point in the middle of the truck
        assertTrue(instance.isColliding(0.234375, 0.234375));

        // point on the right of the truck
        assertFalse(instance.isColliding(0.234375 * 2 + 0.01, 0.234375));
        
        // rotate the truck 90 degrees to the right
        instance.updatePolygon(Math.PI / 2);

        assertFalse(instance.isColliding(0.234375 * 2, 0.234375));
        assertFalse(instance.isColliding(0.546875, 0.234375));
    }

    /**
     * Test of isColliding method, of class Vehicle.
     */
    @Test
    public void testIsColliding_RigidBody() {
        System.out.println("isColliding");

        LevelObject vObj = LevelEngine.getInstance().loadObject("Truck");
        LevelObject vObjColl = LevelEngine.getInstance().loadObject("Truck");
        LevelObject vObjColl2 = LevelEngine.getInstance().loadObject("Truck");
        
        Level vLevel = new Level();
        vLevel.create("SimpleMap");

        vObj.setLevel(vLevel);
        vObjColl.setLevel(vLevel);
        vObjColl2.setLevel(vLevel);
            
        for (int offset = 0; offset < 5; offset++)
        {
            // Create level objects
            vObj.setPosition(new Vector2D(0.0 + offset,0.0 + offset), 0);
            vObj.initialize();

            vObjColl.setPosition(new Vector2D(0.09375 + offset,0.140625 + offset), 0);
            vObjColl.initialize();

            vObjColl2.setPosition(new Vector2D(0.3359375 + offset,0.0 + offset), 0);
            vObjColl2.initialize();

            // Initialize physical models
            Vehicle instance = (Vehicle)vObj.getObjectModel();
            Vehicle instance_coll = (Vehicle)vObjColl.getObjectModel();
            Vehicle instance_no_coll = (Vehicle)vObjColl2.getObjectModel();
            instance.updateData();
            instance_coll.updateData();
            instance_no_coll.updateData();
            
            // perform tests
            assertTrue(instance.isColliding(instance_coll));
            assertFalse(instance.isColliding(instance_no_coll));

            // rotate the truck 90 degrees to the right
            instance.updatePolygon(Math.PI / 2);
            instance.createCollisionPoints();
            assertFalse(instance.isColliding(instance_no_coll));

            // some other angle
            instance.updatePolygon(Math.PI / 3);
            instance.createCollisionPoints();
            assertTrue(instance.isColliding(instance_coll));
            assertTrue(!instance.isColliding(instance_no_coll));
        }
    }

    /**
     * Test of isRigidBody method, of class Vehicle.
     */
    @Test
    public void testIsRigidBody() {
        System.out.println("isRigidBody");
        LevelObject vObj = LevelEngine.getInstance().loadObject("Truck");
        Vehicle instance = (Vehicle)vObj.getObjectModel();
        assertTrue(instance.isRigidBody());
    }

    private void update (Vehicle v1, Vehicle v2, int update)
    {
        v1.update(0.1, 0.0, 0.0);
        v1.updateData();
        if (v1.isColliding(v2))
            v1.collisionReactionObject(0.1);
        v1.updateData();

        v2.update(0.1, 0.0, 0.0);
        v2.updateData();
        if (v2.isColliding(v1))
            v2.collisionReactionObject(0.1);
        v2.updateData();
    }
    
    private void output (Vehicle v1, Vehicle v2, String name)
    {
        try 
        {
            BufferedImage img = ImageIO.read(new File("Input.bmp"));
            v1.paintCollisionPoints(img, Color.green);
            v2.paintCollisionPoints(img, Color.red);
            ImageIO.write(img, "bmp", new File(name));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }      
    }

    /**
     * Tests the getBoundingSphere-method.
     */
    @Test
    public void testGetBoundingSphere()
    {
        System.out.println("getBoundingSphere");

        LevelObject vObj = LevelEngine.getInstance().loadObject("Truck");
        LevelObject vObjColl = LevelEngine.getInstance().loadObject("BigTruck");

        // Create level
        Level vLevel = new Level();
        vLevel.create("SimpleMap");

        vObj.setLevel(vLevel);
        vObjColl.setLevel(vLevel);
        
        // Create level objects
        vObj.setPosition(new Vector2D(1.0,3.0), 0);
        vObj.initialize();
        vObjColl.setPosition(new Vector2D(1.0,0.5), 0);
        vObjColl.initialize();        

        Vehicle instance = (Vehicle)vObj.getObjectModel();
        Vehicle instance_coll = (Vehicle)vObjColl.getObjectModel();

        instance.updateData();
        instance_coll.updateData();

        assertEquals(0.3069825422560703, instance.getBoundingSphere(), 0.0000001);
        assertEquals(0.30428659522110074, instance_coll.getBoundingSphere(), 0.0000001);
    }
    
    /**
     * Tests the update-method of RigidBody (combined with collision reaction).
     */
    @Test
    public void testUpdate() 
    {
        System.out.println("update");
        
        LevelObject vObj = LevelEngine.getInstance().loadObject("Truck");
        LevelObject vObjColl = LevelEngine.getInstance().loadObject("BigTruck");

        // Create level
        Level vLevel = new Level();
        vLevel.create("SimpleMap");

        vObj.setLevel(vLevel);
        vObjColl.setLevel(vLevel);
        
        // Create level objects
        vObj.setPosition(new Vector2D(1.0,2.0), 0);
        vObj.initialize();
        vObjColl.setPosition(new Vector2D(1.0,0.5), 0);
        vObjColl.initialize();        

        Vehicle instance = (Vehicle)vObj.getObjectModel();
        Vehicle instance_coll = (Vehicle)vObjColl.getObjectModel();

        instance.updateData();
        instance_coll.updateData();

        //output(instance, instance_coll, "Output0.bmp");
        
        // apply forces/torques
        vObj.getObjectModel().applyRotation(true);
        instance.setRotation(0.0 * Math.PI);
        instance_coll.setRotation(1.0 * Math.PI);

        instance.applyAcceleration(true);
        instance_coll.applyAcceleration(true);
        
        update(instance, instance_coll, 0);

        vObj.getObjectModel().detachRotation();
        
        update(instance, instance_coll, 0);
        update(instance, instance_coll, 0);

        // update position (no rotation)
        for (int i = 1; i < 40; i++)
            update(instance, instance_coll, i);
        
    }
    
}
