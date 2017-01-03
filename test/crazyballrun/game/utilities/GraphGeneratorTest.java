/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.utilities;

import crazyballrun.game.utilities.PathFinder.Node;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing class for GraphGenerator.
 * 
 * @author Timm Hoffmeister
 */
public class GraphGeneratorTest {
    
    public GraphGeneratorTest() {
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
     * Test of getNodes method, of class GraphGenerator.
     */
    @Test
    public void testGetNodes() {
        System.out.println("getNodes");

        // Create QuadTree with 4 leafs
        QuadTree<Integer> vQuadTree = new QuadTree<Integer>(0, 0, 10, 10, 2);
        vQuadTree.insert(0, 0, 0);
        vQuadTree.insert(2, 2, 1);
        vQuadTree.insert(2, 0, 1);
        vQuadTree.insert(0, 2, 1);

        // Create a GraphGenerator
        GraphGenerator vGraphGenerator = new GraphGenerator(vQuadTree);

        // Test getNodes-method
        LinkedList<Node> vList = vGraphGenerator.getNodes();
        assertTrue(vList.size() == 4);
    }

    /**
     * Test of getNode method, of class GraphGenerator.
     */
    @Test
    public void testGetNode() {
        System.out.println("getNode");

        // Create QuadTree with 4 leafs
        QuadTree<Integer> vQuadTree = new QuadTree<Integer>(0, 0, 10, 10, 2);
        vQuadTree.insert(0, 0, 0);
        vQuadTree.insert(2, 2, 1);
        vQuadTree.insert(2, 0, 1);
        vQuadTree.insert(0, 2, 1);

        // Create a GraphGenerator
        GraphGenerator vGraphGenerator = new GraphGenerator(vQuadTree);

        // Test getNode-method
        Node vNode = vGraphGenerator.getNode(2, 2);
        assertTrue(((GraphGenerator.Node)vNode).content.compareTo(1) == 0);
        assertTrue(vNode.getNeighbors().size() == 2);
        Node vNode2 = vGraphGenerator.getNode(0, 0);
        assertTrue(vNode2.getNeighbors().isEmpty());
    }
}
