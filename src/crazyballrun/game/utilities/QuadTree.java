/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.utilities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The QuadTree class is a descrete representation of a continous domain. It 
 * adaptively increase accuracy where changes of data in the domain are high. 
 * 
 * @param <Value> content type of the quadtree
 * @author Timm Hoffmeister
 */
public class QuadTree <Value extends Comparable & Serializable> {
    
    /**
     * String-representation for "new line" (file-input).
     */
    private static String newline = System.getProperty("line.separator");
    
    /**
     * QuadTreeParser is used to parse a QuadTree-xml file.
     */
    private class QuadTreeParser extends DefaultHandler
    {
        /**
         * Currently processed node. 
         */
        private Node mCurrentNode = null;
        
        /**
         * Stack of parent nodes. 
         */
        private LinkedList<Node> mNodeStack = new LinkedList<Node>();
        
        /**
         * Starts the xml-parsing process for the quadtree.
         * @param file filename
         */
        public void parse(String file) throws ParserConfigurationException, SAXException, IOException  
        {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(file, this);
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
        {
            if (qName.equals("QuadTree"))
            {
                mMinNodeSize = Long.parseLong(attributes.getValue("minNodeSize"));
                mWidth = Long.parseLong(attributes.getValue("width"));
                mHeight = Long.parseLong(attributes.getValue("height"));
            }
            else if (qName.equals("Node"))
            {
                // Read Node Content
                String vStrContent = attributes.getValue("content");
                String [] vSplitContent = vStrContent.split(";");
                byte [] vByteContent = new byte[vSplitContent.length];
                for (int i = 0; i < vSplitContent.length; i++)
                    vByteContent[i] = Byte.parseByte(vSplitContent[i]);
                
                ByteArrayInputStream vContentStream = new ByteArrayInputStream(vByteContent);
                Value vContent;
                try
                {
                    ObjectInputStream vInStrContent = new ObjectInputStream(vContentStream);
                    vContent = (Value)vInStrContent.readObject();
                
                    // Read Node Position/Size
                    long x = Long.parseLong(attributes.getValue("x"));
                    long y = Long.parseLong(attributes.getValue("y"));
                    long width = Long.parseLong(attributes.getValue("width"));
                    long height = Long.parseLong(attributes.getValue("height"));

                    // Create Node
                    Node vNode = new Node(x,y,width,height, vContent);
                    if (mCurrentNode != null)
                    {
                        mCurrentNode.children.add(vNode);
                        mNodeStack.push(mCurrentNode);
                        mCurrentNode = vNode;
                    }
                    else 
                    {
                        mRootNode = vNode;
                        mCurrentNode = vNode;
                    }
                }
                catch (Exception e)
                {
                    System.err.println(e.getMessage());
                }
                    
            }
        }
        
        @Override
        public void endElement (String uri, String localName, String qName) throws SAXException 
        {
            if (qName.equals("Node"))
            {
                if (!mNodeStack.isEmpty())
                {
                    mCurrentNode = mNodeStack.pop();
                }
            }
        }
        
    }
    
    /**
     * The Node class represents nodes in the QuadTree. 
     */
    public class Node 
    {
        /**
         * Position of the Node.
         */
        private long x, y;
        
        /**
         * Size of the node's area. 
         */
        private long width, height;
        
        /**
         * Node's children. 
         */
        private LinkedList<Node> children = new LinkedList<Node>();
        
        /**
         * Content of the node. 
         */
        private Value content;
        
        /**
         * Creates an instance of Node.
         */
        public Node () {
            
        }
        
        /**
         * Creates an instance of Node. 
         * @param x x-coordinate of the node's position
         * @param y y-coordinate of the node's position
         * @param width width of the node's area
         * @param height height of the node's area
         * @param content node's content
         */
        public Node (long x, long y, long width, long height, Value content) {
            this.x = x; this.y = y; this.width = width; this.height = height;
            this.content = content;
        }
        
        /**
         * Creates children for the node.
         * @return 'true' if children could be created. This might not be the case
         * if the minimum node size has been reached.
         */
        private boolean createChildren () {
            if (width / 2 < mMinNodeSize || height / 2 < mMinNodeSize)
                return false;

            children.add(new Node(x,             y,              width / 2,             height / 2,              content));
            children.add(new Node(x,             y + height / 2, width / 2,             height / 2 + height % 2, content));
            children.add(new Node(x + width / 2, y,              width / 2 + width % 2, height / 2,              content));
            children.add(new Node(x + width / 2, y + height / 2, width / 2 + width % 2, height / 2 + height % 2, content));
            return true;
        }
        
