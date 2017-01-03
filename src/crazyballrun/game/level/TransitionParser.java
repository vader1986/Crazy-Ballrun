/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level;

import crazyballrun.game.GameConstants;
import crazyballrun.game.utilities.GraphGenerator;
import crazyballrun.game.utilities.PathFinder;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The TransitionParser read an xml-file specifying transitions between level-
 * layers. 
 * 
 * @author Timm Hoffmeister
 */
public class TransitionParser extends DefaultHandler {
 
    /**
     * One GraphGenerator containing the QuadTree-Leafnodes for each layer.
     */
    private ArrayList<GraphGenerator> mGraphs = null;
    
    /**
     * Reference to the level (to set Tile-transitions).
     */
    private Level mLevel = null;
    
    /**
     * Parses a level file.
     * @param file name of the xml file
     * @param graphGenerators list of GraphGenerators containing the nodes for 
     * each level-layer
     * @param level reference to the level
     * @throws ParserConfigurationException error during xml-parsing
     * @throws SAXException xml-file is not xml-conform
     * @throws IOException file not found
     */
    public void parse(String file, ArrayList<GraphGenerator> graphGenerators, Level level) throws ParserConfigurationException, SAXException, IOException 
    {
        mLevel = level;
        mGraphs = graphGenerators;
        
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        sp.parse(file, this);
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
    {
        if (qName.equals("Transition"))
        {
            // read transition attributes
            int layer = Integer.parseInt(attributes.getValue("layer"));
            long x = Long.parseLong(attributes.getValue("x"));
            long y = Long.parseLong(attributes.getValue("y"));
            String direction = attributes.getValue("direction");
            
            // get transition nodes
            GraphGenerator vGraph = mGraphs.get(layer);
            PathFinder.Node vNode1 = vGraph.getNode(x, y);
            PathFinder.Node vNode2 = null;
            if (direction.equals("up"))
            {
                vNode2 = mGraphs.get(layer+1).getNode(x, y);
            }
            else
            {
                vNode2 = mGraphs.get(layer-1).getNode(x, y);
            }
            
            // create link from node1 to node2
            vGraph.createLink(vNode1, vNode2);
            
            // create transition in level-tile
            Tile vTile = mLevel.getTile((int)(x / GameConstants.TEXTURE_SIZE), (int)(y / GameConstants.TEXTURE_SIZE), layer);
            if (vTile != null)
            {
                if (direction.equals("up"))
                {
                    vTile.setNextLayer(true);
                }                
                else
                {
                    vTile.setPreviousLayer(true);
                }
            }
        }
    }
    
}
