/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * The PathFinder calculates the shortest path between two nodes in a graph. 
 * 
 * @author Timm Hoffmeister
 */
public class PathFinder {
    
    /**
     * Representation of one node of the graph.
     */
    public static abstract class Node {

        /**
         * Specifies the neighbors of the node. 
         * @return a list of neighboring nodes
         */
        public abstract ArrayList<Node> getNeighbors ();

        /**
         * Calculates the costs from one neighoring node to this node. 
         * @param pre reference to the neighboring node
         * @return costs to get from one node to another
         */
        public abstract long getCosts(Node pre);

        /**
         * Calculates a heuristical value to estimate the costs from this node 
         * to the goal node. 
         * @param goal reference to the goal node
         * @return estimated costs to reach the goal from this node
         */
        public abstract long getHeuristic(Node goal);
    }

    /**
     * Representation of node information used by the pathfinding algorithm.
     */
    private class NodeProperties implements Comparable<NodeProperties> {
        public long g;
        public long f;
        public NodeProperties predecessor;
        public Node node;

        @Override
        public int compareTo(NodeProperties o) {
            return Long.compare(f, o.f);
        }
        public NodeProperties(Node node) {
            this.node = node;
        }
        public void clear() {g = Long.MAX_VALUE; f = 0;}
    }
    
    /**
     * Maps node-references to Integer-values.
     */
    private HashMap<Node, NodeProperties> mNodeList = new HashMap<Node, NodeProperties>();
    
    /**
     * Priority queue used for A* implementation.
     */
    private PriorityQueue<NodeProperties> mQueue = new PriorityQueue<NodeProperties>();
    
    /**
     * List of all visited nodes (used by A*).
     */
    private LinkedList<NodeProperties> mClosedList = new LinkedList<NodeProperties>();
    
    /**
     * Creates an instance of PathFinder.
     * @param nodeList list of graph nodes
     */
    public PathFinder (LinkedList<Node> nodeList) {
        for (Node node : nodeList) 
            mNodeList.put(node, new NodeProperties(node));
    }
    
    /**
     * Finds the (shortest) path from start- to goal-node.  
     * @param startNode reference to the start-node
     * @param goalNode reference to the goal-node
     * @return shortest path from start- to goal-node or null if no path found
     */
    public LinkedList<Node> getPath (Node startNode, Node goalNode) {
        
        // initialization
        NodeProperties goal = mNodeList.get(goalNode);
        NodeProperties start = mNodeList.get(startNode);
        mQueue.clear();
        mClosedList.clear();
        mQueue.add(start);
        
        // start search
        while (!mQueue.isEmpty()) 
        {
            // visit next node of the queue
            NodeProperties curNode = mQueue.poll();
            
            // goal found
            if (curNode == goal) 
            {
                LinkedList<Node> path = new LinkedList<Node>();

                // recontruct path
                while (curNode != start) {
                    path.addFirst(curNode.node);
                    curNode = curNode.predecessor;
                }
                return path;
            }
            
            // expand node
            for (Node neighbor : curNode.node.getNeighbors())
            {
                NodeProperties successor = mNodeList.get(neighbor);
                        
                // already visited
                if (mClosedList.contains(successor))
                    continue;
                
                // costs from start to successor
                long g = curNode.g + curNode.node.getCosts(successor.node); 
                
                // skip successor is already in the queue and doesn't need update
                if (mQueue.contains(successor) && g >= successor.g)
                    continue;
                
                // update successor node
                successor.predecessor = curNode;
                successor.g = g;
                
                // estimate the path costs from start to goal through successor
                long f = g + successor.node.getHeuristic(goal.node);
                successor.f = f;
                
                // update priority in queue
                if (mQueue.contains(successor)) 
                    mQueue.remove(successor);
                mQueue.add(successor);
            }
            
            // add current node to the list of visited nodes
            mClosedList.add(curNode);
        }
        
        return null;
    }
    
}