        /**
         * Checks if the node contains a particular point of the domain.
         * @param x collision point
         * @param y collision point
         * @return 'true' if the node contains the specified point
         */
        private boolean contains (long x, long y) {
            return (x >= this.x && y >= this.y && x < this.x + width && y < this.y + height);
        }

        /**
         * Finds the position of the quadtree node.
         * @return the x-coordinate of the position of the node
         */
        public long getX () {return x;}

        /**
         * Finds the position of the quadtree node.
         * @return the y-coordinate of the position of the node 
         */
        public long getY () {return y;}
        
        /**
         * Gets the width the area the node represents.  
         * @return the node's width
         */
        public long getWidth () {return width;}
        
        /**
         * Gets the height the area the node represents.  
         * @return the node's height
         */
        public long getHeight () {return height;}
        
        /**
         * Gets the node's content.
         * @return a reference to the node's content
         */
        public Value getContent () {return content;}
    }
    
    /**
     * Root node of the quadtree.
     */
    private Node mRootNode = null;

    /**
     * Minimum size of one node.
     */
    private long mMinNodeSize = 0;

    /**
     * Domain size.
     */
    private long mWidth, mHeight, mDomainSize;
    
    /**
     * Creates a QuadTree-instance from a xml-file. 
     * @param file filename
     * @throws ParserConfigurationException file violates xml-standard
     * @throws SAXException file violates xml-standard
     * @throws IOException  file not found
     */
    public QuadTree(String file) throws ParserConfigurationException, SAXException, IOException
    {
        QuadTreeParser parser = new QuadTreeParser();
        parser.parse(file);
    }
    
    /**
     * Creates an instance of QuadTree. 
     * @param x1 top left point of the domain
     * @param y1 top left point of the domain
     * @param x2 bottom right point of the domain
     * @param y2 bottom right point of the domain
     * @param minSize minimum node size
     */
    public QuadTree(long x1, long y1, long x2, long y2, long minSize) 
    {
        mMinNodeSize = minSize;
        mWidth = x2 - x1;
        mHeight = y2 - y1;
        mDomainSize = Math.max(mWidth, mHeight);
        mRootNode = new Node(x1, y1, mDomainSize, mDomainSize, null);
    }
    
    /**
     * Inserts a new element into the quadtree. If the node already exists, its
     * content will be replaced. 
     * @param x position of the element
     * @param y position of the element
     * @param content reference to the element's content
     * @return 'true' if the insertion has been successfull
     */
    public boolean insert (long x, long y, Value content) {
        return insert(mRootNode, x, y, content);
    }
    
    /**
     * Inserts a new element into the quadtree.
     * @param node where to insert the new element
     * @param x position of the element
     * @param y position of the element
     * @param content reference to the element's content
     * @return 'true' if the insertion has been successfull
     */
    private boolean insert (Node node, long x, long y, Value content) {

        int vSameContent = 0;
        boolean vReturn = false;
        
        // Traverse the tree for the correct node
        for (Node vNode : node.children) {
            if (vNode.contains(x, y)) 
                vReturn = insert(vNode, x, y, content);
            if (vNode.content == content) vSameContent++;
        }
        
        // Combine 4 leafs with the same content
        if (vSameContent == 4) {
            node.content = content;
            node.children.clear();
            return vReturn;
        }

        // Find the leaf
        if (node.children.isEmpty()) {
            
            // Create children for mode accuracy
            if ((node.content == null || content.compareTo(node.content) > 0) && node.createChildren()) {
                node.content = null;
                for (Node vNode : node.children)
                    if (vNode.contains(x, y))
                        insert(vNode, x, y, content);
            } 
            // Deepest level -> leaf (stores content)
            else {
                if (node.contains(x, y) && (
                        node.content == null || 
                        content.compareTo(node.content) > 0)) {
                    node.content = content;
                }
            }
        }
        
        return true;
    }

    /**
     * Renders the quadtree into the image.
     * @param out reference to the output image
     * @param colorTable reference to the color map (which content should be 
     * painted with which color)
     */
    public void paint (BufferedImage out, HashMap<Value, Color> colorTable) {
        paint(mRootNode, out, colorTable, Color.GREEN);
    }    
    
