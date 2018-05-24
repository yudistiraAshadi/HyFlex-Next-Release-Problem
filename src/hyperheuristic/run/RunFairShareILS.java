package hyperheuristic.run;

/*
 * author: Steven Adriaensen
 * date: 22/01/2014
 * contact: steven.adriaensen@vub.ac.be
 * affiliation: Vrije Universiteit Brussel
 */

import nrp.NRP;
import nrp.logger.NRPLogger;
import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import hyperheuristic.FairShareILS;

public class RunFairShareILS
{

    /**
     * @param args
     */
    public static void main( String[] args )
    {
        long seed = 1234;
        long totalExecutionTime = 5000;
        
        int totalInstances = 2;
        int totalRuns = 2;
        
        String hyperHeuristicName = "FairShareILS";
        
        for ( int instance = 1; instance <= totalInstances; instance++ ) {
            for ( int run = 1; run <= totalRuns; run++ ) {

                // algorithm used (FS-ILS with default parameter settings)
                HyperHeuristic algo = new FairShareILS( seed * run );

                // benchmark instance solved (4th instance in the Maximum Satisfiability problem
                // domain)
                ProblemDomain problem = new NRP( seed * run  );
                problem.loadInstance( instance );

                // time we're allowed to optimize
                // long t_allowed = 10000;
                algo.setTimeLimit( totalExecutionTime );

                algo.loadProblemDomain( problem );

                // start optimizing
                // System.out.println("Testing "+algo+" for "+totalExecutionTime+" ms on
                // "+problem.getClass().getSimpleName()+"["+instanceId+"]...");
                NRPLogger.logStart( hyperHeuristicName, instance, run, totalExecutionTime );
                algo.run();

                // print out quality of best solution found
                double bestSolutionValue = 0 - algo.getBestSolutionValue();
                System.out.println( "Best Solution Value: " + bestSolutionValue );
                NRPLogger.logFinish( bestSolutionValue );
            }
        }
    }

}