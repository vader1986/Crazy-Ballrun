/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.utilities;

import crazyballrun.game.GameConstants;

/**
 * The Vector2D class is a utility for translation and rotation of position 
 * coordinates.
 * @author Timm Hoffmeister
 */
public class Vector2D {

    /**
     * Size of one texture in pixels. 
     */
    private final static int sTextureSize = GameConstants.TEXTURE_SIZE;
    
    /**
     * Vector coordinates.
     */
    public double x, y;

    /**
     * Simple constructor for Vector2D which initializes the vector coordinates
     * with "0". 
     */
    public Vector2D () {
        x = y = 0;
    }
   
    /**
     * Constructor of class Vector2D. Copies its data from the parameter-vector.
     * @param copy vector-instance to copy
     */
    public Vector2D (Vector2D copy) {
        x = copy.x;
        y = copy.y;
    }

    /**
     * Constructor of class Vector2D. Initializes the data with the given 
     * parameters. 
     * @param x x-coordinate of the vector
     * @param y y-coordinate of the vector
     */
    public Vector2D (double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Constructor of class Vector2D. Initializes the data with the given 
     * coordinates (pixel-coordinates) and transforms them into texture-coordinates.
     * @param x x-coordinate of the vector
     * @param y y-coordinate of the vector
     */
    public Vector2D (int x, int y) {
        this.x = (double)x / (double)sTextureSize;
        this.y = (double)y / (double)sTextureSize;
    }
    
    /**
     * Adds a vector to this vector's data. 
     * @param v reference to the vector to add
     */
    public void add (Vector2D v) {
        this.x += v.x;
        this.y += v.y;
    }

    /**
     * Adds a vector to this vector. 
     * @param x x-coordinate of the vector to add
     * @param y y-coordinate of the vector to add
     */
    public void add (double x, double y) {
        this.x += x;
        this.y += y;
    }
    
    /**
     * Substracts a vector from this vector. 
     * @param v reference to the vector to substract
     */
    public void sub (Vector2D v) {
        this.x -= v.x;
        this.y -= v.y;
    }

    /**
     * Substracts a vector from this vector. 
     * @param x x-coordinate of the vector to substract
     * @param y y-coordinate of the vector to substract
     */
    public void sub (double x, double y) {
        this.x -= x;
        this.y -= y;
    }

    /**
     * Calculates the squared distance between two vectors. Sometimes used for
     * performance reasons instead of the normal distance.
     * @param v other vector
     * @return distance between two vectors
     */
    public double squaredDistance (Vector2D v) {
        return ( (x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) );
    }
    
    /**
     * Calculates the distance between two vectors. 
     * @param v other vector
     * @return distance between two vectors
     */
    public double distance (Vector2D v) {
        return Math.sqrt( (x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) );
    }
    
    /**
     * Calculates the length of the vector.
     * @return the length of the vector.
     */
    public double length () {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Multiplys vector y with a and then adds it to this vector. 
     * @param a constant multiplication factor
     * @param y vector to add after multiplication
     */
    public void axpy (double a, Vector2D y) {
        this.x += a * y.x;
        this.y += a * y.y;
    }
    
    /**
     * Multiplies the vector with a given constant. 
     * @param c constant to multiply the vector with
     */
    public void multiply (double c) {
        x *= c;
        y *= c;
    }

    /**
     * Normalizes the vector (-> length of 1). 
     */
    public void normalize () {
        double l = length();
        multiply(1.0 / l);
    }
    
    /**
     * Calculates the dot product of this vector and v.
     * @param v the vector to multiply
     * @return the result of the dot product between this vector and the argument.
     */
    public double dot (Vector2D v) {
        return (x * v.x + y * v.y);
    }
    
    /**
     * Rounds all data values x and y down to zero which fullfill following
     * condition: x² < epsilon or y² < epsilon.
     * @param epsilon cut-off limit
     */
    public void cut (double epsilon) {
        if (x * x < epsilon) x = 0.0;
        if (y * y < epsilon) y = 0.0;
    }

    /**
     * Rotates the vector around the point 0/0. 
     * @param phi angle of rotation
     */
    public void rotate (double phi) {
        double x_old = x, y_old = y;
        x = x_old * Math.cos(phi) - y_old * Math.sin(phi); 
        y = x_old * Math.sin(phi) + y_old * Math.cos(phi);      
    }
    
    /**
     * Rotates the vector around a given rotation point. 
     * @param phi angle of rotation
     * @param point rotation point
     */
    public void rotate (double phi, Vector2D point) {
        double x_old = x - point.x, y_old = y - point.y;
        x = x_old * Math.cos(phi) - y_old * Math.sin(phi); 
        y = x_old * Math.sin(phi) + y_old * Math.cos(phi);      
        this.add(point);
    }
    
    /**
     * Rotates the vector around a given rotation point. 
     * @param phi angle of rotation
     * @param x x-coordinate of the rotation point
     * @param y y-coordinate of the rotation point
     */
    public void rotate (double phi, double x, double y) {
        double x_old = this.x - x, y_old = this.y - y;
        this.x = x_old * Math.cos(phi) - y_old * Math.sin(phi); 
        this.y = x_old * Math.sin(phi) + y_old * Math.cos(phi);      
        this.add(x, y);
    }
     
    /**
     * Calculates the product of the x-component and the number of pixels one
     * texture consists of and converts the result into an integer value.
     * @return the integer value of the product of x and texture-size. 
     */
    public long getX () {
        return (long)(x * sTextureSize);
    }

    /**
     * Calculates the product of the y-component and the number of pixels one
     * texture consists of and converts the result into an integer value.
     * @return the integer value of the product of y and texture-size. 
     */
    public long getY () {
        return (long)(y * sTextureSize);
    }
    
    @Override
    public String toString() {
        return ("[" + x + "," + y + "]");
    }
    
}
