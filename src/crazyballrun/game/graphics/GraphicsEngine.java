/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.graphics;

import crazyballrun.game.GameConstants;
import crazyballrun.game.GameLogger;
import crazyballrun.game.level.Level;
import java.awt.Frame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 * The GraphicsEngine is used for rendering 2D graphics. It also provides 
 * functionality to load and store different image-filetypes. 
 * @author Timm Hoffmeister
 */
public class GraphicsEngine {
    
    /**
     * Reference to the graphics engine. 
     */
    private static GraphicsEngine engine = null;

    /**
     * Reference to the rendering-thread. 
     */
    private RenderThread mRenderer = null; 

    /**
     * Reference to the Window-Frame.
     */
    private Frame mFrame = null;
    
    /**
     * Hashmap containing all images from the graphics resource folder (e.g.
     * standard background image, load screen, ...). 
     */
    private HashMap <String, BufferedImage> mGraphics = null;
    
    /**
     * Hashmap containing all texture images of the textures-resource folder.
     * For each texture there has to be two files: "[texture].png" and 
     * "[texture]_coll.png" for the collision data. 
     */
    private HashMap <String, BufferedImage> mTextures = null;
    
    /**
     * Provides access to the graphics engine.
     * @return a reference to the GraphicsEngine. 
     */
    public static GraphicsEngine getInstance() 
    {
        if (engine == null)
            engine = new GraphicsEngine();
        return engine;
    }
    
    /**
     * Private constructor for GraphicsEngine. 
     */
    private GraphicsEngine() 
    {
        mFrame = new Frame();
        mGraphics = new HashMap<String, BufferedImage>();
        mTextures = new HashMap<String, BufferedImage>();
    }
    
    /**
     * Defines the current state of the ample (-1 means no ample, 0 means red
     * is active, 1 means red and yellow are active, 2 just yellow, 3 yellow and
     * green and 4 just green).
     * @param index ample index (state of the ample)
     */
    public void setAmpleState (int index)
    {
        mRenderer.setAmple(index);
    }
    
    /**
     * Gives a reference of the current level to the RenderThread.
     * @param level level-reference
     */
    public void setLevel (Level level) 
    {
        mRenderer.setCurrentLevel(level);
    }
    
    /**
     * Returns the main frame for rendering/event-listening.
     * @return the main frame.
     */
    public Frame getFrame() 
    {
        return mFrame;
    }
   
    /**
     * Loads one image from a file. 
     * @param path absolute file path
     * @return the reference to the Image-instance.
     */
    public static BufferedImage sLoadImage (String path) 
    {
        BufferedImage vReturn = null;
        try 
        {
            File file = new File(path);
            vReturn = ImageIO.read(file);
        }
        catch (Exception e) 
        {
            vReturn = null;
        }
        return vReturn;
    }
    
    /**
     * Loads all images of the given path into a hashmap <filename, image>. 
     * @param path folder-path containg the image-files
     * @param output hashmap for the output
     * @param recursive load images of the subfolders recursively
     * @param topFolder name of the top directory 
     */
    private static void sLoadImages (String path, HashMap<String, BufferedImage> output, boolean recursive, String topFolder) 
    {
        try 
        {
            // get relative path with "/" as seperator
            String relativeSubFolder = path.substring(topFolder.length());
            String splitter = File.separator;
            if (File.separator.equals("\\"))
                splitter = File.separator + File.separator;
            String [] subpath = relativeSubFolder.split(splitter);
            String index = "";
            if (subpath != null)
                for (int i = 0; i < subpath.length; i++)
                    index += (i == 0) ? subpath[i] : (subpath[i] + "/") ;
            if (index.equals("/")) index = "";
            
            // go through files and subfolders
            File folder = new File(path);
            BufferedImage vImage = null;
            for (File file : folder.listFiles()) 
            {
                if (file.isFile())
                    vImage = ImageIO.read(file);
                else
                {
                    if (recursive)
                        sLoadImages(file.getAbsolutePath(), output, true, topFolder);
                    vImage = null;
                }
                if (vImage != null) 
                {
                    output.put(index + file.getName(), vImage);
                }
            }
        }
        catch (Exception e) 
        {
            GameLogger.log(e);
        }
    }
    
    /**
     * Loads all images of the given path into a hashmap <filename, image>. 
     * @param path folder-path containg the image-files
     * @param output hashmap for the output
     * @param recursive load images of the subfolders recursively
     */
    public static void sLoadImages (String path, HashMap<String, BufferedImage> output, boolean recursive) 
    {
        sLoadImages(path, output, recursive, path);
    }
    
    /**
     * Initializes the graphics engine. 
     */
    public void initialize() 
    {
        try 
        {
            // Load graphics
            GraphicsEngine.sLoadImages(GameConstants.GRAPHICS_RESOURCE_PATH, mGraphics, true);
            
            // Load textures
            GraphicsEngine.sLoadImages(GameConstants.TEXTURE_RESOURCE_PATH, mTextures, true);
            
        }
        catch (Exception e) 
        {
            GameLogger.log(e);
        }
    }
    
    /**
     * Creates and runs the rendering thread.
     */
    public void start()
    {
        mRenderer = new RenderThread(mFrame);
        mRenderer.start();
    }

    /**
     * Ends all threads the GraphicsEngine controls. 
     * @throws InterruptedException interrupting the RenderThread might lead to errors 
     */
    public void end() throws InterruptedException
    {
        mRenderer.doExit();
        mRenderer.join();
    }
    