    /**
     * Recursively paints nodes and children into the output image. 
     * @param node node from where to start recursion
     * @param out reference to the output image
     * @param colorTable reference to the color map (which content should be 
     * @param borderColor color of the border
     * painted with which color)
     */
    public void paint (Node node, BufferedImage out, HashMap<Value, Color> colorTable, Color borderColor) {

        // paint children
        for (Node vChild : node.children)
            paint(vChild, out, colorTable, borderColor);

        // return if node is not leaf or has no content
        if (!node.children.isEmpty() || node.content == null) 
            return;
        
        // image coordinates
        int imgX = (int)Math.round(((double)node.x / (double)mWidth) * out.getWidth());
        int imgY = (int)Math.round(((double)node.y / (double)mHeight) * out.getHeight());
        int imgWidth = (int)Math.round(( (double)node.width / (double)mWidth) * out.getWidth());
        int imgHeight = (int)Math.round(( (double)node.height / (double)mHeight) * out.getHeight());

        if (out != null) {
            
            // top and bottom node border
            for (int i = imgX; i < imgX + imgWidth && i < out.getWidth(); i++) {
                out.setRGB(i, Math.min(imgY,               out.getHeight()-1), borderColor.getRGB());
                out.setRGB(i, Math.min(imgY + imgHeight-1, out.getHeight()-1), borderColor.getRGB());
            }
            
            // left and right node border
            for (int i = imgY; i < imgY + imgHeight && i < out.getHeight(); i++) {
                out.setRGB(Math.min(imgX,              out.getWidth()-1), i, borderColor.getRGB());
                out.setRGB(Math.min(imgX + imgWidth-1, out.getWidth()-1), i, borderColor.getRGB());
            }
            
            // fill node with content-color
            for (int x = imgX + 1; x < imgX + imgWidth - 1; x++)
                for (int y = imgY + 1; y < imgY + imgHeight - 1; y++)
                    if (colorTable.get(node.content) != null)
                        out.setRGB(Math.min(x, out.getWidth()-1), Math.min(y, out.getHeight()-1), colorTable.get(node.content).getRGB());
        }

    }

    /**
     * Searches for a leaf at the given collision point.
     * @param x collision point
     * @param y collision point
     * @return the leaf-node or 'null' if none exists at the given point
     */
    public Node getLeaf (long x, long y) {
        return getLeaf(mRootNode, x, y);
    }

    /**
     * Recursively searches for a leaf at the given collision point.
     * @param node reference to the current node
     * @param x collision point
     * @param y collision point
     * @return the leaf-node or 'null' if none exists at the given point
     */
    private Node getLeaf (Node node, long x, long y) {
        if (node.content != null) return node;
        for (Node vNode : node.children) {
            if (vNode.contains(x, y))
                return getLeaf(vNode, x, y);
        }
        return null;
    }
    
    /**
     * Gets the minimum size of a leaf node. 
     * @return the minimum size of a leaf node
     */
    public long getMinNodeSize () {
        return mMinNodeSize;
    }
    
    /**
     * Gets the width of the domain covered by the QuadTree.
     * @return the domain width
     */
    public long getDomainWidth () {
        return mWidth;
    }
    
    /**
     * Gets the height of the domain covered by the QuadTree.
     * @return the domain height
     */
    public long getDomainHeight () {
        return mHeight;
    }
    
    /**
     * Stores the quadtree into a file. 
     * @param filename name of the file
     * @throws IOException file not found 
     */
    public void save (String filename) throws IOException
    {
        FileWriter vOutput = new FileWriter(filename);
        vOutput.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + newline);
        vOutput.write("<QuadTree minNodeSize=\"" + mMinNodeSize + "\" " + 
                                 "width=\"" + mWidth + "\" " + 
                                 "height=\"" + mHeight + "\">" + newline);
        save(mRootNode, vOutput, filename, "    ");
        vOutput.write("</QuadTree>");
        vOutput.flush();
        vOutput.close();        
    }
    
    /**
     * Writes the given node into a file and traverses its children.
     * @param node reference to the node
     * @param file FileWriter-instance
     * @param filename name of the quadtree-file
     * @param tabs whitespaces for tree-depth
     */
    private void save (Node node, FileWriter file, String filename, String tabs) throws IOException
    {
        // store node-content in file
        ByteArrayOutputStream vOut = new ByteArrayOutputStream(); 
        ObjectOutputStream vStream = new ObjectOutputStream(vOut);
        vStream.writeObject(node.content);
        vStream.close();
        byte [] vContent = vOut.toByteArray();
        String vContentString = "" + vContent[0];
        for (int i = 1; i < vContent.length; i++)
            vContentString += ";" + vContent[i];
        
        // store node-information in xml
        file.write(tabs + "<Node x=\"" + node.x + "\" " + 
                                "y=\"" + node.y + "\" " + 
                                "height=\"" + node.height + "\" " + 
                                "width=\"" + node.width + "\" " +
                                "content=\"" + vContentString + "\">" + newline);

        // add neighbors
        for (Node vNode : node.children)
            save (vNode, file, filename, tabs + "    ");
        
        // finish node-infos
        file.write(tabs + "</Node>" + newline);
    }
    
    
    
}
