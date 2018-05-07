package logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class NRPLogger
{
    private static final Logger LOGGER = Logger.getLogger(NRPLogger.class.getName());
    private static FileHandler fileHandler = null;

    public static void init()
    {
        try {
            fileHandler = new FileHandler( "src/logger/NRP.log", false );

            fileHandler.setFormatter( new SimpleFormatter() );

            LOGGER.addHandler( fileHandler );
            LOGGER.setLevel( Level.ALL );
            
        } catch ( SecurityException | IOException e ) {
            e.printStackTrace();
        }
    }
}
