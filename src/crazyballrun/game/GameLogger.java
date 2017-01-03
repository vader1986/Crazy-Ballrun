/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun.game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Writes logging-messages into a log-file.
 * @author Timm Hoffmeister
 */
public class GameLogger {
    
    /**
     * Operating instance for writing the logfile.
     */
    private static BufferedWriter sFileWriter = null;

    /**
     * Defines if the GameLogger is initilized (created the sFileWriter).
     */
    private static boolean sInitialized = false;
    
    /**
     * Closes the log-file.
     */
    public synchronized static void close () {
        if (!sInitialized) return;
        try {
            sFileWriter.close();
        } catch (IOException ex) {
            System.err.println("Error while closing the Logfile: " + GameConstants.LOG_FILE_PATH);
        }
    }

    /**
     * Writes a log-entry for a caught exception into the logfile.
     * @param e exception
     */
    public synchronized static void log (Exception e) {

        // Exception is null
        if (e == null) {
            log("Error while logging exception: exception is null.");
            return;
        }
        
        // Log exception message
        log("Exception caught: " + e.getMessage() + " - " + e.getLocalizedMessage());

        // Log exception stack-trace
        StackTraceElement vStackElement = null;
        if (e.getStackTrace() != null) 
        {
            for (int i = 0; i < e.getStackTrace().length; i++)
            {
                vStackElement = e.getStackTrace()[i];
                if (vStackElement != null)  
                {
                    log("    " + vStackElement.getClassName() + "."
                        + vStackElement.getMethodName() + " [" 
                        + vStackElement.getLineNumber() + "]");
                }
            }
        }
    }    
    
    /**
     * Writes a log-entry into the logfile.
     * @param entry log entry
     */
    public synchronized static void log (String entry) {
        
        // initialize the logger
        if (!sInitialized) {
            try {
                
                // remote old logfile
                File vTmpFile = new File (GameConstants.LOG_FILE_PATH);
                if (vTmpFile.exists()) vTmpFile.delete();

                // create new logfile
                sFileWriter = new BufferedWriter(new FileWriter(new File(GameConstants.LOG_FILE_PATH), true));
                sInitialized = true;
                log("LOG [" + (new Date()).toString() + "]");
                
            } catch (IOException ex) {
                System.err.println("Path to the logfile doesn't exist. ");
                System.err.println("Please correct the path in GameConstants.LOG_FILE_PATH. ");
            }
        }
        
        // write the log-entry into the log-file
        if (sInitialized) {
            try {
                sFileWriter.write(entry);
                sFileWriter.newLine();
                sFileWriter.flush();
            } catch (IOException ex) {
                System.err.println("Error while writing into the Logfile: " + GameConstants.LOG_FILE_PATH);
            }
        }
    }
    
}
