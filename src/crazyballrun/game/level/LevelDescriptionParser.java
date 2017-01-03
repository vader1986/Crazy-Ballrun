/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.level;

import crazyballrun.game.GameConstants;
import crazyballrun.game.GameLogger;
import crazyballrun.game.graphics.GraphicsEngine;
import crazyballrun.game.music.MusicEngine;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The LevelDescriptionParser is a "pre-parser" for the LevelParser. It's parsing
 * fast at the start-up of the game and just reads a small amount of information
 * from each level to describe the level for the user in the GUI.
 * @author Timm Hoffmeister
 */
public class LevelDescriptionParser extends DefaultHandler {
 
    /**
     * States of the XML-Parser (in which tag the parser currently is).
     */
    private enum State {
        BEGIN,
        DESCRIPTION,
        LEVEL,
        MAP,
        PLAYERS,
        PLAYER
    }
    
    /**
     * Current state in the XML file during parsing process.
     */
    private State mState = State.BEGIN;
    
    /**
     * Tag-Depth of the XML-parser.
     */
    private int mDepth = 0;
    
    /**
     * Reference to the level description.
     */
    private LevelDescription mDescription = null;
    
    /**
     * Parses a level-file but skips all tags which are not used to build up
     * the level description.
     * @param file level filename
     * @param description reference to the level description which has to be 
     * filled with information
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException 
     */
    public void parse(String file, LevelDescription description) throws ParserConfigurationException, SAXException, IOException 
    {
        mState = State.BEGIN;
        mDepth = 0;
        mDescription = description;

        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();

        //parse the file and also register this class for call backs
        sp.parse(file, this);

        // use default load screen if not defined
        if (mDescription.mLoadScreen == null)
            mDescription.mLoadScreen = GraphicsEngine.getInstance().getGraphics(GameConstants.LEVEL_DEFAULT_LOADSCREEN);
        if (mDescription.mDefaultSound == null) 
            mDescription.mDefaultSound = MusicEngine.getInstance().getMusic(GameConstants.LEVEL_DEFAULT_MUSIC);
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (mState == State.DESCRIPTION) {
            if (mDescription.mDescription == null)
                mDescription.mDescription = "";
            mDescription.mDescription += new String(ch, start, length);
        }
    }
  
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
    {
        switch (mState) 
        {
            case BEGIN:
            {
                if (qName.equals("Level")) {
                    mDescription.mLevelName = attributes.getValue("name");
                    mDescription.mAuthor = attributes.getValue("author");
                    try {
                        if (attributes.getValue("loadscreen") != null) {
                            mDescription.mLoadScreen = GraphicsEngine.getInstance().getGraphics(attributes.getValue("loadscreen"), mDescription.mLevelName);
                        }
                    } catch (Exception e) {
                        GameLogger.log(e);
                        mDescription.mLoadScreen = null;
                    }
                    mState = State.LEVEL;
                }
                break;
            }
            case LEVEL:
            {
                if (qName.equals("Description")) {
                    mState = State.DESCRIPTION;
                }
                else if (qName.equals("Map")) {
                    mDescription.mWidth = Integer.parseInt(attributes.getValue("width"));
                    mDescription.mHeight = Integer.parseInt(attributes.getValue("height"));
                    mDescription.mLayers = attributes.getValue("layers");
                    try {
                        String vMiniMapRoot = attributes.getValue("minimap");
                        if (vMiniMapRoot != null) {
                            mDescription.mMiniMap = new Image[Integer.parseInt(mDescription.mLayers)];
                            for (int i = 0; i < mDescription.mMiniMap.length; i++) 
                            {
                                String path = GameConstants.LEVEL_RESOURCE_PATH + File.separator + mDescription.mLevelName + File.separator +  vMiniMapRoot + i + GameConstants.IMAGE_FILE_EXTENSION;                             
                                mDescription.mMiniMap[i] = GraphicsEngine.sLoadImage(path);
                            }
                        }
                    } catch (Exception e) {
                        GameLogger.log(e);
                        mDescription.mMiniMap = null;
                    }
                    mState = State.MAP;
                }
                break;
            }
            case MAP:
            {
                if (qName.equals("Players"))
                {
                    mState = State.PLAYERS;
                }
                break;
            }
            case PLAYERS:
            {
                if (qName.equals("Player"))
                {
                    mState = State.PLAYER;
                }
                break;
            }
                
        }
    }
    
    @Override
    public void endElement (String uri, String localName, String qName) throws SAXException {
        switch (mState) 
        {
            case DESCRIPTION:
            {
                if (qName.equals("Description"))
                    mState = State.LEVEL;
                break;
            }
            case MAP:
            {
                if (qName.equals("Map"))
                    mState = State.LEVEL;
                break;
            }
            case PLAYERS:
            {
                if (qName.equals("Players"))
                    mState = State.MAP;
                break;
            }
            case PLAYER:
            {
                if (qName.equals("Player"))
                {
                    mState = State.PLAYERS;
                    mDescription.mNumberPlayers++;
                }
                break;
            }      
        }        
    }    
    
}
