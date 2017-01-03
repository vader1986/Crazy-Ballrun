/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The GraphGenerator generates a graph with neighborhood relations between
 * QuadTree-leaf-nodes.  
 * 
 * @author Timm Hoffmeister
 */
public class GraphGenerator {
        
    /**
     * Represents a QuadTree-leaf-node with neighborhood-information.
     */
   public class Node extends PathFinder.Node {

       /**
        * Reference to the corresponding QuadTree-leaf.
        */
       public QuadTree.Node leaf;
       
        /**
         * List of all neighboring leaf-nodes of the QuadTree.
         */
        public ArrayList<PathFinder.Node> neighbors = new ArrayList<PathFinder.Node>();

        /**
         * Node position.
         */
        private long x, y;

        /**
         * Node size.
         */
        private long width, height;
        
        /**
         * Node Content.
         */
        public Comparable content;
        
        /**
         * Creates an instance of Node. 
         * @param leaf reference to the QuadTree-leaf
         */
        public Node(QuadTree.Node leaf) {
            this.x = leaf.getX();
            this.y = leaf.getY();
            this.width = leaf.getWidth();
            this.height = leaf.getHeight();
            this.content = leaf.getContent();
            this.leaf = leaf;
        }
        
        /**
         * Gets the x-coordinate of the node's position.
         * @return the x-coordinate of the node's position
         */
        public long getX () {
            return x;
        }

        /**
         * Gets the y-coordinate of the node's position.
         * @return the y-coordinate of the node's position
         */
        public long getY () {
            return y;
        }
        
        /**
         * Gets the node's width.
         * @return width of the node
         */
        public long getWidth () {
            return width;
        }
        
        /**
         * Gets the node's height.
         * @return height of the node
         */
        public long getHeight () {
            return height;
        }

        /**
         * Calculates the distance between the center-points of two nodes. 
         * @param node reference to the other node
         * @return distance between both nodes
         */
        private double distance (Node node) {
            long x1, y1, x2, y2;
            x1 = node.getX() + node.getWidth() / 2;
            y1 = node.getY() + node.getHeight() / 2;
            x2 = this.getX() + this.getWidth() / 2;
            y2 = this.getY() + this.getHeight() / 2;
            return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        }
        
        @Override
        public ArrayList<PathFinder.Node> getNeighbors() {
            return neighbors;
        }

        @Override
        public long getCosts(PathFinder.Node pre) {
            return (long)distance((Node)pre);
        }

        @Override
        public long getHeuristic(PathFinder.Node goal) {
            return (long)distance((Node)goal);
        }
    }
    
    /**
     * Reference to the QuadTree. 
     */
    private QuadTree mQuadTree = null;
    
    /**
     * List of all leaf-nodes of the QuadTree.
     */
    private LinkedList<PathFinder.Node> mLeafList = new LinkedList<PathFinder.Node>();
    
    /**
     * Mapping from QuadTree-Node to PathFinder-Node. 
     */
    private HashMap<QuadTree.Node, Node> mLeafMap = new HashMap<QuadTree.Node, Node>();
    
    /**
     * Creates an instance of GraphGenerator.
     * @param quadtree reference to the quadtree
     */
    public GraphGenerator (QuadTree quadtree) {
        mQuadTree = quadtree;
        createLeafList();
        createNeighborhood();
    }

    /**
     * Creates the leaf-list for the QuadTree. 
     */
    private void createLeafList () {
        mLeafList.clear();

        long minSize = mQuadTree.getMinNodeSize();
        LinkedList<QuadTree.Node> vList = new LinkedList<QuadTree.Node>();
        
        // Go through the domain by stepsize "minimum node size"
        for (long x = minSize / 2; x < mQuadTree.getDomainWidth(); x = x + minSize)
        {
            for (long y = minSize / 2; y < mQuadTree.getDomainHeight(); y = y + minSize)
            {
                // Find the node at particular position in the domain
                QuadTree.Node vNode = mQuadTree.getLeaf(x, y);
                if (vNode != null && vNode.getContent() != null) 
                {
                    // Check if the leaf is already in the list (larger leafs)
                    if (!vList.contains(vNode)) 
                    {
                        vList.add(vNode);
                    }
                }
            }         
        }
        
        // Create new nodes supporting neighborhood information
        for (QuadTree.Node vNode : vList) 
        {
            Node vNNode = new Node(vNode);
            mLeafList.add(vNNode);
            mLeafMap.put(vNode, vNNode);
        }
    }

