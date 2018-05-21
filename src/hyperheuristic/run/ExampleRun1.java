package hyperheuristic.run;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import hyperheuristic.ExampleHyperHeuristic1;
import nrp.NRP;
import nrp.logger.NRPLogger;

/**
 * This class shows how to run a selected hyper-heuristic on a selected problem
 * domain. It shows the minimum that must be done to test a hyper heuristic on a
 * problem domain, and it is intended to be read before the ExampleRun2 class,
 * which provides an example of a more complex set-up
 */
public class ExampleRun1
{

    public static void main( String[] args )
    {
        int instanceId = 5;
        long timeLimit = 10000;
        String hyperHeuristicName = "ExampleHyperHeuristic1";

        // create a ProblemDomain object with a seed for the random number generator
        ProblemDomain problem = new NRP( 1234 );

        // creates an ExampleHyperHeuristic object with a seed for the random number
        // generator
        HyperHeuristic hyper_heuristic_object = new ExampleHyperHeuristic1( 5678 );

        // we must load an instance within the problem domain, in this case we choose
        // instance 1
        problem.loadInstance( instanceId );

        // we must set the time limit for the hyper-heuristic in milliseconds, in this
        // example we set the time limit to 30 seconds
        hyper_heuristic_object.setTimeLimit( timeLimit );

        // a key step is to assign the ProblemDomain object to the HyperHeuristic
        // object.
        // However, this should be done after the instance has been loaded, and after
        // the time limit has been set
        hyper_heuristic_object.loadProblemDomain( problem );

        // now that all of the parameters have been loaded, the run method can be
        // called.
        // this method starts the timer, and then calls the solve() method of the
        // hyper_heuristic_object.
        NRPLogger.logStart( hyperHeuristicName, instanceId, timeLimit );
        hyper_heuristic_object.run();

        // obtain the best solution found within the time limit
        double bestSolutionValue = 1.0 / hyper_heuristic_object.getBestSolutionValue();
        System.out.println( "The best solution value: " + bestSolutionValue );

        NRPLogger.logFinish( bestSolutionValue );
    }
}