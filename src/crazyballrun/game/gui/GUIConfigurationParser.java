/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.gui;

import crazyballrun.game.GameSettings;
import java.io.IOException;

import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The GUIConfigurationParser reads the GUI configuration file and translates
 * it into different GUI-screens (called "Frames") and their content (GUI
 * controls like buttons, textboxes, messages, etc.). The parser passes 
 * specified "Attributes" to the GUI controls. 
 * @author Timm Hoffmeister
 */
public class GUIConfigurationParser extends DefaultHandler {

    /**
     * States of the XML-Parser (in which tag the parser currently is).
     */
    private enum State {
        BEGIN,
        GUI,
        FRAME,
        CONTROL
    }

    /**
     * Current state in the XML file during parsing process.
     */
    private State mState = State.BEGIN;
    
    /**
     * List containing the GUIFrames. 
     */
    private HashMap <String, GUIFrame> mFrames = null;
    
    /**
     * GUIFrame the parser is currently working on.
     */
    private GUIFrame mCurrentFrame = null;

    /**
     * GUIControl the parser is currently working on.
     */
    private GUIControl mCurrentControl = null;
    
    /**
     * String name of the current control tag.
     */
    private String mCurrentControlTagName = null;
    
    /**
     * Constructor of the GUIConfigurationParser.
     */
    public GUIConfigurationParser () {
        super();
    }    
    
    /**
     * Parses the GUI configurations from the given xml file. 
     * @param file name of the xml file
     * @param frames reference to the list of GUIFrames to fill up while parsing
     * @throws ParserConfigurationException error during xml-parsing
     * @throws SAXException xml-file is not xml-conform
     * @throws IOException file not found
     */
    public void parse(String file, HashMap <String, GUIFrame> frames) throws ParserConfigurationException, SAXException, IOException {

        mFrames = frames;
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
                if (qName.equals("GUI")) {
                    mState = State.GUI;
                }
                break;
            
            case GUI:
                if (qName.equals("Settings")) {
                    Integer vRecSize = Integer.parseInt(attributes.getValue("recsize"));
                    GameSettings.getInstance().addValue("GUIElementPixelSize", vRecSize);
                } 
                else if (qName.equals("Frame")) {
                    mState = State.FRAME;
                    mCurrentFrame = new GUIFrame(attributes.getValue("id"));
                    mFrames.put(mCurrentFrame.getId(), mCurrentFrame);
                } 
                break;
           
            case FRAME:
                mCurrentControlTagName = qName;
                mCurrentControl = GUIControlFactory.create(qName);
                Double x1 = Double.parseDouble(attributes.getValue("x1"));
                Double y1 = Double.parseDouble(attributes.getValue("y1"));
                Double x2 = Double.parseDouble(attributes.getValue("x2"));
                Double y2 = Double.parseDouble(attributes.getValue("y2"));
                mCurrentControl.create(attributes.getValue("id"), mCurrentFrame, x1, y1, x2, y2);
                mCurrentFrame.addControl(mCurrentControl);
                mState = State.CONTROL;
                break;
                
            case CONTROL:
                if (qName.equals("Attribute")) {
                    mCurrentControl.setAttribute(attributes.getValue("id"), attributes.getValue("value"));
                } 
                break;
                
        }
    }

    @Override
    public void endElement (String uri, String localName, String qName) throws SAXException {
        switch (mState) 
        {
            case FRAME:
                if (qName.equals("Frame")) {
                    mCurrentFrame.initialize();
                    mCurrentFrame = null;
                    mState = State.GUI;
                }
                break;
            case CONTROL:
                if (qName.equals(mCurrentControlTagName)) {
                    mCurrentControl = null;
                    mState = State.FRAME;
                }
                break;
        }        
    }    
    
}
