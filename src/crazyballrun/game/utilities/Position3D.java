/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.utilities;

/**
 * The Position3D class represents a pseudo-3d position with 2d-coordinates and
 * the layer. 
 * 
 * @author Timm Hoffmeister
 */
public class Position3D 
{
    /**
     * Current layer of the position.
     */
    public int layer = 0; 
    
    /**
     * 2D-coordinates on the current layer.
     */
    public Vector2D position = new Vector2D(); 

    /**
     * Constructor of Position3D.
     * @param position current possition vector
     * @param layer current layer
     */
    public Position3D (Vector2D position, int layer) {
        this.position = position;
        this.layer = layer;
    }

    /**
     * Constructor of Position3D.
     * @param x current position
     * @param y current position
     * @param layer current layer
     */
    public Position3D (double x, double y, int layer) {
        this.position.x = x;
        this.position.y = y;
        this.layer = layer;
    }
}
