package nrp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import AbstractClasses.ProblemDomain;
import AbstractClasses.ProblemDomain.HeuristicType;

public class NRP extends ProblemDomain
{
    private NRPInstance nrpInstance;
    private NRPSolution[] nrpSolutions = new NRPSolution[ 2 ];
    private double biggestProfit = 0;

    public NRP( long seed )
    {
        super( seed );
    }

    @Override
    public double applyHeuristic( int heuristicID, int solutionSourceIndex,
            int solutionDestinationIndex )
    {
        long startTime = System.currentTimeMillis();

        switch ( heuristicID ) {
            case 0:
                this.maximumRatioGRASP( solutionSourceIndex, solutionDestinationIndex );
                break;
            default:
                System.err.println( "heuristic does not exist" );
                System.exit( -1 );
        }

        ++this.heuristicCallRecord[ heuristicID ];
        this.heuristicCallTimeRecord[ heuristicID ]
                = (int) ( (long) this.heuristicCallTimeRecord[ heuristicID ]
                        + ( System.currentTimeMillis() - startTime ) );

        double currentTotalProfit = this.nrpSolutions[ solutionDestinationIndex ].getTotalCost();
        this.verifyBiggestProfit( currentTotalProfit );

        return currentTotalProfit;
    }

    @Override
    public double applyHeuristic( int heuristicID, int solutionSourceIndex1,
            int solutionSourceIndex2, int solutionDestinationIndex )
    {
        long startTime = System.currentTimeMillis();

        switch ( heuristicID ) {
            case 0:
                this.maximumRatioGRASP( solutionSourceIndex1, solutionDestinationIndex );
                break;
            default:
                System.err.println( "heuristic does not exist" );
                System.exit( -1 );
        }

        ++this.heuristicCallRecord[ heuristicID ];
        this.heuristicCallTimeRecord[ heuristicID ]
                = (int) ( (long) this.heuristicCallTimeRecord[ heuristicID ]
                        + ( System.currentTimeMillis() - startTime ) );

        double currentTotalProfit = this.nrpSolutions[ solutionDestinationIndex ].getTotalCost();
        this.verifyBiggestProfit( currentTotalProfit );

        return currentTotalProfit;
    }

    private void verifyBiggestProfit( double currentTotalProfit )
    {
        if ( currentTotalProfit > this.biggestProfit ) {
            this.biggestProfit = currentTotalProfit;
            System.out.println( this.biggestProfit );
        }
    }

    private void maximumRatioGRASP( int sourceIndex, int targetIndex )
    {
        Map< Integer, Customer > customerMap = new HashMap<>( this.nrpInstance.getCustomerMap() );
        Map< Integer, Enhancement > enhancementMap
                = new HashMap<>( this.nrpInstance.getEnhancementMap() );

        Set< Enhancement > allEnhancements = new HashSet<>( enhancementMap.values() );
        Set< Customer > allCustomers = new HashSet<>( customerMap.values() );
        Set< Customer > acceptedCustomers = new HashSet<>();
        Set< Enhancement > acceptedEnhancements = new HashSet<>();

        // Get cost limit
        int costLimit = 0;
        for ( Enhancement enhancement : allEnhancements ) {
            costLimit += enhancement.getCost();
        }

        for ( int currentCost = 0;; ) {
            // get set of customers that haven't been accepted
            Set< Customer > remainingCustomers = new HashSet<>( allCustomers );
            remainingCustomers.removeAll( acceptedCustomers );

            // update customer's profitEnhancementsCostRatio
            for ( Customer customer : remainingCustomers ) {

                double profit = customer.getProfit();

                // count the cost of each customer
                Set< Enhancement > enhancementSet = customer.getEnhancementsSet();
                enhancementSet.removeAll( acceptedEnhancements );
                int customerCost = 0;
                for ( Enhancement enhancement : enhancementSet ) {
                    customerCost += enhancement.getCost();
                }

                double profitEnhancementsCostRatio = profit / customerCost;
                customer.setProfitEnhancementsCostRatio( profitEnhancementsCostRatio );
            }

            // sort in descending order
            List< Customer > customerList = new ArrayList<>( remainingCustomers );
            customerList.sort( ( a, b ) -> Double.compare( b.getProfitEnhancementsCostRatio(),
                    a.getProfitEnhancementsCostRatio() ) );

            // do GRASP
            int randomNumber = this.rng.nextInt( 10 );
            Customer acceptedCustomer = customerMap.get( randomNumber );

            acceptedCustomers.add( acceptedCustomer );
            acceptedEnhancements.addAll( acceptedCustomer.getEnhancementSet() );

            // count current cost
            int totalCostAccepted = 0;
            for ( Enhancement enhancement : acceptedEnhancements ) {
                totalCostAccepted += enhancement.getCost();
            }

            currentCost = totalCostAccepted;
        }
    }

    @Override
    public String bestSolutionToString()
    {
        return "Biggest Profit = " + this.biggestProfit;
    }

    @Override
    public boolean compareSolutions( int solutionIndex1, int solutionIndex2 )
    {
        Set< Customer > solution1 = this.nrpSolutions[ solutionIndex1 ].getAcceptedCustomers();
        Set< Customer > solution2 = this.nrpSolutions[ solutionIndex2 ].getAcceptedCustomers();

        return solution1.equals( solution2 );
    }

    @Override
    public void copySolution( int solutionSourceIndex, int solutionDestinationIndex )
    {
        this.nrpSolutions[ solutionDestinationIndex ]
                = new NRPSolution( this.nrpSolutions[ solutionSourceIndex ] );
    }

    @Override
    public double getBestSolutionValue()
    {
        return this.biggestProfit;
    }

    @Override
    public double getFunctionValue( int solutionIndex )
    {
        return this.nrpSolutions[ solutionIndex ].getTotalProfit();
    }

    @Override
    public int[] getHeuristicsOfType( HeuristicType arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int[] getHeuristicsThatUseDepthOfSearch()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int[] getHeuristicsThatUseIntensityOfMutation()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getNumberOfHeuristics()
    {
        return 3;
    }

    @Override
    public int getNumberOfInstances()
    {
        return 1;
    }

    @Override
    public void initialiseSolution( int solutionIndex )
    {
        int[] initialSolution = new int[ this.instance.getNumberOfCities() ];

        for ( int i = 0; i < initialSolution.length; i++ ) {
            initialSolution[ i ] = i;
        }

        double totalDistance = this.instance.getTotalDistance( initialSolution );
        this.solutionMemory[ solutionIndex ] = new TSPSolution( initialSolution, totalDistance );
        this.verifyShortestDistance( totalDistance );
    }

    @Override
    public void loadInstance( int index )
    {
        this.nrpInstance = new NRPInstance();
    }

    @Override
    public void setMemorySize( int size )
    {
        NRPSolution[] newSolutionMemory = new NRPSolution[ size ];

        if ( this.nrpSolutions != null ) {
            for ( int i = 0; i < this.nrpSolutions.length; ++i ) {
                if ( i < size ) {
                    newSolutionMemory[ i ] = this.nrpSolutions[ i ];
                }
            }
        }

        this.nrpSolutions = newSolutionMemory;
    }

    @Override
    public String solutionToString( int solutionIndex )
    {
        return this.nrpSolutions[ solutionIndex ].toString();
    }

    @Override
    public String toString()
    {
        return this.nrpInstance.toString();
    }

}
