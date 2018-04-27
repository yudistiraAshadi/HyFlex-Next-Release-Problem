package nrp;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import AbstractClasses.ProblemDomain;
import AbstractClasses.ProblemDomain.HeuristicType;

public class NRP extends ProblemDomain
{
    private NRPInstance nrpInstance;
    private NRPSolution[] nrpSolutions = new NRPSolution[ 2 ];
    private double biggestProfit;

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
                this.randomDeletionAndFirstAdding( solutionSourceIndex, solutionDestinationIndex );
                break;
            case 1:
                this.deleteBiggestCostAddSmallestCost( solutionSourceIndex,
                        solutionDestinationIndex );
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
                this.randomDeletionAndFirstAdding( solutionSourceIndex1, solutionDestinationIndex );
                break;
            case 1:
                this.deleteBiggestCostAddSmallestCost( solutionSourceIndex1,
                        solutionDestinationIndex );
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

    /**
     * Randomly delete an accepted customer and add all customer while the cost is
     * sufficient
     */
    private void randomDeletionAndFirstAdding( int sourceIndex, int targetIndex )
    {
        NRPSolution currentSolution = new NRPSolution( this.nrpSolutions[ sourceIndex ] );
        Set< Customer > customersSet = currentSolution.getAcceptedCustomers();

        /*
         * Select a random number between 0 ~ totalAcceptedCustomers
         */
        int totalAcceptedCustomers = customersSet.size();
        int randomNumber = this.rng.nextInt( totalAcceptedCustomers );

        /*
         * Remove the selected entry
         */
        Iterator< Customer > customersIterator = customersSet.iterator();
        for ( int i = 0; i < randomNumber; i++ ) {
            customersIterator.next();
        }
        Customer removedCustomer = customersIterator.next();
        currentSolution.removeAnAcceptedCustomer( removedCustomer );

        /*
         * Add all customer if it is safe to add
         */
        double costLimit = this.nrpInstance.getCostLimit();
        for ( Customer customer : currentSolution.getHaveNotBeenAcceptedCustomers() ) {
            if ( currentSolution.isSafeAddingACustomer( customer, costLimit ) ) {
                currentSolution.addAnAcceptedCustomer( customer );
            }
        }

        this.nrpSolutions[ targetIndex ] = new NRPSolution( currentSolution );
    }

    private void deleteBiggestCostAddSmallestCost( int sourceIndex, int targetIndex )
    {
        NRPSolution currentSolution = new NRPSolution( this.nrpSolutions[ sourceIndex ] );
        Set< Customer > customersSet = currentSolution.getAcceptedCustomers();

        /*
         * Find the biggest cost customer
         */
        double biggestCost = 0.0;
        int theBiggestCostCustomerId = 0;
        for ( Customer customer : customersSet ) {
            double customerCost = customer.getOriginalCost();

            if ( customerCost > biggestCost ) {
                biggestCost = customerCost;
                theBiggestCostCustomerId = customer.getId();
            }
        }

        /*
         * Delete the biggest cost customer from the set
         */
        for ( Customer customer : customersSet ) {
            if ( customer.getId() == theBiggestCostCustomerId ) {
                currentSolution.removeAnAcceptedCustomer( customer );
                break;
            }
        }

        /*
         * Reorder the haveNotBeenAcceptedCustomers set
         */
        Set< Customer > haveNotBeenAcceptedCustomers
                = currentSolution.getHaveNotBeenAcceptedCustomers();
        Set< Customer > customersSetFromLowestToBiggestCost
                = new TreeSet< Customer >( new Comparator< Customer >() {

                    @Override
                    public int compare( Customer cust1, Customer cust2 )
                    {
                        return Double.compare( cust2.getOriginalCost(), cust1.getOriginalCost() );
                    }

                } );
        customersSetFromLowestToBiggestCost.addAll( haveNotBeenAcceptedCustomers );

        /*
         * Add all customer if cost is sufficient, start from the lowest cost
         */
        double costLimit = this.nrpInstance.getCostLimit();
        for ( Customer customer : customersSetFromLowestToBiggestCost ) {
            if ( currentSolution.isSafeAddingACustomer( customer, costLimit ) ) {
                currentSolution.addAnAcceptedCustomer( customer );
            } else {
                break;
            }
        }

        this.nrpSolutions[ targetIndex ] = new NRPSolution( currentSolution );
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
        return 2;
    }

    @Override
    public int getNumberOfInstances()
    {
        return 1;
    }

    @Override
    public void initialiseSolution( int solutionIndex )
    {
        NRPSolution initialSolution = new NRPSolution( this.nrpInstance.getCustomersSet() );
        double costLimit = this.nrpInstance.getCostLimit();
        System.out.println( costLimit );

        Set<Customer> haveNotBeenAcceptedCustomers = initialSolution.getHaveNotBeenAcceptedCustomers();
        Iterator< Customer > customersIterator = haveNotBeenAcceptedCustomers.iterator();

        if ( customersIterator.hasNext() ) {
            Customer customer = customersIterator.next();
            initialSolution.addAnAcceptedCustomer( customer );

            while ( customersIterator.hasNext() ) {
                customer = customersIterator.next();
                if ( initialSolution.isSafeAddingACustomer( customer, costLimit ) ) {
                    initialSolution.addAnAcceptedCustomer( customer );
                } else {
                    break;
                }
            }
        }

        this.nrpSolutions[ solutionIndex ] = new NRPSolution( initialSolution );
        // System.out.println(
        // this.nrpSolutions[solutionIndex].getAcceptedCustomers().size() );
        this.verifyBiggestProfit( initialSolution.getTotalProfit() );
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
