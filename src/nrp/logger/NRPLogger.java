package nrp.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    /*
     * Logger
     */
    private final static Logger LOGGER = Logger.getLogger( NRPLogger.class.getName() );
    private final static Path logFilePath = Paths.get( "nrp.log" );

    /*
     * Results directory and filenames
     */
    private final static Path nrpResultsParentDirectory = Paths.get( "nrp-results" );
    private final static String heuristicLogsFilename = "heuristicLogs.csv";
    private final static String bestSolutionFoundFilename = "bestSolutionFound.csv";

    /*
     * Basic information
     */
    private static long startTime;
    private static int applyHeuristicIterationCounter;
    private static String hyperHeuristicName;
    private static int instanceId;

    private static ThreadMXBean thread = ManagementFactory.getThreadMXBean();
    
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
            fileHandler = new FileHandler( logFilePathString, true );

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
        startTime = thread.getCurrentThreadCpuTime();
        applyHeuristicIterationCounter = 0;

        NRPLogger.hyperHeuristicName = hyperHeuristicName;
        NRPLogger.instanceId = instanceId;

        System.out.println( "Started: Hyper-heuristic [ " + hyperHeuristicName + " ], instanceId [ "
                + instanceId + " ], time limit [ " + timeLimit + " ms ]" );

        LOGGER.info( "Hyper-heuristic [ " + hyperHeuristicName + " ], instanceId [ " + instanceId
                + " ], time limit [ " + timeLimit + " ms ]\n***" );
    }

    /**
     * Function to log on ProblemDomain.applyHeuristic() call
     * 
     * @param heuristicNumber
     */
    public static void logApplyHeuristic( int heuristicNumber )
    {
        applyHeuristicIterationCounter += 1;
        long timeElapsed = NRPLogger.getTimeElapsedSinceRun();

        HeuristicLog heuristicLog
                = new HeuristicLog( applyHeuristicIterationCounter, timeElapsed, heuristicNumber );

        heuristicLogList.add( heuristicLog );
    }

    /**
     * Function to log when the best solution is found
     * 
     * @param heuristicNumber
     * @param solutionValue
     */
    public static void logBestSolutionFound( int heuristicNumber, double solutionValue )
    {
        applyHeuristicIterationCounter += 1;
        long timeElapsed = NRPLogger.getTimeElapsedSinceRun();

        HeuristicLog heuristicLog
                = new HeuristicLog( applyHeuristicIterationCounter, timeElapsed, heuristicNumber );
        heuristicLogList.add( heuristicLog );

        long minute = TimeUnit.NANOSECONDS.toMinutes( timeElapsed );
        long second = TimeUnit.NANOSECONDS.toSeconds( timeElapsed )
                - TimeUnit.MINUTES.toSeconds( minute );
        long millis = TimeUnit.NANOSECONDS.toMillis( timeElapsed ) - TimeUnit.MINUTES.toMillis( minute )
                - TimeUnit.SECONDS.toMillis( second );

        String time = String.format( "%d min : %02d.%d sec", minute, second, millis );

        LOGGER.info( "Time elapsed: [" + time + "] - Iteration #" + applyHeuristicIterationCounter
                + " - Best sln value: " + solutionValue + " - Heuristic #" + heuristicNumber );

        BestSolutionFoundLog bestSolutionFoundLog = new BestSolutionFoundLog(
                applyHeuristicIterationCounter, timeElapsed, heuristicNumber, solutionValue );
        bestSolutionFoundLogList.add( bestSolutionFoundLog );
    }

    /**
     * Function to log at the end of the HyperHeuristic.run()
     * 
     * @param bestSolutionValue
     * @throws IOException
     */
    public static void logFinish( double bestSolutionValue )
    {
        int randomDeletionAndFirstAddingCounter = 0;
        int deleteHighestCostAddLowestCostCounter = 0;
        int deleteLowestProfitAddHighestProfitCounter = 0;
        int deleteLowestProfitCostRatioAddHighestProfitCostRatioCounter = 0;

        try {

            /*
             * Initialize the parent directory
             */
            if ( !Files.exists( nrpResultsParentDirectory ) ) {
                Files.createDirectories( nrpResultsParentDirectory );
            }

            /*
             * Initialize new directory for current run
             */
            String currentDate = LocalDateTime.now()
                    .format( DateTimeFormatter.ofPattern( "yyyy-MM-dd HH_mm_ss" ) );
            String currentResultDirectoryName
                    = "[" + currentDate + "]-" + hyperHeuristicName + "-instance#" + instanceId;
            Path currentRunResultDirectory
                    = Paths.get( nrpResultsParentDirectory.toAbsolutePath().normalize().toString(),
                            currentResultDirectoryName );

            Files.createDirectories( currentRunResultDirectory );

            /*
             * Initialize the output files
             */
            FileWriter heuristicLogsWriter = new FileWriter(
                    currentRunResultDirectory.toAbsolutePath().normalize().toString() + "/"
                            + heuristicLogsFilename );
            FileWriter bestSolutionFoundWriter = new FileWriter(
                    currentRunResultDirectory.toAbsolutePath().normalize().toString() + "/"
                            + bestSolutionFoundFilename );

            /*
             * Add the CSV files header
             */
            String[] heuristicLogsHeader = { "iterationNumber", "timeElapsed", "heuristicNumber" };
            CSVUtils.writeLine( heuristicLogsWriter, Arrays.asList( heuristicLogsHeader ) );

            String[] bestSolutionFoundHeader
                    = { "iterationNumber", "timeFound", "heuristicNumber", "solutionValue" };
            CSVUtils.writeLine( bestSolutionFoundWriter, Arrays.asList( bestSolutionFoundHeader ) );

            /*
             * Iterate through heuristicLog and generate CSV file
             */
            for ( HeuristicLog heuristicLog : heuristicLogList ) {

                int logApplyHeuristicIterationCounter
                        = heuristicLog.getLogApplyHeuristicIterationCounter();
                int heuristicNumber = heuristicLog.getHeuristicNumber();
                long timeElapsed = heuristicLog.getTimeElapsed();

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

                /*
                 * Write to CSV
                 */
                String[] heuristicLogString
                        = { Integer.toString( logApplyHeuristicIterationCounter ),
                                Long.toString( timeElapsed ), Integer.toString( heuristicNumber ) };
                CSVUtils.writeLine( heuristicLogsWriter, Arrays.asList( heuristicLogString ) );
            }

            /*
             * Iterate through bestSolutionFoundLogList and generate CSV file
             */
            for ( BestSolutionFoundLog bestSolutionFoundLog : bestSolutionFoundLogList ) {

                int logApplyHeuristicIterationCounter
                        = bestSolutionFoundLog.getLogApplyHeuristicIterationCounter();
                long timeFound = bestSolutionFoundLog.getTimeFound();
                int heuristicNumber = bestSolutionFoundLog.getHeuristicNumber();
                double solutionValue = bestSolutionFoundLog.getSolutionValue();

                /*
                 * Write to CSV
                 */
                String[] bestSolutionFoundLogString
                        = { Integer.toString( logApplyHeuristicIterationCounter ),
                                Long.toString( timeFound ), Integer.toString( heuristicNumber ),
                                Double.toString( solutionValue ) };
                CSVUtils.writeLine( bestSolutionFoundWriter,
                        Arrays.asList( bestSolutionFoundLogString ) );
            }

            heuristicLogsWriter.flush();
            bestSolutionFoundWriter.flush();

            heuristicLogsWriter.close();
            bestSolutionFoundWriter.close();

        } catch ( IOException e ) {
            e.printStackTrace();
        }

        LOGGER.info( "Best solution value: " + bestSolutionValue );
        LOGGER.info( "Total heuristic called: " + applyHeuristicIterationCounter + " times" );

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

        // Finishing Line
        LOGGER.info(
                "\n--------------------------------------------------------------------------------------------------------\n" );
    }

    /**
     * Calculate and return the time elapsed since HyperHeuristic.run() function is
     * being called
     * 
     * @return timeElapsed
     */
    private static long getTimeElapsedSinceRun()
    {
        long timeElapsed = thread.getCurrentThreadCpuTime() - startTime;
        assert ( startTime != 0 );

        return timeElapsed;
    }
}