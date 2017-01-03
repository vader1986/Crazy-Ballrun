/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level;

import crazyballrun.game.level.objects.LevelObject;
import crazyballrun.game.level.controllers.ObjectController;
import crazyballrun.game.level.controllers.ObjectControllerFactory;
import crazyballrun.game.GameConstants;
import crazyballrun.game.GameLogger;
import crazyballrun.game.graphics.Animation;
import crazyballrun.game.graphics.GraphicsEngine;
import crazyballrun.game.physics.CollisionTexture;
import crazyballrun.game.utilities.GraphGenerator;
import crazyballrun.game.utilities.PathFinder;
import crazyballrun.game.utilities.Position3D;
import crazyballrun.game.utilities.QuadTree;
import crazyballrun.game.utilities.Vector2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The LevelParser parses an XML-file which specifies level-specific data (map
 * texturing, objects on the map, player positions, goals, etc.). For loading
 * objects, sounds and textures, the LevelParser first tries to find them in the 
 * Level/Music/Graphics-Engine. If they do not exist there it will search in the 
 * local level path. 
 * 
 * @author Timm Hoffmeister
 */
public class LevelParser extends DefaultHandler 
{
    /**
     * String-representation for "new line" (file-input).
     */
    private static String newline = System.getProperty("line.separator");
    
    /**
     * XML-Example for a level:
     * 
     * <!-- The 'name' specifies the level-folder in 'resources/levels/' -->
     * <Level name="NewMap" author="Timm Hoffmeister" loadscreen="load.png">
     * 
     *  <Description>This text is shown in game when selecting this level.</Description>
     * 
     *  <!-- 'minimap' specifies the root-filename of the small level display.
     *       depending on the number of layers, the minimap-filenames will be
     *       "[minimap].[layer].bmp". -->
     *  <Map width="5" height="5" layers="2" minimap="minimap">
     * 
     *      <!-- The Texture-Image-Files names' extension are ".bmp". For the
     *           Collision-Map the GraphicsEngine automatically loads
     *           "[texturename].col.bmp" -->
     *      <Textures>
     *          <Texture name="texture_1" number="5" change="500" music="_ghost_-_Ice_and_Chilli.mp3" x="0" y="0" layer="0" />
     *          <Texture name="texture_2" music="7OOP3D_-_Feeling_Dark_(Behind_The_Mask).mp3 x="3" y="4" layer="1" />
     *      </Textures>
     *     
     *      <Players>
     *          <Player vehicle="Vehicle" x="1.11" y="3.12" layer="0"/>
     *          <Player vehicle="Vehicle" x="2.11" y="3.12" layer="0"/>
     *          <Player vehicle="Vehicle" x="3.11" y="3.12" layer="0"/>
     *          <Player vehicle="Vehicle" x="4.11" y="3.12" layer="0"/>
     *      </Players>
     * 
     *      <Objects>
     *          <Object id="Airplane" x="3.01" y="2.98" layer="0">
     *              ...
     *          </Object>
     *      </Objects>
     *  </Map>
     * 
     * </Level>
     * 
     * --------------------------
     * XML-Example for "Objects": 
     * --------------------------
     * 
     * <!-- id is the identifier of the "LevelObject",
     *      x and y specify the starting position of the
     *      object after loading the level -->
     * <Object id="Airplane" x="3.01" y="2.98" layer="0">
     * 
     *  <!-- Controls the airplane's pathfinding -->
     *  <Controller id="WaypointFollower">
     * 
     *      <!-- Waypoint-Coordinates are specified in "Textures" -->
     *      <Set id="Waypoint" value="5.12,4.21"/>
     *      <Set id="Waypoint" value="7.12,4.21"/>
     *      <Set id="Waypoint" value="7.12,5.21"/>
     *      <Set id="Waypoint" value="5.12,5.21"/>
     * 
     *  </Controller>
     * 
     * </Object>
     * 
     */

    /**
     * States of the XML-Parser (in which tag the parser currently is).
     */
    private enum State {
        BEGIN,
        LEVEL,
        MAP,
        TEXTURES,
        PLAYERS,
        OBJECTS,
        GOALS,
        TEXTURE,
        PLAYER,
        OBJECT,
        CONTROLLER,
        GOAL
    }

