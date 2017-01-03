/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.utilities;

/**
 * Rectangle3D defines a rectangle on a particular layer (not over several layers!)
 * in a Crazy Ballrun level. 
 * 
 * @author Timm Hoffmeister
 */
public class Rectangle3D 
{
    /**
     * X-Coordinate of the position of the rectangle.
     */
    public double x;
    
    /**
     * Y-Coordinate of the position of the rectangle.
     */
    public double y;
    
    /**
     * Width of the rectangle.
     */
    public double width;
    
    /**
     * Height of the rectangle.
     */
    public double height;
    
    /**
     * Layer of the rectangle. 
     */
    public int layer; 
    
    /**
     * Creates an instance of Rectangle3D. 
     * @param x position of the rectangle
     * @param y position of the rectangle
     * @param width width of the rectangle
     * @param height height of the rectangle
     * @param layer layer of the rectangle
     */
    public Rectangle3D (double x, double y, double width, double height, int layer)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layer = layer;
    }

    /**
     * Checks if a point is inside of the rectangle. 
     * @param x point position
     * @param y point position
     * @param layer layer of the point
     * @return 'true' if point is inside of the rectangle
     */
    public boolean inside (double x, double y, int layer)
    {
        return (layer == this.layer &&
                x <= this.x + this.width &&
                y <= this.y + this.height &&
                x >= this.x && y >= this.y);
    }

    /**
     * Checks if a point is inside of the rectangle. 
     * @param p point position
     * @param layer layer of the point
     * @return 'true' if point is inside of the rectangle
     */
    public boolean inside (Vector2D p, int layer)
    {
        return (layer == this.layer &&
                p.x <= this.x + this.width &&
                p.y <= this.y + this.height &&
                p.x >= this.x && p.y >= this.y);
    }
}
