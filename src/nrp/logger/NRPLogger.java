package nrp.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class NRPLogger
{
    private final static Logger LOGGER = Logger.getLogger( NRPLogger.class.getName() );
    private final static Path logFilePath = Paths.get( "nrp.log" );

    private static int applyHeuristicCounter = 0;
    private static int randomDeletionAndFirstAddingCounter = 0;
    private static int deleteHighestCostAddLowestCostCounter = 0;
    private static int deleteLowestProfitAddHighestProfitCounter = 0;
    private static int deleteLowestProfitCostRatioAddHighestProfitCostRatioCounter = 0;

    public static void init()
    {
        Handler fileHandler = null;

        try {
            /*
             * Locate and initialize the logFile
             */
            String logFilePathString = logFilePath.toAbsolutePath().normalize().toString();
            File logFile = new File( logFilePathString );
            logFile.createNewFile(); // if file already exists will do nothing

            /*
             * Initialize logger handlers
             */
            LogManager.getLogManager().reset();
            fileHandler = new FileHandler( logFilePathString, false );

            /*
             * Format the logger handlers
             */
            fileHandler.setFormatter( new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] - [%2$-7s] - %3$s %n";

                @Override
                public synchronized String format( LogRecord lr )
                {
                    return String.format( format, new Date( lr.getMillis() ),
                            lr.getLevel().getLocalizedName(), lr.getMessage() );
                }
            } );

            /*
             * Add the logger handlers
             */
            LOGGER.addHandler( fileHandler );
            LOGGER.setLevel( Level.ALL );

        } catch ( SecurityException | IOException e ) {
            e.printStackTrace();
        }
    }

    public static void logApplyHeuristic()
    {
        applyHeuristicCounter += 1;
        // LOGGER.info( "applyHeuristic calls number #" + applyHeuristicCounter );
    }

    public static void logRandomDeletionAndFirstAdding( long timeElapsed )
    {
        randomDeletionAndFirstAddingCounter += 1;
        LOGGER.info( "Heuristic #1 - " + timeElapsed + " ns" );
    }

    public static void logDeleteHighestCostAddLowestCost( long timeElapsed )
    {
        deleteHighestCostAddLowestCostCounter += 1;
        LOGGER.info( "Heuristic #2 - " + timeElapsed + " ns" );
    }

    public static void logDeleteLowestProfitAddHighestProfit( long timeElapsed )
    {
        deleteLowestProfitAddHighestProfitCounter += 1;
        LOGGER.info( "Heuristic #3 - " + timeElapsed + " ns" );
    }

    public static void logDeleteLowestProfitCostRatioAddHighestProfitCostRatio( long timeElapsed )
    {
        deleteLowestProfitCostRatioAddHighestProfitCostRatioCounter += 1;
        LOGGER.info( "Heuristic #4 - " + timeElapsed + " ns" );
    }

    public static void logFinish()
    {
        long totalTimeHeuristicOne = 0;
        long minimumTimeHeuristicOne = Long.MAX_VALUE;
        long maximumTimeHeuristicOne = 0;

        long totalTimeHeuristicTwo = 0;
        long minimumTimeHeuristicTwo = Long.MAX_VALUE;
        long maximumTimeHeuristicTwo = 0;

        long totalTimeHeuristicThree = 0;
        long minimumTimeHeuristicThree = Long.MAX_VALUE;
        long maximumTimeHeuristicThree = 0;

        long totalTimeHeuristicFour = 0;
        long minimumTimeHeuristicFour = Long.MAX_VALUE;
        long maximumTimeHeuristicFour = 0;

        try ( BufferedReader br = Files.newBufferedReader( logFilePath ) ) {

            String lineOfLog;
            while ( ( lineOfLog = br.readLine() ) != null ) {

                // Split the line of log
                String[] theLogs = lineOfLog.split( "\\s-\\s" );

                // Check which heuristic
                String[] heuristic = theLogs[ 2 ].split( "#" );
                int heuristicNumber = Integer.parseInt( heuristic[ 1 ] );

                // Check the time
                String[] theTime = theLogs[ 3 ].split( "\\s+" );
                long theTimeNumber = Long.parseLong( theTime[ 0 ] );

                // Add it to total time and check whether it is the minimum or the maximum time
                switch ( heuristicNumber ) {
                    case 1:
                        totalTimeHeuristicOne += theTimeNumber;

                        if ( theTimeNumber < minimumTimeHeuristicOne ) {
                            minimumTimeHeuristicOne = theTimeNumber;
                            break;
                        }

                        if ( theTimeNumber > maximumTimeHeuristicOne ) {
                            maximumTimeHeuristicOne = theTimeNumber;
                            break;
                        }
                        break;
                    case 2:
                        totalTimeHeuristicTwo += theTimeNumber;

                        if ( theTimeNumber < minimumTimeHeuristicTwo ) {
                            minimumTimeHeuristicTwo = theTimeNumber;
                            break;
                        }

                        if ( theTimeNumber > maximumTimeHeuristicTwo ) {
                            maximumTimeHeuristicTwo = theTimeNumber;
                            break;
                        }
                        break;
                    case 3:
                        totalTimeHeuristicThree += theTimeNumber;

                        if ( theTimeNumber < minimumTimeHeuristicThree ) {
                            minimumTimeHeuristicThree = theTimeNumber;
                            break;
                        }

                        if ( theTimeNumber > maximumTimeHeuristicThree ) {
                            maximumTimeHeuristicThree = theTimeNumber;
                            break;
                        }
                        break;
                    case 4:
                        totalTimeHeuristicFour += theTimeNumber;

                        if ( theTimeNumber < minimumTimeHeuristicFour ) {
                            minimumTimeHeuristicFour = theTimeNumber;
                            break;
                        }

                        if ( theTimeNumber > maximumTimeHeuristicFour ) {
                            maximumTimeHeuristicFour = theTimeNumber;
                            break;
                        }
                        break;
                    default:
                        System.err.println( "heuristic does not exist" );
                        System.exit( -1 );
                }
            }

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        
        // Heuristic one
        LOGGER.info( "Heuristic #1 - Called: " + randomDeletionAndFirstAddingCounter + " times, Average time: "
                + totalTimeHeuristicOne / randomDeletionAndFirstAddingCounter
                + " ns, Minimum time: " + minimumTimeHeuristicOne + " ns, Maximum time: "
                + maximumTimeHeuristicOne + " ns" );

        // Heuristic two
        LOGGER.info( "Heuristic #2 - Called: " + deleteHighestCostAddLowestCostCounter + " times, Average time: "
                + totalTimeHeuristicTwo / deleteHighestCostAddLowestCostCounter
                + " ns, Minimum time: " + minimumTimeHeuristicTwo + " ns, Maximum time: "
                + maximumTimeHeuristicTwo + " ns" );

        // Heuristic three
        LOGGER.info( "Heuristic #3 - Called: " + deleteLowestProfitAddHighestProfitCounter + " times, Average time: "
                + totalTimeHeuristicThree / deleteLowestProfitAddHighestProfitCounter
                + " ns, Minimum time: " + minimumTimeHeuristicThree + " ns, Maximum time: "
                + maximumTimeHeuristicThree + " ns" );

        // Heuristic four
        LOGGER.info( "Heuristic #4 - Called: " + deleteLowestProfitCostRatioAddHighestProfitCostRatioCounter + " times, Average time: "
                + totalTimeHeuristicFour / deleteLowestProfitCostRatioAddHighestProfitCostRatioCounter
                + " ns, Minimum time: " + minimumTimeHeuristicFour + " ns, Maximum time: "
                + maximumTimeHeuristicFour + " ns" );
    }

    /**
     * @return the applyHeuristicCounter
     */
    protected static int getApplyHeuristicCounter()
    {
        return applyHeuristicCounter;
    }

    /**
     * @return the randomDeletionAndFirstAddingCounter
     */
    protected static int getRandomDeletionAndFirstAddingCounter()
    {
        return randomDeletionAndFirstAddingCounter;
    }

    /**
     * @return the deleteHighestCostAddLowestCostCounter
     */
    protected static int getDeleteHighestCostAddLowestCostCounter()
    {
        return deleteHighestCostAddLowestCostCounter;
    }

    /**
     * @return the deleteLowestProfitAddHighestProfitCounter
     */
    protected static int getDeleteLowestProfitAddHighestProfitCounter()
    {
        return deleteLowestProfitAddHighestProfitCounter;
    }

    /**
     * @return the deleteLowestProfitCostRatioAddHighestProfitCostRatioCounter
     */
    protected static int getDeleteLowestProfitCostRatioAddHighestProfitCostRatioCounter()
    {
        return deleteLowestProfitCostRatioAddHighestProfitCostRatioCounter;
    }
}