    /**
     * Current state in the XML file du
     * ring parsing process.
     */
    private State mState = State.BEGIN;
    
    /**
     * Level-instance to fill with level information.
     */
    private Level mLevel = null;

    /**
     * Current level tile.
     */
    private Tile mLevelTile = null;
    
    /**
     * Current level object.
     */
    private LevelObject mLevelObject = null;
    
    /**
     * Current object controller.
     */
    private ObjectController mController = null;

    /**
     * Collision-Quadtrees for the level (AI pathfinding, one quadtree per layer).
     */
    private ArrayList<GraphGenerator> mGraphGenerators = new ArrayList<GraphGenerator>();

    /**
     * List of all nodes of the whole level (includes all layers).
     */
    private LinkedList<PathFinder.Node> mLevelGraph = new LinkedList<PathFinder.Node>();

    /**
     * List of player-specific goals for the level. 
     */
    private Goal [] mPlayerGoals = null;

    /**
     * Goal which is currently parsed. 
     */
    private Goal mCurrentGoal = null;
    
    /**
     * Constructor of the LevelParser.
     */
    public LevelParser () {
        super();
    }    

    /**
     * Creates quadtrees and graphs for pathfinding.
     */
    private void generateQuadtree () 
    {
        ArrayList<QuadTree<Integer>> vQuadTrees = new ArrayList<QuadTree<Integer>>();
        LinkedList<Position3D> vTransitionPointsUp = new LinkedList<Position3D>();
        LinkedList<Position3D> vTransitionPointsDown = new LinkedList<Position3D>();
        
        // Initialize Quadtree-List
        vQuadTrees.clear();
        long vDomainWidth = mLevel.getWidth() * GameConstants.TEXTURE_SIZE;
        long vDomainHeight = mLevel.getHeight() * GameConstants.TEXTURE_SIZE;

        final long vObjectSize = GameConstants.TEXTURE_SIZE / 5;
        
        for (int layer = 0; layer < mLevel.getLayers(); layer++)
        {
            // Generate Quadtree for current layer
            QuadTree<Integer> vQuadTree = null;

            // Check if QuadTree_[layer].xml exists
            String vQuadTreeFileName = GameConstants.LEVEL_RESOURCE_PATH + File.separator + mLevel.getName() + File.separator + "Quadtree_" + layer + ".xml";
            File vQuadTreeFile = new File(vQuadTreeFileName);
            if (vQuadTreeFile.exists())
            {
                try
                {
                    vQuadTree = new QuadTree<Integer>(vQuadTreeFileName);
                }
                catch (Exception e)
                {
                    vQuadTree = null;
                }
                if (vQuadTree != null)
                {
                    vQuadTrees.add(vQuadTree);
                    continue;
                }
            }

            vQuadTree = new QuadTree<Integer>(0, 0, vDomainWidth, vDomainHeight, vObjectSize);
            vQuadTrees.add(vQuadTree);
            
            // Fill Quadtree with collision data of the ground
            for (int x = 0; x < mLevel.getWidth(); x++)
            {
                for (int y = 0; y < mLevel.getHeight(); y++)
                {
                    Tile vTile = mLevel.getTile(x, y, layer);
                    if (vTile == null) continue;
                    for (int ix = 0; ix < GameConstants.TEXTURE_SIZE; ix++)
                    {
                        for (int iy = 0; iy < GameConstants.TEXTURE_SIZE; iy++)
                        {
                            Vector2D vPosition = new Vector2D(ix, iy);
                            CollisionTexture.GroundProperty vGround = vTile.getGroundProperty(vPosition);
                            
                            // Store tiles which are transitions between two layers
                            if (vGround == CollisionTexture.GroundProperty.NEXT_LAYER)
                            {
                                vTile.setNextLayer(true);
                                vTransitionPointsUp.add(new Position3D( new Vector2D(x * GameConstants.TEXTURE_SIZE + ix, y * GameConstants.TEXTURE_SIZE + iy), layer));
                            }
                            else if (vGround == CollisionTexture.GroundProperty.PREVIOUS_LAYER)
                            {
                                vTile.setPreviousLayer(true);
                                vTransitionPointsDown.add(new Position3D( new Vector2D(x * GameConstants.TEXTURE_SIZE + ix, y * GameConstants.TEXTURE_SIZE + iy), layer));
                            }

                            // Fill quadtree with wall/walkable ground
                            if (vGround == CollisionTexture.GroundProperty.WALL)
                                vQuadTree.insert(x * GameConstants.TEXTURE_SIZE + ix, y * GameConstants.TEXTURE_SIZE + iy, 1);
                            else
                                vQuadTree.insert(x * GameConstants.TEXTURE_SIZE + ix, y * GameConstants.TEXTURE_SIZE + iy, 0);
                        }
                    }
                }
            }

            // Save QuadTree to avoid re-calculation of the tree
            try
            {
                vQuadTree.save(vQuadTreeFileName);
            } 
            catch (Exception e) 
            {
                GameLogger.log(e);
            }
        }
        
        // Generate Graphs
        mGraphGenerators.clear();
        for (int i = 0; i < mLevel.getLayers(); i++) 
            mGraphGenerators.add(new GraphGenerator(vQuadTrees.get(i)));

        String vTransitionFileName = GameConstants.LEVEL_RESOURCE_PATH + File.separator + mLevel.getName() + File.separator + "Transition.xml";
        File vTransitionFile = new File(vTransitionFileName);
        if (vTransitionFile.exists())
        {
            try
            {
                // Load transitions from file
                TransitionParser vTransitionParser = new TransitionParser();
                vTransitionParser.parse(vTransitionFileName, mGraphGenerators, mLevel);
            } 
            catch (Exception e) 
            {
                GameLogger.log(e);
            }                       
        }
        else
        {
            try
            {
                FileWriter vFile = new FileWriter(vTransitionFile);
                vFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + newline);
                vFile.write("<Transitions>" + newline);                
            
                // Create links between layers
                LinkedList<PathFinder.Node> vTransitionNodes = new LinkedList<PathFinder.Node>();
                for (Position3D vTransitionPoint : vTransitionPointsUp)
                {
                    // find corresponding node to position and layer
                    GraphGenerator vGraph = mGraphGenerators.get(vTransitionPoint.layer);
                    PathFinder.Node vNode = vGraph.getNode(vTransitionPoint.position.getX(), 
                                                           vTransitionPoint.position.getY());
                    if (!vTransitionNodes.contains(vNode)) 
                    {
                        vTransitionNodes.add(vNode);
                        vFile.write("<Transition x=\"" + vTransitionPoint.position.getX() + "\" " +
                                                "y=\"" + vTransitionPoint.position.getY() + "\" " +
                                                "layer=\"" + vTransitionPoint.layer + "\" " +
                                                "direction=\"up\"/>" + newline);                

                        // Get node from upper layer
                        GraphGenerator vGraph2 = mGraphGenerators.get(vTransitionPoint.layer+1);
                        PathFinder.Node vNode2 = vGraph2.getNode(vTransitionPoint.position.getX(), 
                                                                 vTransitionPoint.position.getY());
                        
                        // Create link to node from upper layer
                        vGraph.createLink(vNode, vNode2);
                    }
                }
                vTransitionNodes.clear();
                for (Position3D vTransitionPoint : vTransitionPointsDown)
                {
                    // find corresponding node to position and layer
                    GraphGenerator vGraph = mGraphGenerators.get(vTransitionPoint.layer);
                    PathFinder.Node vNode = vGraph.getNode(vTransitionPoint.position.getX(), 
                                                           vTransitionPoint.position.getY());
                    if (!vTransitionNodes.contains(vNode)) 
                    {
                        vTransitionNodes.add(vNode);
                        vFile.write("<Transition x=\"" + vTransitionPoint.position.getX() + "\" " +
                                                "y=\"" + vTransitionPoint.position.getY() + "\" " +
                                                "layer=\"" + vTransitionPoint.layer + "\" " +
                                                "direction=\"down\"/>" + newline);                

                        // Get node from previous layer
                        GraphGenerator vGraph2 = mGraphGenerators.get(vTransitionPoint.layer-1);
                        PathFinder.Node vNode2 = vGraph2.getNode(vTransitionPoint.position.getX(), 
                                                                 vTransitionPoint.position.getY());
                        // Create link to node from previous layer
                        vGraph.createLink(vNode, vNode2);
                    }
                }
                
                vFile.write("</Transitions>");                
                vFile.flush();
                vFile.close();        
            } 
            catch (Exception e) 
            {
                GameLogger.log(e);
            }                
        }
        
