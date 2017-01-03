/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.utilities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class tests the Vector2D-class.
 * @author Timm Hoffmeister
 */
public class Vector2DTest {
    
    /**
     * Constructor of Vector2DTest.
     */
    public Vector2DTest() {
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
     * Test of add method, of class Vector2D.
     */
    @Test
    public void testAdd_Vector2D() {
        System.out.println("add");
        Vector2D v = new Vector2D(0.2, 0.2);
        Vector2D instance = new Vector2D(0.1, 0.1);
        instance.add(v);
        assertEquals(0.3, instance.x, 0.0001);
        assertEquals(0.3, instance.y, 0.0001);
    }

    /**
     * Test of add method, of class Vector2D.
     */
    @Test
    public void testAdd_double_double() {
        System.out.println("add");
        double x = 0.1;
        double y = 0.1;
        Vector2D instance = new Vector2D(0.2, 0.2);
        instance.add(x, y);
        assertEquals(0.3, instance.x, 0.0001);
        assertEquals(0.3, instance.y, 0.0001);
    }

    /**
     * Test of sub method, of class Vector2D.
     */
    @Test
    public void testSub_Vector2D() {
        System.out.println("sub");
        Vector2D v = new Vector2D(0.1, 0.2);
        Vector2D instance = new Vector2D(0.2, 0.2);
        instance.sub(v);
        assertEquals(0.1, instance.x, 0.0001);
        assertEquals(0.0, instance.y, 0.0001);
    }

    /**
     * Test of sub method, of class Vector2D.
     */
    @Test
    public void testSub_double_double() {
        System.out.println("sub");
        double x = 0.1;
        double y = 0.2;
        Vector2D instance = new Vector2D(0.2, 0.2);
        instance.sub(x, y);
        assertEquals(0.1, instance.x, 0.0001);
        assertEquals(0.0, instance.y, 0.0001);
    }

    /**
     * Test of length method, of class Vector2D.
     */
    @Test
    public void testLength() {
        System.out.println("length");
        Vector2D instance = new Vector2D(1.0, 1.0);
        double expResult = 1.41421356;
        double result = instance.length();
        assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of multiply method, of class Vector2D.
     */
    @Test
    public void testMultiply() {
        System.out.println("multiply");
        double c = 2.0;
        Vector2D instance = new Vector2D(1.0, 2.0);
        instance.multiply(c);
        assertEquals(2.0, instance.x, 0.0001);
        assertEquals(4.0, instance.y, 0.0001);
    }

    /**
     * Test of dot method, of class Vector2D.
     */
    @Test
    public void testDot() {
        System.out.println("dot");
        Vector2D v = new Vector2D(2.0, 1.0);
        Vector2D instance = new Vector2D(2.0, 2.0);
        double expResult = 6.0;
        double result = instance.dot(v);
        assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of cut method, of class Vector2D.
     */
    @Test
    public void testCut() {
        System.out.println("cut");
        double epsilon = 0.001;
        Vector2D instance = new Vector2D(0.01, 0.1);
        instance.cut(epsilon);
        assertEquals(0.0, instance.x, 0.0);
        assertEquals(0.1, instance.y, 0.0);
    }

    /**
     * Test of normalize method, of class Vector2D.
     */
    @Test
    public void testNormalize() {
        System.out.println("normalize");
        Vector2D instance = new Vector2D(1.0, 1.0);
        instance.normalize();
        assertEquals(1.0, instance.length(), 0.0001);
    }
    
    /**
     * Test of rotate method, of class Vector2D.
     */
    @Test
    public void testRotate_double() {
        System.out.println("rotate");

        // left rotation
        double phi = Math.PI / 2;
        Vector2D instance = new Vector2D(0.0, 1.0);
        instance.rotate(phi);
        assertEquals(-1.0, instance.x, 0.0001);
        assertEquals(0.0, instance.y, 0.0001);

        // right rotation
        phi = -Math.PI / 2;
        instance = new Vector2D(1.0, 0.0);
        instance.rotate(phi);
        assertEquals(0.0, instance.x, 0.0001);
        assertEquals(-1.0, instance.y, 0.0001);
    }

    /**
     * Test of rotate method, of class Vector2D.
     */
    @Test
    public void testRotate_double_Vector2D() {
        System.out.println("rotate");
        double phi = Math.PI / 2;
        Vector2D point = new Vector2D(1.0, 1.0);
        Vector2D instance = new Vector2D(1.0, 2.0);
        instance.rotate(phi, point);
        assertEquals(0.0, instance.x, 0.0001);
        assertEquals(1.0, instance.y, 0.0001);
    }

    /**
     * Test of rotate method, of class Vector2D.
     */
    @Test
    public void testRotate_3args() {
        System.out.println("rotate");
        double phi = Math.PI / 2;
        double x = 1.0;
        double y = 1.0;
        Vector2D instance = new Vector2D(1.0, 2.0);
        instance.rotate(phi, x, y);
        assertEquals(0.0, instance.x, 0.0001);
        assertEquals(1.0, instance.y, 0.0001);
    }
}
