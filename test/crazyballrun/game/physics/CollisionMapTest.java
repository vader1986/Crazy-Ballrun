/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.physics;

import crazyballrun.game.physics.CollisionTexture.GroundProperty;
import crazyballrun.game.utilities.Vector2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test-class for the CollisionMap.
 * @author Timm Hoffmeister
 */
public class CollisionMapTest {
    
    public CollisionMapTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setCollisionTexture method, of class CollisionMap.
     */
    @Test
    public void testSetCollisionTexture() {
        System.out.println("setCollisionTexture");
        try
        {
            BufferedImage image = ImageIO.read(this.getClass().getResource("CollisionMapTest.bmp"));
            CollisionTexture instance = new CollisionTexture();
            instance.setCollisionTexture(image);
            GroundProperty result = instance.getGroundProperty(new Vector2D(0,0.15625)); // (0,10)
            GroundProperty expResult = GroundProperty.DEFAULT;
            assertTrue(result == expResult);
        }
        catch (Exception e) 
        {
            fail("Could not load image file.");
        }
    }

    /**
     * Test of getGroundProperty method, of class CollisionMap.
     */
    @Test
    public void testGetGroundProperty() {
        System.out.println("getGroundProperty");
        try
        {
            Vector2D point1 = new Vector2D(0, 0);
            Vector2D point2 = new Vector2D(0.015625, 0);
            Vector2D point3 = new Vector2D(2 * 0.015625, 0);
            Vector2D point4 = new Vector2D(3 * 0.015625, 0);
            Vector2D point5 = new Vector2D(4 * 0.015625, 0);
            Vector2D point6 = new Vector2D(5 * 0.015625, 0);
            Vector2D point7 = new Vector2D(6 * 0.015625, 0);

            BufferedImage image = ImageIO.read(this.getClass().getResource("CollisionMapTest.bmp"));
            CollisionTexture instance = new CollisionTexture();
            instance.setCollisionTexture(image);

            GroundProperty expResult1 = GroundProperty.WATER_GROUND;
            GroundProperty expResult2 = GroundProperty.WALL;
            GroundProperty expResult3 = GroundProperty.HIGH_FRICTION;
            GroundProperty expResult4 = GroundProperty.ICE_GROUND;
            GroundProperty expResult5 = GroundProperty.NEXT_LAYER;
            GroundProperty expResult6 = GroundProperty.PREVIOUS_LAYER;
            GroundProperty expResult7 = GroundProperty.LOW_FRICTION;

            GroundProperty result1 = instance.getGroundProperty(point1);
            GroundProperty result2 = instance.getGroundProperty(point2);
            GroundProperty result3 = instance.getGroundProperty(point3);
            GroundProperty result4 = instance.getGroundProperty(point4);
            GroundProperty result5 = instance.getGroundProperty(point5);
            GroundProperty result6 = instance.getGroundProperty(point6);
            GroundProperty result7 = instance.getGroundProperty(point7);

            assertEquals(expResult1, result1);
            assertEquals(expResult2, result2);
            assertEquals(expResult3, result3);
            assertEquals(expResult4, result4);
            assertEquals(expResult5, result5);
            assertEquals(expResult6, result6);
            assertEquals(expResult7, result7);
        }
        catch (Exception e) 
        {
            fail("Could not load image file.");
        }
    }
}