    /**
     * Forces the RenderThread to paint the current level's cameras' contents in
     * addition to the current GUI-Frame (which might be an in-game-menu for 
     * example).
     * This method does not continue rendering if currently paused.
     */
    public void renderGame () {
        mRenderer.switchState(true);
    }
    
    /**
     * Forces the RenderThread to paint the current GUI-Frame and stop rendering
     * the level's cameras.
     * This method does not continue rendering if currently paused.
     */
    public void renderGUI () {
        mRenderer.switchState(false);
    }
    
    /**
     * Re-Initilizes the RenderThread, e.g. after changing screen resolution. 
     */
    public void renderUpdate()
    {
        try 
        {
            renderPause();
            mRenderer.init();
            renderContinue();
        }
        catch (Exception e) 
        {
            GameLogger.log(e);
        }
    }
    
    /**
     * Pauses the rendering thread. 
     */
    public void renderPause()
    {
        try 
        {
            mRenderer.doPause();
        }
        catch (Exception e) 
        {
            GameLogger.log(e);
        }
    }
    
    /**
     * Continues rendering after a renderPause()-call. 
     */
    public void renderContinue()
    {
        try
        {
            mRenderer.doContinue();
        }
        catch (Exception e) 
        {
            GameLogger.log(e);
        }
    }

    /**
     * Copies an image of the image-hashtable.
     * @param filename name of the image-file (not path)
     * @return a reference to the Image-instance.
     */
    public Image getImageCopy (String filename) 
    {
        return sLoadImage(GameConstants.GRAPHICS_RESOURCE_PATH + File.separator + filename);
    }
    
    /**
     * Gets an image-reference from the hashmap. 
     * @param filename name of the image file
     * @return a reference to the Image-object or null if not existing.
     */
    public BufferedImage getGraphics(String filename) 
    {
        return mGraphics.get(filename);
    }
    
    /**
     * Searches for an image in the local level folder first. If not found 
     * there it starts searching in the global directory.
     * @param filename image filename
     * @param levelname level name
     * @return instance of the Image
     */
    public BufferedImage getGraphics(String filename, String levelname) 
    {
        BufferedImage vReturn = null;

        vReturn = getGraphics(filename);
        
        if (vReturn == null) 
        {
            try 
            {
                vReturn = sLoadImage(GameConstants.LEVEL_RESOURCE_PATH + 
                        File.separator + levelname + File.separator + filename);
                mGraphics.put(filename, vReturn);
            }
            catch (Exception e)
            {
                vReturn = null;
            }
        }
        return vReturn;
    }

    /**
     * Loads an animation.
     * @param filename root-filename of the animation
     * @param levelname level name
     * @param size number of pictures used for the animation
     * @param frequence change frequency of the animation (in miliseconds)
     * @return reference to the animation
     */
    public Animation getAnimation (String filename, String levelname, int size, int frequence) 
    {
        // load image file for texture animation
        BufferedImage [] vImages = new BufferedImage[size];
        if (size > 1)
        {
            for (int i = 0; i < size; i++) {
                vImages[i] = (BufferedImage) getGraphics(filename + i + GameConstants.IMAGE_FILE_EXTENSION, levelname);
                if (vImages[i] == null) return null;
            }
        }
        else
        {
            vImages[0] = (BufferedImage) getGraphics(filename + GameConstants.IMAGE_FILE_EXTENSION, levelname);
            if (vImages[0] == null) return null;
        }
        
        // Create and return animation
        Animation vReturn = new Animation(vImages, -1);
        vReturn.setFrequency(frequence);
        return vReturn;
    }
    
    /**
     * Gets an image-reference from the textures-hashmap.
     * @param filename name of the texture-file
     * @return a reference to the Image-object or null if not existing.
     */
    public BufferedImage getTexture(String filename) 
    {
        return mTextures.get(filename);
    }
    
    /**
     * Searches for a texture in the local level folder first. If not found 
     * there it starts searching in the global directory.
     * @param filename texture filename
     * @param levelname level name
     * @return instance of the texture-Image
     */
    public BufferedImage getTexture(String filename, String levelname)
    {
        BufferedImage vReturn = null;
        
        vReturn = getTexture(filename);
        
        if (vReturn == null) 
        {
            try
            {
                vReturn = sLoadImage(GameConstants.LEVEL_RESOURCE_PATH + 
                    File.separator + levelname + File.separator + filename);
                mTextures.put(filename, vReturn);
            }
            catch (Exception e)
            {
                GameLogger.log(e);
            }
        }
        return vReturn;
    }

    /**
     * Loads a texture animation.
     * @param filename root-filename of the texture
     * @param levelname level name
     * @param size number of pictures used for the animation
     * @param frequence change frequency of the animation (in miliseconds)
     * @return reference to the animation
     */
    public Animation getTextureAnimation (String filename, String levelname, int size, int frequence) 
    {
        // load image file for texture animation
        BufferedImage [] vImages = new BufferedImage[size];
        if (size > 1)
        {
            for (int i = 0; i < size; i++) {
                vImages[i] = (BufferedImage) getTexture(filename + i + GameConstants.TEXTURE_FILE_EXTENSION, levelname);
                if (vImages[i] == null)
                    return null;
            }
        }
        else
        {
            vImages[0] = (BufferedImage) getTexture(filename + GameConstants.TEXTURE_FILE_EXTENSION, levelname);
            if (vImages[0] == null) return null;
        }
        
        // Create and return animation
        Animation vReturn = new Animation(vImages, -1);
        vReturn.setFrequency(frequence);
        return vReturn;
    }
    
}
