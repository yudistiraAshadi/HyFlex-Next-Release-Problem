/*  
    Adapted from ExampleRun1.java (chesc.jar @ http://www.asap.cs.nott.ac.uk/external/chesc2011/hyflex_download.html)
*/

package hyperheuristic.run;

import be.kuleuven.kahosl.acceptance.AcceptanceCriterionType;
import be.kuleuven.kahosl.hyperheuristic.GIHH;
import be.kuleuven.kahosl.selection.SelectionMethodType;
import nrp.NRP;
import nrp.logger.NRPLogger;
import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;

/**
 * This class show hows to run GIHH
 */
public class RunGIHH
{

    public static void main( String[] args )
    {

        long seed = 1234;
        long totalExecutionTime = 5000;

        int totalInstances = 2;
        int totalRuns = 2;

        String hyperHeuristicName = "GIHH";

        SelectionMethodType selectionType
                = SelectionMethodType.AdaptiveLimitedLAassistedDHSMentorSTD;
        AcceptanceCriterionType acceptanceType
                = AcceptanceCriterionType.AdaptiveIterationLimitedListBasedTA;

        String resultFileName = "GIHH_";

        for ( int instance = 1; instance <= totalInstances; instance++ ) {
            for ( int run = 1; run <= totalRuns; run++ ) {

                // create a ProblemDomain object with a seed for the random number generator
                ProblemDomain problem = new NRP( seed * run );

                // creates an HyperHeuristic object with a seed for the random number generator
                HyperHeuristic hyper_heuristic_object
                        = new GIHH( seed * run, problem.getNumberOfHeuristics(), totalExecutionTime,
                                resultFileName, selectionType, acceptanceType );

                // we must load an instance within the problem domain, in this case we choose
                // instance 2
                problem.loadInstance( instance );

                // we must set the time limit for the hyper-heuristic in milliseconds
                hyper_heuristic_object.setTimeLimit( totalExecutionTime );

                // a key step is to assign the ProblemDomain object to the HyperHeuristic
                // object.
                // However, this should be done after the instance has been loaded, and after
                // the time limit has been set
                hyper_heuristic_object.loadProblemDomain( problem );

                // now that all of the parameters have been loaded, the run method can be
                // called.
                // this method starts the timer, and then calls the solve() method of the
                // hyper_heuristic_object.
                NRPLogger.logStart( hyperHeuristicName, instance, run, totalExecutionTime );
                hyper_heuristic_object.run();

                // obtain the best solution found within the time limit
                double bestSolutionValue = 0 - hyper_heuristic_object.getBestSolutionValue();
                System.out.println( "\n\n BEST SLN FOUND: " + bestSolutionValue );

                NRPLogger.logFinish( bestSolutionValue );
            }
        }
    }
}
