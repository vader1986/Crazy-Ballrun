/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game;

import crazyballrun.game.controls.EventMapper;
import crazyballrun.game.controls.IControl;

import crazyballrun.game.controls.IControlFactory;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The GameSettingsParser parses an XML file and fills the GameSettings.
 * @author Timm Hoffmeister
 */
public class GameSettingsParser extends DefaultHandler {

    /**
     * States of the XML-Parser (in which tag the parser currently is).
     */
    private enum State {
        BEGIN, 
        GAME, 
        CONTROLS, 
        CONTROL
    }
    
    /**
     * Current state in the XML file during parsing process.
     */
    private State mState = State.BEGIN;
    
    /**
     * Current control which is being parsed.
     */
    private IControl mCurrentControl = null;
    
    /**
     * Reference to the game settings. 
     */
    private GameSettings mSettings = null;
    
    /**
     * Reference to a list of all controls. 
     */
    private ArrayList <IControl> mControls = null;

    /**
     * Reference to the EventMapper.
     */
    private EventMapper mEventMapper = null;
    
    /**
     * Constructor of the GameSettingsParser.
     */
    public GameSettingsParser () {
        super();
    }
    
    /**
     * Parses the game configurations from the game configuration xml file. 
     * @param file name of the xml file
     * @param controls list of all controls (Keyboard, Mouse, ... )
     * @param eventMapper the EventMapper which has to be filled with event-action-pairs
     * @throws ParserConfigurationException error during xml-parsing
     * @throws SAXException xml-file is not xml-conform
     * @throws IOException file not found
     */
    public void parse(String file, ArrayList <IControl> controls, EventMapper eventMapper) throws ParserConfigurationException, SAXException, IOException 
    {
        mControls = controls;
        mEventMapper = eventMapper;
        mSettings = GameSettings.getInstance();
        mState = State.BEGIN;

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
                if (qName.compareTo("Game") == 0) {
                    mState = State.GAME;
                }
                break;
            
            case GAME:
                if (qName.compareTo("Screen") == 0) {
                    mSettings.addValue("ResolutionWidth", Integer.parseInt(attributes.getValue("resolutionx")));
                    mSettings.addValue("ResolutionHeight", Integer.parseInt(attributes.getValue("resolutiony")));
                    mSettings.addValue("ColorDepth", Integer.parseInt(attributes.getValue("colordepth")));
                    mSettings.addValue("RefreshRate", Integer.parseInt(attributes.getValue("refreshrate")));
                } 
                else if (qName.compareTo("Controls") == 0) {
                    mState = State.CONTROLS;
                }
                break;
           
            case CONTROLS:
                if (qName.compareTo("Control") == 0) {
                    mState = State.CONTROL;
                    mCurrentControl = IControlFactory.create( attributes.getValue("id") );
                    mControls.add(mCurrentControl);
                }
                break;
                
            case CONTROL: 
                if (qName.compareTo("Action") == 0) {
                    String vId = attributes.getValue("id");
                    String vEvent = attributes.getValue("event");
                    mEventMapper.add(vEvent, vId);
                }
                break;
        }
    }

    @Override
    public void endElement (String uri, String localName, String qName) throws SAXException {
        switch (mState) 
        {
            case CONTROLS:
                if (qName.compareTo("Controls") == 0)
                    mState = State.GAME;
                break;
            case CONTROL:
                if (qName.compareTo("Control") == 0) {
                    mState = State.CONTROLS;
                }
                break;
        }        
    }        
    
}
