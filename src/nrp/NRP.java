package nrp;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import AbstractClasses.ProblemDomain;

public class NRP extends ProblemDomain
{
    private NRPInstance nrpInstance;
    private NRPSolution[] nrpSolutions = new NRPSolution[ 2 ];
    private NRPSolution bestSolution;

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
            case 2:
                this.deleteSmallestProfitAddBiggestProfit( solutionSourceIndex,
                        solutionDestinationIndex );
                break;
            case 3:
                this.deleteLowestProfitCostRatioAddHighestProfitCostRatio( solutionSourceIndex,
                        solutionDestinationIndex );
            default:
                this.deleteBiggestCostAddSmallestCost( solutionSourceIndex,
                        solutionDestinationIndex );
                break;
        }

        ++this.heuristicCallRecord[ heuristicID ];
        this.heuristicCallTimeRecord[ heuristicID ]
                = (int) ( (long) this.heuristicCallTimeRecord[ heuristicID ]
                        + ( System.currentTimeMillis() - startTime ) );

        /*
         * Verify best solution or not
         */
        this.verifyBestSolution( this.nrpSolutions[ solutionDestinationIndex ] );

        return this.nrpSolutions[ solutionDestinationIndex ].getTotalProfit();
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
            case 2:
                this.deleteSmallestProfitAddBiggestProfit( solutionSourceIndex1,
                        solutionDestinationIndex );
                break;
            case 3:
                this.deleteLowestProfitCostRatioAddHighestProfitCostRatio( solutionSourceIndex1,
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

        /*
         * Verify best solution or not
         */
        this.verifyBestSolution( this.nrpSolutions[ solutionDestinationIndex ] );

        return this.nrpSolutions[ solutionDestinationIndex ].getTotalProfit();
    }

    /**
     * If the currentSolution is the best solution, save it
     * 
     * @param currentSolution
     */
    private void verifyBestSolution( NRPSolution currentSolution )
    {
        if ( currentSolution.getTotalProfit() > this.bestSolution.getTotalProfit() ) {
            this.bestSolution = new NRPSolution( currentSolution );

            System.out.println( this.getBestSolutionValue() );
        }
    }

    /**
     * Heuristic #1 Randomly delete an accepted customer and add all customer while
     * the cost is sufficient
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

    /**
     * Heuristic #2 Delete a customer with the biggest cost and add customer(s) with
     * sufficient fund, start from the smallest cost customers
     */
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
                        return Double.compare( cust1.getOriginalCost(), cust2.getOriginalCost() );
                    }

                } );
        customersSetFromLowestToBiggestCost.addAll( haveNotBeenAcceptedCustomers );

        /*
         * Add all customer if cost is sufficient, start from the lowest cost
         */
        double costLimit = this.nrpInstance.getCostLimit();
        for ( Customer customer : customersSetFromLowestToBiggestCost ) {
            if ( currentSolution.isSafeAddingACustomer( customer, costLimit )
                    && ( customer.getId() != theBiggestCostCustomerId ) ) {
                currentSolution.addAnAcceptedCustomer( customer );
            }
        }

        this.nrpSolutions[ targetIndex ] = new NRPSolution( currentSolution );
    }

    /**
     * Heuristic #3 Delete an acceptedCustomer with the smallest profit and add
     * customer(s) with sufficient fund with the biggest profit
     */
    private void deleteSmallestProfitAddBiggestProfit( int sourceIndex, int targetIndex )
    {
        NRPSolution currentSolution = new NRPSolution( this.nrpSolutions[ sourceIndex ] );
        Set< Customer > customersSet = currentSolution.getAcceptedCustomers();

        /*
         * Find the smallest profit customer
         */
        double smallestProfit = Double.POSITIVE_INFINITY;
        int theSmallestProfitCustomerId = 0;
        for ( Customer customer : customersSet ) {
            double customerProfit = customer.getProfit();

            if ( customerProfit < smallestProfit ) {
                smallestProfit = customerProfit;
                theSmallestProfitCustomerId = customer.getId();
            }
        }

        /*
         * Delete the smallest cost customer from the set
         */
        for ( Customer customer : customersSet ) {
            if ( customer.getId() == theSmallestProfitCustomerId ) {
                currentSolution.removeAnAcceptedCustomer( customer );
                break;
            }
        }

        /*
         * Reorder the haveNotBeenAcceptedCustomers set
         */
        Set< Customer > haveNotBeenAcceptedCustomers
                = currentSolution.getHaveNotBeenAcceptedCustomers();
        Set< Customer > customersSetFromBiggestToLowestProfit
                = new TreeSet< Customer >( new Comparator< Customer >() {

                    @Override
                    public int compare( Customer cust1, Customer cust2 )
                    {
                        return Double.compare( cust2.getProfit(), cust1.getProfit() );
                    }

                } );
        customersSetFromBiggestToLowestProfit.addAll( haveNotBeenAcceptedCustomers );

        /*
         * Add all customer if cost is sufficient, start from the lowest cost
         */
        double costLimit = this.nrpInstance.getCostLimit();
        for ( Customer customer : customersSetFromBiggestToLowestProfit ) {
            if ( currentSolution.isSafeAddingACustomer( customer, costLimit )
                    && ( customer.getId() != theSmallestProfitCustomerId ) ) {
                currentSolution.addAnAcceptedCustomer( customer );
            }
        }

        this.nrpSolutions[ targetIndex ] = new NRPSolution( currentSolution );
    }

    /**
     * Heuristic #4 Delete an acceptedCustomer with the smallest profit/cost ratio
     * and add customer(s) with sufficient fund with the biggest profit/cost ratio
     */
    private void deleteLowestProfitCostRatioAddHighestProfitCostRatio( int sourceIndex,
            int targetIndex )
    {
        NRPSolution currentSolution = new NRPSolution( this.nrpSolutions[ sourceIndex ] );
        Set< Customer > customersSet = currentSolution.getAcceptedCustomers();

        /*
         * Find the smallest profit/cost ratio customer
         */
        double smallestProfitCostRatio = Double.POSITIVE_INFINITY;
        int theSmallestProfitCostRatioCustomerId = 0;
        for ( Customer customer : customersSet ) {
            double customerProfitCostRatio = customer.getProfit() / customer.getOriginalCost();

            if ( customerProfitCostRatio < smallestProfitCostRatio ) {
                smallestProfitCostRatio = customerProfitCostRatio;
                theSmallestProfitCostRatioCustomerId = customer.getId();
            }
        }

        /*
         * Delete the smallest cost customer from the set
         */
        for ( Customer customer : customersSet ) {
            if ( customer.getId() == theSmallestProfitCostRatioCustomerId ) {
                currentSolution.removeAnAcceptedCustomer( customer );
                break;
            }
        }

        /*
         * Reorder the haveNotBeenAcceptedCustomers set
         */
        Set< Customer > haveNotBeenAcceptedCustomers
                = currentSolution.getHaveNotBeenAcceptedCustomers();
        Set< Customer > customersSetFromBiggestToLowestProfitCostRatio
                = new TreeSet< Customer >( new Comparator< Customer >() {

                    @Override
                    public int compare( Customer cust1, Customer cust2 )
                    {

                        return Double.compare( cust2.getProfit() / cust2.getOriginalCost(),
                                cust1.getProfit() / cust1.getOriginalCost() );
                    }

                } );
        customersSetFromBiggestToLowestProfitCostRatio.addAll( haveNotBeenAcceptedCustomers );

        /*
         * Add all customer if cost is sufficient, start from the lowest cost
         */
        double costLimit = this.nrpInstance.getCostLimit();
        for ( Customer customer : customersSetFromBiggestToLowestProfitCostRatio ) {
            if ( currentSolution.isSafeAddingACustomer( customer, costLimit )
                    && ( customer.getId() != theSmallestProfitCostRatioCustomerId ) ) {
                currentSolution.addAnAcceptedCustomer( customer );
            }
        }

        this.nrpSolutions[ targetIndex ] = new NRPSolution( currentSolution );
    }

    @Override
    public String bestSolutionToString()
    {
        return "Biggest Profit = " + this.bestSolution.getTotalProfit();
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
        return this.bestSolution.getTotalProfit();
    }

    @Override
    public double getFunctionValue( int solutionIndex )
    {
        return this.nrpSolutions[ solutionIndex ].getTotalProfit();
    }

    @Override
    public int[] getHeuristicsOfType( HeuristicType heuristicType )
    {
        if ( heuristicType == ProblemDomain.HeuristicType.MUTATION ) {

            return new int[] { 0, 1, 2, 3 };
        }
        if ( heuristicType == ProblemDomain.HeuristicType.RUIN_RECREATE ) {
            return new int[ 0 ];
        }
        if ( heuristicType == ProblemDomain.HeuristicType.LOCAL_SEARCH ) {
            return new int[ 0 ];
        }
        if ( heuristicType == ProblemDomain.HeuristicType.CROSSOVER ) {
            return new int[ 0 ];
        }

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
        return 4;
    }

    @Override
    public int getNumberOfInstances()
    {
        return 5;
    }

    @Override
    public void initialiseSolution( int solutionIndex )
    {
        NRPSolution initialSolution = new NRPSolution( this.nrpInstance.getCustomersSet() );
        double costLimit = this.nrpInstance.getCostLimit();
        System.out.println( "Cost Limit: " + costLimit );

        Set< Customer > haveNotBeenAcceptedCustomers
                = initialSolution.getHaveNotBeenAcceptedCustomers();
        Iterator< Customer > customersIterator = haveNotBeenAcceptedCustomers.iterator();

        if ( customersIterator.hasNext() ) {
            Customer customer = customersIterator.next();
            initialSolution.addAnAcceptedCustomer( customer );

            while ( customersIterator.hasNext() ) {
                customer = customersIterator.next();
                if ( initialSolution.isSafeAddingACustomer( customer, costLimit ) ) {
                    initialSolution.addAnAcceptedCustomer( customer );
                }
            }
        }

        this.nrpSolutions[ solutionIndex ] = new NRPSolution( initialSolution );
        this.bestSolution = new NRPSolution( initialSolution );
    }

    @Override
    public void loadInstance( int index )
    {
        this.nrpInstance = new NRPInstance( index );
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
