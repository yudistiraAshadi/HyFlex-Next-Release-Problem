package hyperheuristic.run;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import hyperheuristic.EPH;
import nrp.NRP;
import nrp.logger.NRPLogger;

public class RunEPH
{
    public static void main( String[] args )
    {
        long seed = 1234;
        int instanceId = 1;
        long timeLimit = 10000;
        String hyperHeuristicName = "DavidChescEPH";

        // create a ProblemDomain object with a seed for the random number generator
        ProblemDomain problem = new NRP( seed );

        // creates an EPH object with a seed for the random number
        // generator
        HyperHeuristic hyper_heuristic_object = new EPH( seed );

        // we must load an instance within the problem domain
        problem.loadInstance( instanceId );

        // we must set the time limit for the hyper-heuristic in milliseconds
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