    /**
     * Creates the neighborhood-relation between the leaf-nodes of the QuadTree.
     * Runtime of this method is O(N²).
     */
    private void createNeighborhood () {
        
        // find neighbors for all leafs
        for (PathFinder.Node vPNode : mLeafList) 
        {
            // initialize with no neighbors
            Node vNode = (Node) vPNode;
            vNode.neighbors.clear();
            
            // go through all candidates
            for (PathFinder.Node vNeighbor : mLeafList) 
            {
                if (vNeighbor != vNode && isNeighbor((Node)vNeighbor, vNode)) 
                {
                    vNode.neighbors.add(vNeighbor);
                }
            }
        }
    }
    
    /**
     * Finds out if 'neighbor' is a neighbor of 'node'.
     * @param neighbor reference to the possible neighbor node
     * @param node reference to the node
     * @return 'true' if neighbor is neighbor of node
     */
    private boolean isNeighbor (Node neighbor, Node node) {
        
        if (neighbor.content.compareTo(node.content) != 0)
            return false;
        
        // top neighbor
        if (neighbor.getY() + neighbor.getHeight() == node.getY()) 
        {
            // neighbor-size is smaller/equal to node-size
            if (neighbor.getX() >= node.getX() && neighbor.getX() < node.getX() + node.getWidth()) 
            {
                return true;
            }
            // neighbor-size is larger than node-size
            if (node.getX() >= neighbor.getX() && node.getX() < neighbor.getX() + neighbor.getWidth()) 
            {
                return true;
            }
        }

        // bottom neighbor
        if (neighbor.getY() == node.getY() + node.getHeight()) 
        {
            // neighbor-size is smaller/equal to node-size
            if (neighbor.getX() >= node.getX() && neighbor.getX() < node.getX() + node.getWidth()) 
            {
                return true;
            }
            // neighbor-size is larger than node-size
            if (node.getX() >= neighbor.getX() && node.getX() < neighbor.getX() + neighbor.getWidth()) 
            {
                return true;
            }
        }

        // left neighbor
        if (neighbor.getX() + neighbor.getWidth() == node.getX()) 
        {
            // neighbor-size is smaller/equal to node-size
            if (neighbor.getY() >= node.getY() && neighbor.getY() < node.getY() + node.getHeight()) 
            {
                return true;
            }
            // neighbor-size is larger than node-size
            if (node.getY() >= neighbor.getY() && node.getY() < neighbor.getY() + neighbor.getHeight()) 
            {
                return true;
            }
        }
        
        // right neighbor
        if (neighbor.getX() == node.getX() + node.getWidth()) 
        {
            // neighbor-size is smaller/equal to node-size
            if (neighbor.getY() >= node.getY() && neighbor.getY() < node.getY() + node.getHeight()) 
            {
                return true;
            }
            // neighbor-size is larger than node-size
            if (node.getY() >= neighbor.getY() && node.getY() < neighbor.getY() + neighbor.getHeight()) 
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets a reference to the list of nodes with neighborhood information. 
     * @return a list of nodes with neighborhood information
     */
    public LinkedList<PathFinder.Node> getNodes () {
        return mLeafList;
    }
    
    /**
     * Creates a new neighborhood relation from node1 to node2 (NOT the other
     * way around).
     * @param node1 reference to the first node
     * @param node2 reference to the node which becomes neighbor of node1
     */
    public void createLink (PathFinder.Node node1, PathFinder.Node node2) {
        node1.getNeighbors().add(node2);
    }
    
    /**
     * Finds the node for a given position.
     * @param x position of the leaf
     * @param y position of the leaf
     * @return reference to the leaf node
     */
    public PathFinder.Node getNode(long x, long y) 
    {
        return mLeafMap.get(mQuadTree.getLeaf(x, y));
    }

    
}