        // Fill level-graph
        mLevelGraph.clear();
        for (GraphGenerator vGraph : mGraphGenerators)
            mLevelGraph.addAll(vGraph.getNodes());
    }
    
    /**
     * Parses a level file.
     * @param file name of the xml file
     * @param level reference to the empty level
     * @throws ParserConfigurationException error during xml-parsing
     * @throws SAXException xml-file is not xml-conform
     * @throws IOException file not found
     */
    public void parse(String file, Level level) throws ParserConfigurationException, SAXException, IOException {

        mState = State.BEGIN;
        mLevel = level;
        
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();

        //parse the file and also register this class for call backs
        sp.parse(file, this);
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (mState) 
        {
            case BEGIN:
            {
                if (qName.equals("Level"))
                {
                    mLevel.create(attributes.getValue("name"));
                    mState = State.LEVEL;
                }
                else
                    throw new SAXException("Level-Tag expected, instead of '" + qName + "'.");
                break;
            }
            case LEVEL:
            {
                if (qName.equals("Map"))
                {
                    mState = State.MAP;
                }
                break;
            }
            case MAP:
            {
                if (qName.equals("Textures"))
                {
                    mState = State.TEXTURES;
                }
                else if (qName.equals("Players"))
                {
                    mState = State.PLAYERS;
                }
                else if (qName.equals("Objects"))
                {
                    mState = State.OBJECTS;
                }
                else if (qName.equals("Goals"))
                {
                    mState = State.GOALS;
                }
                break;
            }
            case TEXTURES:
            {
                if (qName.equals("Texture"))
                {
                    int vX = Integer.parseInt(attributes.getValue("x"));
                    int vY = Integer.parseInt(attributes.getValue("y"));
                    int vLayer = Integer.parseInt(attributes.getValue("layer"));

                    // Collision Texture
                    BufferedImage vCollImg = (BufferedImage)GraphicsEngine.getInstance().getTexture(
                            attributes.getValue("name") + GameConstants.COLLISION_EXTENSION + 
                            GameConstants.COLLISION_FILE_EXTENSION, mLevel.getName());
                    if (vCollImg == null)
                    {
                        GameLogger.log("Image not found: " + attributes.getValue("name") + GameConstants.COLLISION_EXTENSION + GameConstants.COLLISION_FILE_EXTENSION);
                    }

                    CollisionTexture vCollision = new CollisionTexture();
                    vCollision.setCollisionTexture(vCollImg);
                    
                    // Texture Animation
                    int vFrequency = Integer.parseInt(attributes.getValue("change"));
                    int vAnimationSize = Integer.parseInt(attributes.getValue("number"));
                    Animation vTexture = GraphicsEngine.getInstance().getTextureAnimation(
                            attributes.getValue("name"), mLevel.getName(), vAnimationSize, vFrequency);
                    vTexture.setPosition(new Vector2D((double)vX, (double)vY));
                    
                    // Create a level tile
                    String vMusic = attributes.getValue("music");
                    String [] vPlayList = null;
                    if (vMusic != null) {
                        vPlayList = vMusic.split(",");
                    }
                    mLevelTile = new Tile(vCollision, vTexture, vPlayList);
                    mLevel.createLevelTile(mLevelTile, vX, vY, vLayer);
                    mState = State.TEXTURE;
                }
                break;
            }
            case PLAYERS:
            {
                if (qName.equals("Player"))
                {
                    // Create player from given data
                    int vLayer = Integer.parseInt(attributes.getValue("layer"));
                    double vX = Double.parseDouble(attributes.getValue("x"));
                    double vY = Double.parseDouble(attributes.getValue("y"));
                    double vRotation = Double.parseDouble(attributes.getValue("rotation"));
                    mLevel.createPlayer(attributes.getValue("vehicle"), 
                            new Vector2D(vX, vY), vLayer, vRotation);
                }
                break;
            }
            case GOALS:
            {
                if (qName.equals("Goal"))
                {
                    // create a new goal for a particular player, 
                    // optionally with time-limit
                    int player = Integer.parseInt(attributes.getValue("id"));
                    String vTimeLimit = attributes.getValue("time");
                    mCurrentGoal = new Goal(player);
                    if (vTimeLimit != null)
                        mCurrentGoal.setTimeLimit(Long.parseLong(vTimeLimit));
                    mState = State.GOAL;
                }
                break;
            }
            case GOAL:
            {
                if (qName.equals("Set"))
                {
                    mCurrentGoal.set(attributes.getValue("id"), attributes.getValue("value"));
                }
                break;
            }
            case OBJECTS:
            {
                if (qName.equals("Object"))
                {
                    // Create a new object on the map
                    int vLayer = Integer.parseInt(attributes.getValue("layer"));
                    double vX = Double.parseDouble(attributes.getValue("x"));
                    double vY = Double.parseDouble(attributes.getValue("y"));
                    double vRotation = Double.parseDouble(attributes.getValue("rotation"));
                    mLevelObject = mLevel.createObject(attributes.getValue("id"), 
                            new Vector2D(vX, vY), vLayer, vRotation);
                    mState = State.OBJECT;
                }
                break;
            }
            case OBJECT:
            {
                if (qName.equals("Controller"))
                {
                    mController = ObjectControllerFactory.create(attributes.getValue("id"));
                    mState = State.CONTROLLER;
                }
                break;
            }
            case CONTROLLER:
            {
                if (qName.equals("Set"))
                {
                    mController.setProperty(attributes.getValue("id"), attributes.getValue("value"));
                }
                break;
            }
        }
    }

    @Override
    public void endElement (String uri, String localName, String qName) throws SAXException {
        switch (mState) 
        {
            case LEVEL:
            {
                if (qName.equals("Level"))
                {
                    mState = State.BEGIN;
                }
                break;
            }
            case MAP:
            {
                if (qName.equals("Map"))
                    mState = State.LEVEL;
                break;
            }
            case OBJECTS:
            {
                if (qName.equals("Objects"))
                    mState = State.MAP;
                break;
            }
            case PLAYERS:
            {
                if (qName.equals("Players"))
                {
                    mPlayerGoals = new Goal[mLevel.getPlayers().size()];
                    for (int i = 0; i < mPlayerGoals.length; i++) 
                        mPlayerGoals[i] = new Goal(i);
                    mState = State.MAP;
                }
                break;
            }
            case GOALS:
            {
                if (qName.equals("Goals"))
                {
                    mState = State.MAP;
                    mLevel.getStatistics().initialize(mPlayerGoals, mLevel.getPlayers());
                }
                break;
            }
            case TEXTURES:
            {
                if (qName.equals("Textures"))
                {
                    generateQuadtree();
                    mState = State.MAP;
                }
                break;
            }
            case TEXTURE:
            {
                if (qName.equals("Texture"))
                    mState = State.TEXTURES;
                break;
            }
            case PLAYER:
            {
                if (qName.equals("Player"))
                    mState = State.PLAYERS;
                break;
            }
            case OBJECT:
            {
                if (qName.equals("Object"))
                {
                    mState = State.OBJECTS;
                }
                break;
            }
            case CONTROLLER:
            {
                if (qName.equals("Controller"))
                {
                    mController.setControlObject(mLevelObject);
                    mController.initialize(mGraphGenerators, new PathFinder(mLevelGraph)); 
                    mLevelObject.setController(mController);
                    mState = State.OBJECT;
                }
                break;
            }
            case GOAL:
            {
                if (qName.equals("Goal"))
                {
                    mPlayerGoals[mCurrentGoal.getPlayerId()] = mCurrentGoal;
                    mState = State.GOALS;
                }
            }
        }
    }


}
