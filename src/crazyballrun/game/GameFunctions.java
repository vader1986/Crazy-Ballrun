/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game;

import crazyballrun.game.controls.EventContent;
import crazyballrun.game.level.controllers.Player;

/**
 * The GameFunctions class overs access to main game functionality, like 'accelerate
 * current player', 'use break for player' and many more. The functions are implemented
 * for both, singleplayer and multiplayer mode and any game setting. 
 * 
 * The game functions have to be initialized with each start/change of level to
 * get access to the players, objects and the whole physical environment. 
 * 
 * The GameFunctions is mainly used by the EventMapper which reacts on user input
 * (therefore maps events on actions). GameFunctions provides a method perform()
 * for that purpose. It contains one element of "Action"-enumeration for argument.
 * All of the private methods called by perform()-method have no parameters, so the
 * behaviour is completely defined by the enumeration-name specified for the action
 * and the private methods itself. Thus, the GameFunctions have access to every 
 * game engine: PhysicsEngine to manipulate physics, NetworkEngine to write chat-
 * messages, GUIEngine to offer a "pause/continue"-menu, etc. 
 * 
 * For a translation of String-actions to enumeration-actions the GameFunctions
 * class provides the method translate(). 
 * 
 * @author Timm Hoffmeister
 */
public class GameFunctions {

    /**
     * Constant player id specifying no player. 
     */
    private final static int NO_PLAYER = -1;
    
    /**
     * Contains all players of the currently loaded map (the array is filled by
     * the LevelEngine after parsing). 
     */
    public static Player [] sPlayer = null;
    
    /**
     * Player number specifies the corresponding player-tag in the level-file. 
     * For event-action-mapping the main player is player "0" but its player-
     * number may be any of the players specified the level-file. 
     */
    public static int [] sPlayerNumber = null;
    
    /**
     * This player-specific array showes if the "turn left"-action is being 
     * performed by the player. 
     */
    public static boolean [] sPlayerTurnLeft = null;

    /**
     * This player-specific array showes if the "turn right"-action is being 
     * performed by the player. 
     */
    public static boolean [] sPlayerTurnRight = null;
    
    /**
     * This player-specific array showes if the "acceleration"-action is being 
     * performed by the player. 
     */
    public static boolean [] sPlayerAccelerate = null;

    /**
     * This player-specific array showes if the "backward-acceleration"-action 
     * is being performed by the player. 
     */
    public static boolean [] sPlayerAccBackwards = null;
    
    /**
     * Enumeration of all possible actions the GameFunctions-class offers to
     * external users. 
     */
    public enum Action 
    {
        /**
         * Exits the game. 
         */
        GAME_EXIT,
        /**
         * Pause the game (if level has been loaded yet).
         */
        GAME_PAUSE,
        /**
         * Accelerate the main player playing at this computer.
         */
        MAIN_PLAYER_ACCELERATION,
        /**
         * Backward-accelerate the main player playing at this computer.
         */
        MAIN_PLAYER_BACKWARDS,
        /**
         * Release acceleration of the main player playing at this computer.
         */
        MAIN_PLAYER_STOP_ACCELERATION,
        /**
         * Release backward acceleration of the main player playing at this computer.
         */
        MAIN_PLAYER_STOP_BACKWARDS,
        /**
         * Turn left the main player playing at this computer.
         */
        MAIN_PLAYER_TURN_LEFT,
        /**
         * Turn right the main player playing at this computer.
         */
        MAIN_PLAYER_TURN_RIGHT,
        /**
         * Release turning left the main player playing at this computer.
         */
        MAIN_PLAYER_STOP_LEFT,
        /**
         * Release turning right the main player playing at this computer.
         */
        MAIN_PLAYER_STOP_RIGHT,
        /**
         * Main player playing at this computer shoots a rocket (if available).
         */
        MAIN_PLAYER_USE_ROCKET,
        /**
         * Accelerate the second player playing at this computer (splitscreen!).
         */
        MAIN_PLAYER2_ACCELERATION,
        /**
         * Backward accelerate the second player playing at this computer (splitscreen!).
         */
        MAIN_PLAYER2_BACKWARDS,
        /**
         * Release acceleration of the second player playing at this computer (splitscreen!).
         */
        MAIN_PLAYER2_STOP_ACCELERATION,
        /**
         * Release backward acceleration of the second player playing at this computer (splitscreen!).
         */
        MAIN_PLAYER2_STOP_BACKWARDS,
        /**
         * Turn left the second player playing at this computer (splitscreen!).
         */
        MAIN_PLAYER2_TURN_LEFT,
        /**
         * Turn right the second player playing at this computer (splitscreen!).
         */
        MAIN_PLAYER2_TURN_RIGHT,
        /**
         * Release turning left the second player playing at this computer (splitscreen!).
         */
        MAIN_PLAYER2_STOP_LEFT,
        /**
         * Release turning right the second player playing at this computer (splitscreen!).
         */
        MAIN_PLAYER2_STOP_RIGHT,
        /**
         * Second player playing at this computer shoots a rocket (if available).
         */
        MAIN_PLAYER2_USE_ROCKET
    }
    
