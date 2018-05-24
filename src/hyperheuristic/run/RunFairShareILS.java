package hyperheuristic.run;

/*
 * author: Steven Adriaensen
 * date: 22/01/2014
 * contact: steven.adriaensen@vub.ac.be
 * affiliation: Vrije Universiteit Brussel
 */

import java.util.Date;

import nrp.NRP;
import nrp.logger.NRPLogger;
import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import hyperheuristic.FairShareILS;


public class RunFairShareILS {

    /**
     * @param args
     */
    public static void main(String[] args) {
        long seed = new Date().getTime();
        long totalExecutionTime = 10000;
        int instanceId = 1;
        String hyperHeuristicName = "FairShareILS";
        
        //algorithm used (FS-ILS with default parameter settings)
        HyperHeuristic algo = new FairShareILS(seed);
        
        //benchmark instance solved (4th instance in the Maximum Satisfiability problem domain)
        ProblemDomain problem = new NRP(seed);
        problem.loadInstance(instanceId);
        
        //time we're allowed to optimize
        //long t_allowed = 10000;
        algo.setTimeLimit(totalExecutionTime);

        algo.loadProblemDomain(problem);
        
        //start optimizing
//        System.out.println("Testing "+algo+" for "+totalExecutionTime+" ms on "+problem.getClass().getSimpleName()+"["+instanceId+"]...");
        NRPLogger.logStart( hyperHeuristicName, instanceId, totalExecutionTime );
        algo.run();

        //print out quality of best solution found
        double bestSolutionValue = 1.0 / algo.getBestSolutionValue();
        System.out.println("Best Solution Value: " + bestSolutionValue);
        NRPLogger.logFinish( bestSolutionValue );
    }

}