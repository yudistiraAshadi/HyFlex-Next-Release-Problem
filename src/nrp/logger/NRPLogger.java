package nrp.logger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class NRPLogger
{
    private final static Logger LOGGER = Logger.getLogger( NRPLogger.class.getName() );

    private static int applyHeuristicCounter = 0;
    private static int randomDeletionAndFirstAddingCounter = 0;
    private static int deleteBiggestCostAddSmallestCostCounter = 0;

    public static void init()
    {
        Handler fileHandler = null;
        Handler consoleHandler = null;
        try {
            fileHandler = new FileHandler( "src/nrp/logger/NRP.log", false );
            consoleHandler = new ConsoleHandler();

            /*
             * Use simple log format
             */
            fileHandler.setFormatter( new SimpleFormatter() );

            /*
             * Reset the Root Log
             */
            LogManager.getLogManager().reset();

            LOGGER.addHandler( fileHandler );
            LOGGER.removeHandler( consoleHandler );
            LOGGER.setLevel( Level.ALL );

        } catch ( SecurityException | IOException e ) {
            e.printStackTrace();
        }
    }

    public static void logApplyHeuristic()
    {
        applyHeuristicCounter += 1;
        LOGGER.info( "applyHeuristic calls number #" + applyHeuristicCounter );
    }

    public static void logRandomDeletionAndFirstAdding( long timeElapsed )
    {
        randomDeletionAndFirstAddingCounter += 1;
        LOGGER.info( "randomDeletionAndFirstAdding calls number #"
                + randomDeletionAndFirstAddingCounter + ", time elapsed: " + timeElapsed + " ms" );
    }

    public static void logdeleteBiggestCostAddSmallestCost( long timeElapsed )
    {
        deleteBiggestCostAddSmallestCostCounter += 1;
        LOGGER.info( "deleteBiggestCostAddSmallestCost calls number #"
                + deleteBiggestCostAddSmallestCostCounter + ", time elapsed: " + timeElapsed
                + " ms" );

    }
}
