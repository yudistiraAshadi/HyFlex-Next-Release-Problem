package nrp.logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import nrp.util.CSVUtils;

public class NRPLogger
{
    private final static Logger LOGGER = Logger.getLogger( NRPLogger.class.getName() );

    private final static Path logFilePath = Paths.get( "nrp.log" );
    private final static Path csvDirectory = Paths.get( "csv" );
    private final static Path csvFilePath = Paths.get( "csv/test.csv" );

    private static long startTime = 0;

    private static List< BestSolutionFoundLog > bestSolutionFoundLogList = new ArrayList<>();
    private static List< HeuristicLog > heuristicLogList = new ArrayList<>();

    /**
     * Initialize the logger file
     */
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

    /**
     * Function to log at the beginning, should be called before
     * HyperHeuristic.run()
     * 
     * @param hyperHeuristicName
     * @param timeLimit
     */
    public static void logStart( String hyperHeuristicName, int instanceId, long timeLimit )
    {
        startTime = System.nanoTime();

        System.out.println( "Started: Hyper-heuristic [ " + hyperHeuristicName + " ], instanceId [ "
                + instanceId + " ], time limit [ " + timeLimit + " ms ]" );
        LOGGER.info( "Hyper-heuristic [ " + hyperHeuristicName + " ], instanceId [ " + instanceId
                + " ], time limit [ " + timeLimit + " ms ]" );
    }

    /**
     * Function to log on ProblemDomain.initialiseSolution() call
     * 
     * @param instanceId
     * @param costLimit
     */
    public static void logInitialise( int instanceId, double costLimit )
    {
        LOGGER.info( "Initialize first solution - Instance ID: " + instanceId + ", Cost limit: "
                + costLimit );
    }

    /**
     * Function to log on ProblemDomain.applyHeuristic() call
     * 
     * @param heuristicNumber
     */
    public static void logApplyHeuristic( int heuristicNumber )
    {
        long timeElapsed = System.nanoTime() - startTime;
        assert startTime != 0;

        HeuristicLog heuristicLog = new HeuristicLog( timeElapsed, heuristicNumber );

        heuristicLogList.add( heuristicLog );
    }

    /**
     * Function to log when the best solution is found
     * 
     * @param heuristicNumber
     * @param solutionValue
     */
    public static void bestSolutionValue( int heuristicNumber, double solutionValue )
    {
        long currentTime = System.nanoTime();
        long timeElapsed = currentTime - startTime;
        assert startTime != 0;

        BestSolutionFoundLog bestSolution
                = new BestSolutionFoundLog( currentTime, heuristicNumber, solutionValue );

        long minute = TimeUnit.NANOSECONDS.toMinutes( timeElapsed );
        long second = TimeUnit.NANOSECONDS.toSeconds( timeElapsed - ( minute * 60 * ( 10 ^ 9 ) ) );
        long millis = TimeUnit.NANOSECONDS
                .toMillis( timeElapsed - ( minute * 60 * ( 10 ^ 9 ) ) - ( second * ( 10 ^ 9 ) ) );

        String time = String.format( "[%02d min : %02d.%d sec]", minute, second, millis );

        LOGGER.info( "Time elapsed: " + time + " - Best sln value: " + solutionValue
                + " - Heuristic #" + heuristicNumber );

        bestSolutionFoundLogList.add( bestSolution );
    }

    /**
     * Function to log at the end of the HyperHeuristic.run()
     * 
     * @param bestSolutionValue
     * @throws IOException
     */
    public static void logFinish( double bestSolutionValue )
    {
        int applyHeuristicCounter = 0;
        int randomDeletionAndFirstAddingCounter = 0;
        int deleteHighestCostAddLowestCostCounter = 0;
        int deleteLowestProfitAddHighestProfitCounter = 0;
        int deleteLowestProfitCostRatioAddHighestProfitCostRatioCounter = 0;

        /*
         * Initialize the CSV directory and file
         */
        if ( !Files.exists( csvDirectory ) ) {
            try {
                Files.createDirectories( csvDirectory );
            } catch ( IOException e ) {
                e.printStackTrace();
                ;
            }
        }

        if ( !Files.exists( csvFilePath ) ) {
            try {
                Files.createFile( csvFilePath );
            } catch ( IOException e ) {
                e.printStackTrace();
                ;
            }
        } else {
            try {
                Files.write( csvFilePath, new byte[ 0 ], StandardOpenOption.TRUNCATE_EXISTING );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        /*
         * Add the CSV file header
         */
        String[] csvFileHeader = { "heuristicNumber", "timeElapsed" };
        try {
            CSVUtils.writeLine( csvFilePath, Arrays.asList( csvFileHeader ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        /*
         * Calculate total called and generate CSV file
         */
        for ( HeuristicLog heuristicLog : heuristicLogList ) {

            int heuristicNumber = heuristicLog.getHeuristicNumber();
            long timeElapsed = heuristicLog.getTimeElapsed();

            /*
             * Write to CSV
             */
            String[] heuristicLogString
                    = { Integer.toString( heuristicNumber ), Long.toString( timeElapsed ) };
            try {
                CSVUtils.writeLine( csvFilePath, Arrays.asList( heuristicLogString ) );
            } catch ( IOException e ) {
                e.printStackTrace();
            }

            applyHeuristicCounter += 1;

            switch ( heuristicNumber ) {
                case 0:
                    randomDeletionAndFirstAddingCounter += 1;
                    break;
                case 1:
                    deleteHighestCostAddLowestCostCounter += 1;
                    break;
                case 2:
                    deleteLowestProfitAddHighestProfitCounter += 1;
                    break;
                case 3:
                    deleteLowestProfitCostRatioAddHighestProfitCostRatioCounter += 1;
                    break;
                default:
                    System.err.println( "heuristic does not exist" );
                    System.exit( -1 );
            }
        }

        LOGGER.info( "Best solution value: " + bestSolutionValue );
        LOGGER.info( "Total heuristic called: " + applyHeuristicCounter + " times" );

        // Heuristic one
        LOGGER.info( "Heuristic #1 - Called: " + randomDeletionAndFirstAddingCounter + " times" );

        // Heuristic two
        LOGGER.info( "Heuristic #2 - Called: " + deleteHighestCostAddLowestCostCounter + " times" );

        // Heuristic three
        LOGGER.info(
                "Heuristic #3 - Called: " + deleteLowestProfitAddHighestProfitCounter + " times" );

        // Heuristic four
        LOGGER.info( "Heuristic #4 - Called: "
                + deleteLowestProfitCostRatioAddHighestProfitCostRatioCounter + " times" );
    }
}