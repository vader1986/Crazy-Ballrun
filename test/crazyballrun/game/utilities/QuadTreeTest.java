/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.utilities;

import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing class for the QuadTree.
 * 
 * @author Timm Hoffmeister
 */
public class QuadTreeTest {
    
    public QuadTreeTest() {
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
     * Test of insert method, of class QuadTree.
     */
    @Test
    public void testInsert() {
        System.out.println("insert");

        BufferedImage vImage = null, vOutput = null, vResult = null;

        // load image
        try {
            vImage = ImageIO.read(this.getClass().getResource("QuadTreeTest.bmp"));
            vOutput = ImageIO.read(this.getClass().getResource("QuadTreeTestOutput.bmp"));
            vResult = ImageIO.read(this.getClass().getResource("QuadTreeTestResult.bmp"));
        }
        catch (IOException e) {
            fail("Image not found: QuadTreeTest.bmp");
            return;
        }
        
        // Build tree from image
        QuadTree<Integer> vTree = new QuadTree<Integer>(0, 0, vImage.getWidth(), vImage.getHeight(), 4);

        for (int x = 0; x < vImage.getWidth(); x++) {
            for (int y = 0; y < vImage.getHeight(); y++) {
                if (vImage.getRGB(x, y) == Color.WHITE.getRGB())
                    vTree.insert(x, y, 1);
                else if (vImage.getRGB(x, y) == Color.BLACK.getRGB())
                    vTree.insert(x, y, 0);
                else 
                    vTree.insert(x, y, 2);
            }
        }
        
        // Paint tree into output image
        HashMap<Integer, Color> vColorTable = new HashMap<Integer, Color> ();
        vColorTable.put(0, Color.BLUE);
        vColorTable.put(1, Color.RED);
        vColorTable.put(2, Color.YELLOW);
        vTree.paint(vOutput, vColorTable);    
        
        // Compare test-result to real solution
        boolean vEqual = true;
        for (int x = 0; x < vOutput.getWidth(); x++)
            for (int y = 0; y < vOutput.getHeight(); y++)
                if (vOutput.getRGB(x, y) != vResult.getRGB(x, y))
                    vEqual = false;

        assertTrue(vEqual);
    }

    /**
     * Test of getLeaf method, of class QuadTree.
     */
    @Test
    public void testGetLeaf() {
        System.out.println("getLeaf");

        BufferedImage vImage = null, vOutput = null, vResult = null;

        // load image
        try {
            vImage = ImageIO.read(this.getClass().getResource("QuadTreeTest.bmp"));
            vOutput = ImageIO.read(this.getClass().getResource("QuadTreeTestOutput.bmp"));
            vResult = ImageIO.read(this.getClass().getResource("QuadTreeTestResult2.bmp"));
        }
        catch (IOException e) {
            fail("Image not found: QuadTreeTest.bmp");
            return;
        }
        
        // Build tree from image
        QuadTree<Integer> vTree = new QuadTree<Integer>(0, 0, vImage.getWidth(), vImage.getHeight(), 4);

        for (int x = 0; x < vImage.getWidth(); x++) {
            for (int y = 0; y < vImage.getHeight(); y++) {
                if (vImage.getRGB(x, y) == Color.WHITE.getRGB())
                    vTree.insert(x, y, 1);
                else if (vImage.getRGB(x, y) == Color.BLACK.getRGB())
                    vTree.insert(x, y, 0);
                else 
                    vTree.insert(x, y, 2);
            }
        }
        
        // Paint tree into output image
        HashMap<Integer, Color> vColorTable = new HashMap<Integer, Color> ();
        vColorTable.put(0, Color.BLUE);
        vColorTable.put(1, Color.RED);
        vColorTable.put(2, Color.YELLOW);
        vTree.paint(vTree.getLeaf(100, 300), vOutput, vColorTable, Color.GREEN);        
        
        boolean vEqual = true;
        for (int x = 0; x < vOutput.getWidth(); x++)
            for (int y = 0; y < vOutput.getHeight(); y++)
                if (vOutput.getRGB(x, y) != vResult.getRGB(x, y))
                    vEqual = false;
        
        assertTrue(vEqual);        
    }
}
