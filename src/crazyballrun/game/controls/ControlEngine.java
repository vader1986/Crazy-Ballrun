/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.controls;

import crazyballrun.game.Game;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The ControlEngine is initialized by the game, which fills the EventMapper 
 * with data according to the game configuration file and gives a list of
 * IControl-instances to the engine. For other classes the control engine offers 
 * an interface for even-registration and -deregistration.
 * 
 * @author Timm Hoffmeister
 * @see EventMapper
 */
public class ControlEngine 
{   
    /**
     * Reference to the ControlEngine.
     */
    private static ControlEngine engine = null;

    /**
     * Mapping of String-IDs to IControls (for event registration). 
     */
    private HashMap<String, IControl> mControls = null;

    /**
     * Reference to the event-action-mapper. 
     */
    private EventMapper mEventMapper = null;
    
    /**
     * Gives access to the control engine of the game. 
     * @return a reference to the control engine. 
     */
    public static ControlEngine getInstance() 
    {
        if (engine == null)
            engine = new ControlEngine();
        return engine;
    }

    /**
     * Constructor of ControlEngine.
     */
    private ControlEngine () 
    {
        mControls = new HashMap<String, IControl>();
    }

    /**
     * Initializes the ControlEngine by filling the control-hashmap and the 
     * EventMapper. 
     * @param eventMapper reference to the filled EventMapper
     * @param controls list of all IControl-implementations
     */
    public void initialize(EventMapper eventMapper, ArrayList<IControl> controls) 
    {        
        // Fill IControl-Hashmap
        for (IControl vControl : controls) {
            mControls.put(vControl.getId(), vControl);
        }

        // EventMapper
        mEventMapper = eventMapper;
    }

    /**
     * Registers a listener for a particular event of a control.
     * @param listener reference to the listener
     * @param control ID of the control
     * @param event name of the event
     */
    public void register (IListener listener, String control, String event) 
    {
        mControls.get(control).addListener(listener, event);
    }
    
    /**
     * De-registers a listener from a control for a particular event. 
     * @param listener reference to the listener
     * @param control ID of the control
     * @param event name of the event
     */
    public void deregister (IListener listener, String control, String event) 
    {
        mControls.get(control).removeListener(listener, event);
    }
    
    /**
     * Notifies the ControlEngine of an incoming event of a particular control.
     * The ControlEngine passes the control- and event-name to the EventMapper
     * which forces an action for the event.
     * @param control ID of the control
     * @param event name of the event
     * @param content reference to the event's content
     */
    public void notify (String control, String event, EventContent content) 
    {
        if (Game.getInstance().getGameState() == Game.GameState.IN_GAME)
        {
           mEventMapper.map(event, content);
        }
    }
}