    /**
     * Display names of the actions (for graphical user interface).  
     */
    public static String [] sActions = {
        "Exit Game",
        "Pause Game",
        "Player 1 Acceleration",
        "Player 1 Backwards",
        "Player 1 Stop Acceleration",
        "Player 1 Stop Backward-Acceleration",
        "Player 1 Turn Left",
        "Player 1 Turn Right",
        "Player 1 Stop Turn Left",
        "Player 1 Stop Turn Right",
        "Player 1 Use Rocket",
        "Player 2 Acceleration",
        "Player 2 Backwards",
        "Player 2 Stop Acceleration",
        "Player 2 Stop Backward-Acceleration",
        "Player 2 Turn Left",
        "Player 2 Turn Right",
        "Player 2 Stop Turn Left",
        "Player 2 Stop Turn Right",
        "Player 2 Use Rocket"
    }; 

    /**
     * Finds the display name for the action enumeration element.
     * @param action specified action
     * @return the display name of the specified action.
     */
    public static String translate (Action action) {
        return sActions[action.ordinal()];
    }
    
    /**
     * Translates a string into an element from the Action-enumeration.
     * @param action string-representation of the action
     * @return an element of the Action-enumeration fitting to the action-argument.
     */
    public static Action translate (String action) {
        return Action.valueOf(action);
    }
    
    /**
     * Calls a particular private method to perform the specified action. Those
     * actions may not be player-specific or position-dependent. 
     * @param action action to perform
     */
    public static void perform (Action action) {
        perform (action, null, -1);
    }

    /**
     * Performs an action. 
     * @param action action to perform
     * @param content event content
     */
    public static void perform (Action action,  EventContent content) {
        perform (action, content, -1);
    }

    /**
     * Performs a player- and position-specific action.
     * @param action action to perform
     * @param player player number
     * @param content event content
     */
    public static void perform (Action action, EventContent content, int player) 
    {        
        switch (action) 
        {
            case GAME_EXIT: GAME_EXIT(); break;
            case GAME_PAUSE: GAME_PAUSE(); break;
                
            // Player 1
            case MAIN_PLAYER_ACCELERATION:          PLAYER_ACCELERATION(sPlayerNumber[0]); break;
            case MAIN_PLAYER_BACKWARDS:             PLAYER_BACKWARDS(sPlayerNumber[0]); break;
            case MAIN_PLAYER_STOP_ACCELERATION:     PLAYER_STOP_ACCELERATION(sPlayerNumber[0]); break;
            case MAIN_PLAYER_STOP_BACKWARDS:        PLAYER_STOP_BACKWARDS(sPlayerNumber[0]); break;
            case MAIN_PLAYER_TURN_LEFT:             PLAYER_TURN_LEFT(sPlayerNumber[0]); break;
            case MAIN_PLAYER_TURN_RIGHT:            PLAYER_TURN_RIGHT(sPlayerNumber[0]); break;
            case MAIN_PLAYER_STOP_LEFT:             PLAYER_STOP_LEFT(sPlayerNumber[0]); break;
            case MAIN_PLAYER_STOP_RIGHT:            PLAYER_STOP_RIGHT(sPlayerNumber[0]); break;
            case MAIN_PLAYER_USE_ROCKET:            PLAYER_USE_ROCKET(sPlayerNumber[0]); break;
                
            // Player 2
            case MAIN_PLAYER2_ACCELERATION:         PLAYER_ACCELERATION(sPlayerNumber[1]); break;
            case MAIN_PLAYER2_BACKWARDS:            PLAYER_BACKWARDS(sPlayerNumber[1]); break;
            case MAIN_PLAYER2_STOP_ACCELERATION:    PLAYER_STOP_ACCELERATION(sPlayerNumber[1]); break;
            case MAIN_PLAYER2_STOP_BACKWARDS:       PLAYER_STOP_BACKWARDS(sPlayerNumber[1]); break;
            case MAIN_PLAYER2_TURN_LEFT:            PLAYER_TURN_LEFT(sPlayerNumber[1]); break;
            case MAIN_PLAYER2_TURN_RIGHT:           PLAYER_TURN_RIGHT(sPlayerNumber[1]); break;
            case MAIN_PLAYER2_STOP_LEFT:            PLAYER_STOP_LEFT(sPlayerNumber[1]); break;
            case MAIN_PLAYER2_STOP_RIGHT:           PLAYER_STOP_RIGHT(sPlayerNumber[1]); break;
            case MAIN_PLAYER2_USE_ROCKET:           PLAYER_USE_ROCKET(sPlayerNumber[1]); break;
        }
    }
    
