/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game;

import crazyballrun.game.controls.ControlEngine;
import crazyballrun.game.controls.EventMapper;
import crazyballrun.game.graphics.GraphicsEngine;
import crazyballrun.game.controls.IControl;
import crazyballrun.game.gui.GUIEngine;
import crazyballrun.game.level.LevelEngine;
import crazyballrun.game.music.MusicEngine;
import crazyballrun.game.physics.PhysicsEngine;
import java.util.ArrayList;

/**
 * The Game-Class initializes and runs the Game-Engines. It has a "getInstance"-
 * Method (singleton pattern) to ensure there's just one instance of Game. 
 * @author Timm Hoffmeister
 */
public class Game {

    /**
     * Specifies the game mode. 
     * (SP = Singleplayer, MP = multiplayer)
     */
    public enum GameMode {
        /**
         * Singleplayer campagne game (influnces stats of local account).
         */
        SP_CAMPAGNE,
        /**
         * Singleplayer normal match (does not influnce account stats).
         */
        SP_MATCH,
        /**
         * Singleplayer "hot seat" with several players on one screen (no campagne!).
         */
        SP_SPLITSCREEN, 
        /**
         * Multiplayer coop (influnces online-account stats).
         */
        MP_CAMPAGNE, 
        /**
         * Multiplayer normal match (no influnce to the stats).
         */
        MP_MATCH
    }
    
    /**
     * Enumeration of all game states. 
     */
    public enum GameState {
        /**
         * User is in one of the menu screens.
         */
        IN_GUI,
        /**
         * User playing (in-game).
         */
        IN_GAME,
        /**
         * User paused the game (state between playing and showing menu).
         */
        IN_GAME_PAUSED
    }

    /**
     * The global Game-instance. 
     */
    private static Game game = null;

    /**
     * Provides access to the game. 
     * @return a reference to the Game. 
     */
    public static Game getInstance() {
        if (game == null)
            game = new Game();
        return game;
    }
    
    /**
     * Game state (in gui, in game, paused, ...).
     */
    private GameState mGameState = GameState.IN_GUI;
    
    /**
     * Private constructor of the Game-Class. 
     */
    private Game() {
        
    }

    /**
     * Checks if the game is a singleplayer game (includes splitscreen!).
     * @return "true" if the game is singleplayer
     */
    public boolean isSingleplayer () {
        return !isMultiplayer();
    }
    
    /**
     * Checks if the game is a multiplayer game (NOT splitscreen!).
     * @return "true" if game is multiplayer
     */
    public boolean isMultiplayer () {
        GameMode vMode = getGameMode();
        if (vMode == GameMode.MP_CAMPAGNE ||
                vMode == GameMode.MP_MATCH)
            return true;
        return false;
    }
    
    /**
     * Reads the GameMode from the GameSettings. 
     * @return game mode (SP, MP, Campagne, Splitscreen, Match, ...)
     */
    public GameMode getGameMode () {
        Game.GameMode vGameMode = Game.GameMode.valueOf(
                ((String)GameSettings.getInstance().getValue("GameMode"))); 
        return vGameMode;
    }
    
    /**
     * Gets the current state of the game. 
     * @return the current game state. 
     */
    public GameState getGameState () {
        return mGameState;
    }
    
    /**
     * Sets the current game state.
     * @param state new game state
     */
    public void setGameState (GameState state) 
    {    
        try
        {
            switch (state) 
            {
                case IN_GUI:
                {
                    GUIEngine.getInstance().setCurrentFrame(GameConstants.GUI_START_FRAME_ID);
                    GraphicsEngine.getInstance().renderGUI();
                    // TODO: MultiplayerEngine.disconnect()
                    PhysicsEngine.getInstance().physicsPause();
                }
                break;

                case IN_GAME_PAUSED:
                {
                    if (mGameState == GameState.IN_GAME) 
                    {
                        if (isSingleplayer())
                            PhysicsEngine.getInstance().physicsPause(); 
                        GUIEngine.getInstance().setCurrentFrame(GameConstants.GUI_BREAK_FRAME_ID);
                    }
                    else if (mGameState == GameState.IN_GUI)
                    {
                        PhysicsEngine.getInstance().physicsContinue(); 
                        PhysicsEngine.getInstance().setAmpleState(true);
                        GUIEngine.getInstance().setCurrentFrame(GameConstants.GUI_IN_GAME_ID);
                    }
                }
                break;

                case IN_GAME:
                {
                    PhysicsEngine.getInstance().setAmpleState(false);
                    if (isSingleplayer())
                        PhysicsEngine.getInstance().physicsContinue(); 
                    GraphicsEngine.getInstance().renderGame();
                    GUIEngine.getInstance().setCurrentFrame(GameConstants.GUI_IN_GAME_ID);
                }
                break;
            }
            
            mGameState = state;
        }
        catch (Exception e) 
        {
            GameLogger.log(e);
            System.exit(1);
        } 
    }
    
    /**
     * Exit the game.
     */
    public void endGame () {
        try
        {
            GraphicsEngine.getInstance().end();
            MusicEngine.getInstance().end();
        }
        catch (Exception e)
        {
            GameLogger.log(e);
        }
        GameLogger.close();
        System.exit(0);
    }
    
    /**
     * Initializes and runs the game. 
     */
    public void startGame () {
        
        try 
        {
            // Create GameSettings
            GameSettingsParser vParser = new GameSettingsParser();
            ArrayList <IControl> vControlList = new ArrayList<IControl>();
            EventMapper vEventMapper = new EventMapper();
            vParser.parse(GameConstants.GAME_CONFIG_FILE_PATH, vControlList, vEventMapper);

            // Initialize Engines
            ControlEngine.getInstance().initialize(vEventMapper, vControlList);
            GraphicsEngine.getInstance().initialize();
            GUIEngine.getInstance().initialize();
            MusicEngine.getInstance().initialize();
            LevelEngine.getInstance().initialize();
            PhysicsEngine.getInstance().initialize();
            // TODO: MultiplayerEngine - offers functionaliy for connect/discconect,
            //       send messages, receive messages and interpret messages. This
            //       engine should be used by physics engine to synchronized in
            //       multiplayer games. It's also used by the GameFunctions, to
            //       send to other computers which action has been performed.
            //       When receiving messages it will interpret them either as 
            //       function calls for the GameFunctions or as synchronization
            //       messages (position, speed, ...). If Game.getGameState == 
            //       IN_GUI, then the messages are interpret as configuration
            //       or chat messages. 
            
            // Run Engine-Threads:
            GUIEngine.getInstance().start();
            GraphicsEngine.getInstance().start();
            MusicEngine.getInstance().start();
            PhysicsEngine.getInstance().start();
            
            // Initial state GUI
            setGameState(GameState.IN_GUI);
        }
        catch (Exception e)
        {
            GameLogger.log(e);
        }
    }

}
