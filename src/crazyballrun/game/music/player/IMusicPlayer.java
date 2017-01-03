/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game.music.player;

/**
 * The IMusicPlayer encapsulates all necessary functions of a music player in
 * the game. 
 * @author Timm Hoffmeister
 */
public interface IMusicPlayer {

    /**
     * Start playing the player's song.
     */
    public void play();

    /**
     * Stop playing the player's song.
     */
    public void stop(); 
    
}