    /**
     * Exits the game without any warning. 
     */
    private static void GAME_EXIT() {
        Game.getInstance().endGame();
    }

    /**
     * Pauses the game.
     */
    private static void GAME_PAUSE() {
        Game.getInstance().setGameState(Game.GameState.IN_GAME_PAUSED);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //                            PLAYER STEARING                             //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Player uses a rocket. 
     * @param i player number
     */
    private static void PLAYER_USE_ROCKET (int i) {
        if (i == NO_PLAYER) return;
        sPlayer[i].startAction(Player.Action.USE_ROCKET);
    }
    
    /**
     * Accelerates the player.
     * @param i player number
     */
    private static void PLAYER_ACCELERATION (int i) {
        if (i == NO_PLAYER) return;
        if (sPlayerAccelerate[i]) return;
        sPlayerAccelerate[i] = true;
        sPlayer[i].startAction(Player.Action.START_ACCELERATION);
    }

    /**
     * Backward-accelerates the player.
     * @param i player number
     */
    private static void PLAYER_BACKWARDS (int i) {
        if (i == NO_PLAYER) return;
        if (sPlayerAccBackwards[i]) return;
        sPlayerAccBackwards[i] = true;
        sPlayer[i].startAction(Player.Action.START_ACCELERATION_BACKWARD);
    }

    /**
     * Stops acceleration of the main player.
     * @param i player number
     */
    private static void PLAYER_STOP_ACCELERATION (int i) {
        if (i == NO_PLAYER) return;
        sPlayerAccelerate[i] = false;
        if (!sPlayerAccBackwards[i])
            sPlayer[i].startAction(Player.Action.STOP_ACCELERATION);
    }

    /**
     * Stops backward-acceleration of the player.
     * @param i player number
     */
    private static void PLAYER_STOP_BACKWARDS (int i) {
        if (i == NO_PLAYER) return;
        sPlayerAccBackwards[i] = false;
        if (!sPlayerAccelerate[i])
            sPlayer[i].startAction(Player.Action.STOP_ACCELERATION);
    }
    
    /**
     * Turns the player's vehicle left. 
     * @param i player number
     */
    private static void PLAYER_TURN_LEFT (int i) {
        if (i == NO_PLAYER) return;
        if (sPlayerTurnLeft[i]) return;
        sPlayerTurnLeft[i] = true;
        sPlayer[i].startAction(Player.Action.START_ROTATION_LEFT);
    }

    /**
     * Turns the player's vehicle right. 
     * @param i player number
     */
    private static void PLAYER_TURN_RIGHT (int i) {
        if (i == NO_PLAYER) return;
        if (sPlayerTurnRight[i]) return;
        sPlayerTurnRight[i] = true;
        sPlayer[i].startAction(Player.Action.START_ROTATION_RIGHT);
    }    
    
    /**
     * Stops turning left of the player.
     * @param i player number
     */
    private static void PLAYER_STOP_LEFT (int i) {
        if (i == NO_PLAYER) return;
        sPlayerTurnLeft[i] = false;
        if (!sPlayerTurnRight[i])
            sPlayer[i].startAction(Player.Action.STOP_ROTATION);
    }

    /**
     * Stops turning right of the player.
     * @param i player number
     */
    private static void PLAYER_STOP_RIGHT (int i) {
        if (i == NO_PLAYER) return;
        sPlayerTurnRight[i] = false;
        if (!sPlayerTurnLeft[i])
            sPlayer[i].startAction(Player.Action.STOP_ROTATION);
    }    
          
}
