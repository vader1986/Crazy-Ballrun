/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level.objects;

import crazyballrun.game.physics.bodies.PhysicalBody;
import crazyballrun.game.physics.bodies.PhysicalBodyFactory;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The LevelObjectParser reads an XML-file and extracts all necessary information
 * from it to create a valid instance LevelObject. Following type (and more) of
 * information are read from the file: animations (idle, moving, dieing), sound
 * effect (acceleration, using brake, collision) and collision data (which type of
 * RigidBody is it? What are possible collision points?). It also describes if 
 * the object has weapons to use. 
 * 
 * @author Timm Hoffmeister
 */
public class LevelObjectParser extends DefaultHandler {

    /**
     * XML-Format:
     * 
     * <Object id="SimpleCar">
     *  
     *  <!-- LevelObject-specific stuff -->
     *  <Properties>
     *      
     *      <!-- Vehicle animations -->
     *      <Set id="IdleAnimation" value="truck_idle"/>
     *      <Set id="IdleAnimationNumber" value="1"/>
     *      <Set id="MovingAnimation" value="truck_moving"/>
     *      <Set id="MovingAnimationNumber" value="4"/>
     * 
     *  </Properties>
     * 
     *  <RigidBody id="Vehicle">

     *      <!-- Vechicle is a Rectangle (coordinates in pixels) -->
     *      <Set id="CollisionPoint" value="(5,5)"/>
     *      <Set id="CollisionPoint" value="(15,5)"/>
     *      <Set id="CollisionPoint" value="(15,25)"/>
     *      <Set id="CollisionPoint" value="(5,25)"/>
     * 
     *      <!-- Set Physical Properties -->
     *      ...
     * 
     *  </RigidBody>
     * 
     * </Object>
     * 
     */
    
    /**
     * States of the XML-Parser (in which tag the parser currently is).
     */
    private enum State {
        BEGIN,
        OBJECT,
        PROPERTIES,
        RIGIDBODY
    }

    /**
     * Current state in the XML file du
     * ring parsing process.
     */
    private State mState = State.BEGIN;
    
    /**
     * Reference to the LevelObject created by the parser.
     */
    private LevelObject mLevelObject = null;
    
    /**
     * Current rigid body. 
     */
    private PhysicalBody mRigidBody = null;
    
    /**
     * Constructor of the LevelObjectParser.
     */
    public LevelObjectParser () {
        super();
    }    

    /**
     * Parses an object file. Each object file may contain only ONE <Object>-Tag!
     * @param file name of the xml file
     * @return reference to the LevelObject
     * @throws ParserConfigurationException error during xml-parsing
     * @throws SAXException xml-file is not xml-conform
     * @throws IOException file not found
     */
    public LevelObject parse(String file) throws ParserConfigurationException, SAXException, IOException {

        mState = State.BEGIN;
        
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();

        //parse the file and also register this class for call backs
        sp.parse(file, this);
        return mLevelObject;
    }    
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (mState) 
        {
            case BEGIN:
            {
                if (qName.equals("Object"))
                {
                    mLevelObject = LevelObjectFactory.create(attributes.getValue("id"));
                    mState = State.OBJECT;
                }
                break;
            }
            case OBJECT:
            {
                if (qName.equals("Properties"))
                {
                    mState = State.PROPERTIES;
                }
                else if (qName.equals("RigidBody"))
                {
                    mRigidBody = PhysicalBodyFactory.create(attributes.getValue("id"));
                    mState = State.RIGIDBODY;
                }
                break;
            }
            case PROPERTIES:
            {
                if (qName.equals("Set"))
                {
                    mLevelObject.set(attributes.getValue("id"), attributes.getValue("value"));
                }
                break;
            }
            case RIGIDBODY:
            {
                if (qName.equals("Set"))
                {
                    mRigidBody.set(attributes.getValue("id"), attributes.getValue("value"));
                }
                break;
            }
        }
    }
    
    @Override
    public void endElement (String uri, String localName, String qName) throws SAXException {
        switch (mState) 
        {
            case OBJECT:
            {
                if (qName.equals("Object"))
                {
                    mState = State.BEGIN;
                }
                break;
            }
            case PROPERTIES:
            {
                if (qName.equals("Properties"))
                    mState = State.OBJECT;
                break;
            }
            case RIGIDBODY:
            {
                if (qName.equals("RigidBody"))
                {
                    mRigidBody.setLevelObject(mLevelObject);
                    mRigidBody.initialize();
                    mLevelObject.setRigidBody(mRigidBody);
                    mState = State.OBJECT;
                }
                break;
            }
        }
    }
    
}
