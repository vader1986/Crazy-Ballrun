/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.utilities;

import java.io.IOException;
import java.util.HashMap;
import java.awt.Color;
import javax.imageio.ImageIO;
import crazyballrun.game.utilities.PathFinder.Node;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing class for the PathFinder.
 * 
 * @author Timm Hoffmeister
 */
public class PathFinderTest {
    
    public PathFinderTest() {
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
     * Test of getPath method, of class PathFinder.
     */
    @Test
    public void testGetPath() {
        System.out.println("getPath");

        /**
         * Create QuadTree:
         * 
         *         (1)             (2)
         *      O O X X X       O O X X X
         *      X O X X X       X O X O O
         *      O O X O X       O O O O O
         *      X X O X O       X X O O O
         *      X X X O O       O O O O O
         */
        QuadTree<Integer> vQuadTree1 = new QuadTree<Integer>(0, 0, 16, 16, 2);
        vQuadTree1.insert(0, 0, 0);
        vQuadTree1.insert(2, 0, 0);
        vQuadTree1.insert(4, 0, 1);
        vQuadTree1.insert(6, 0, 1);
        vQuadTree1.insert(8, 0, 1);
        vQuadTree1.insert(0, 2, 1);
        vQuadTree1.insert(2, 2, 0);
        vQuadTree1.insert(4, 2, 1);
        vQuadTree1.insert(6, 2, 1);
        vQuadTree1.insert(8, 2, 1);
        vQuadTree1.insert(0, 4, 0);
        vQuadTree1.insert(2, 4, 0);
        vQuadTree1.insert(4, 4, 1);
        vQuadTree1.insert(6, 4, 0);
        vQuadTree1.insert(8, 4, 1);
        vQuadTree1.insert(0, 6, 1);
        vQuadTree1.insert(2, 6, 1);
        vQuadTree1.insert(4, 6, 0);
        vQuadTree1.insert(6, 6, 1);
        vQuadTree1.insert(8, 6, 0);
        vQuadTree1.insert(0, 8, 1);
        vQuadTree1.insert(2, 8, 1);
        vQuadTree1.insert(4, 8, 1);
        vQuadTree1.insert(6, 8, 0);
        vQuadTree1.insert(8, 8, 0);

        QuadTree<Integer> vQuadTree2 = new QuadTree<Integer>(0, 0, 16, 16, 2);
        vQuadTree2.insert(0, 0, 0);
        vQuadTree2.insert(2, 0, 0);
        vQuadTree2.insert(4, 0, 1);
        vQuadTree2.insert(6, 0, 1);
        vQuadTree2.insert(8, 0, 1);
        vQuadTree2.insert(0, 2, 1);
        vQuadTree2.insert(2, 2, 0);
        vQuadTree2.insert(4, 2, 1);
        vQuadTree2.insert(6, 2, 0);
        vQuadTree2.insert(8, 2, 0);
        vQuadTree2.insert(0, 4, 0);
        vQuadTree2.insert(2, 4, 0);
        vQuadTree2.insert(4, 4, 0);
        vQuadTree2.insert(6, 4, 0);
        vQuadTree2.insert(8, 4, 0);
        vQuadTree2.insert(0, 6, 1);
        vQuadTree2.insert(2, 6, 1);
        vQuadTree2.insert(4, 6, 0);
        vQuadTree2.insert(6, 6, 0);
        vQuadTree2.insert(8, 6, 0);
        vQuadTree2.insert(0, 8, 0);
        vQuadTree2.insert(2, 8, 0);
        vQuadTree2.insert(4, 8, 0);
        vQuadTree2.insert(6, 8, 0);
        vQuadTree2.insert(8, 8, 0);
                
        // Create GraphGenerators
        GraphGenerator vGG1 = new GraphGenerator(vQuadTree1);
        Node vStart1 = vGG1.getNode(0, 0);
        Node vGoal1 = vGG1.getNode(9, 9);
        GraphGenerator vGG2 = new GraphGenerator(vQuadTree2);
        Node vStart2 = vGG2.getNode(0, 0);
        Node vGoal2 = vGG2.getNode(8, 8);
        
        // Create PathFinders
        PathFinder vPF1 = new PathFinder(vGG1.getNodes());
        PathFinder vPF2 = new PathFinder(vGG2.getNodes());
        assertTrue(vPF1.getPath(vStart1, vGoal1) == null);
        LinkedList<Node> vPath = vPF2.getPath(vStart2, vGoal2);
        assertTrue(vPath.size() == 6);
        vPath = vPF2.getPath(vStart2, vGoal2);
        assertTrue(vPath.size() == 6);
        
        try 
        {
            BufferedImage vResult = ImageIO.read(this.getClass().getResource("PathFinderTestResult.bmp"));
            BufferedImage vImage = ImageIO.read(this.getClass().getResource("PathFinderTest.bmp"));
            BufferedImage vOutput = ImageIO.read(this.getClass().getResource("PathFinderTestOutput.bmp"));
            QuadTree<Integer> vQuadTree = new QuadTree<Integer>(0, 0, vImage.getWidth(), vImage.getHeight(), 2);
            for (int x = 0; x < vImage.getWidth(); x++) {
                for (int y = 0; y < vImage.getHeight(); y++) {
                    if (vImage.getRGB(x, y) == Color.WHITE.getRGB())
                        vQuadTree.insert(x, y, 1);
                    else
                        vQuadTree.insert(x, y, 0);
                }
            }            

            HashMap<Integer, Color> vColorTable = new HashMap<Integer, Color> ();
            vColorTable.put(0, Color.BLUE);
            vColorTable.put(1, Color.RED);

            vQuadTree.paint(vOutput, vColorTable);

            GraphGenerator vGraph = new GraphGenerator(vQuadTree);
            PathFinder vPathFinder = new PathFinder(vGraph.getNodes());
            PathFinder.Node vStart = vGraph.getNode(1, 1);
            PathFinder.Node vGoal = vGraph.getNode(399, 299);
            LinkedList<PathFinder.Node> vMyPath = vPathFinder.getPath(vStart, vGoal);

            for (PathFinder.Node vNode : vMyPath)
                vQuadTree.paint(((GraphGenerator.Node)vNode).leaf, vOutput, vColorTable, Color.RED);
            
            boolean vEqual = true;
            for (int x = 0; x < vOutput.getWidth(); x++)
                for (int y = 0; y < vOutput.getHeight(); y++)
                    if (vOutput.getRGB(x, y) != vResult.getRGB(x, y))
                        vEqual = false;     

            assertTrue(vEqual);
        }
        catch (IOException e) {
            fail ("Image not found.");
        }        
    }
}
