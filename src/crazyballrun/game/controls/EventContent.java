/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.controls;

/**
 * The EventContent class contains all necessary information concerning a control
 * event (e.g. x- and y-value for mouse click coordinates). 
 * 
 * @author Timm Hoffmeister
 */
public class EventContent {

    /**
     * Pixel-coordinates on the screen.
     */
    public int x, y;

    /**
     * Creates an instance of EventContent.
     * @param x x-coordinate of the event's target pixel
     * @param y y-coordinate of the event's target pixel
     */
    public EventContent (int x, int y) {
        this.x = x;
        this.y = y;
    }
    
}
