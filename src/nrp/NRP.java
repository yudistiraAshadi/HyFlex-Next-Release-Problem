package nrp;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
                this.deleteHighestCostAddLowestCost( solutionSourceIndex,
                        solutionDestinationIndex );
                break;
            case 2:
                this.deleteLowestProfitAddHighestProfit( solutionSourceIndex,
                        solutionDestinationIndex );
                break;
            case 3:
                this.deleteLowestProfitCostRatioAddHighestProfitCostRatio( solutionSourceIndex,
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
                this.deleteHighestCostAddLowestCost( solutionSourceIndex1,
                        solutionDestinationIndex );
                break;
            case 2:
                this.deleteLowestProfitAddHighestProfit( solutionSourceIndex1,
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

            System.out.println(
                    this.getBestSolutionValue() + " -- " + currentSolution.getTotalProfit() );
        }
    }

    /**
     * Heuristic #1 Randomly delete an accepted customer and add all customer while
     * the cost is sufficient
     */
    private void randomDeletionAndFirstAdding( int sourceIndex, int targetIndex )
    {
        /*
         * Get currentSolution and customersList
         */
        NRPSolution currentSolution = new NRPSolution( this.nrpSolutions[ sourceIndex ] );
        List< Customer > acceptedCustomers = currentSolution.getAcceptedCustomers();

        /*
         * Select a random number between 0 ~ totalAcceptedCustomers
         */
        int totalAcceptedCustomers = acceptedCustomers.size();
        int randomNumber = this.rng.nextInt( totalAcceptedCustomers );

        /*
         * Remove the selected entry
         */
        Customer removedCustomer = acceptedCustomers.get( randomNumber );
        currentSolution.removeAnAcceptedCustomer( removedCustomer );

        /*
         * Randomly order the haveNotBeenAcceptedCustomers list
         */
        List< Customer > haveNotBeenAcceptedCustomers
                = currentSolution.getHaveNotBeenAcceptedCustomers();
        Collections.shuffle( haveNotBeenAcceptedCustomers );

        /*
         * Add all customer randomly if cost is sufficient
         */
        double costLimit = this.nrpInstance.getCostLimit();
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            if ( currentSolution.isSafeAddingACustomer( customer, costLimit ) ) {
                currentSolution.addAnAcceptedCustomer( customer );
            }
        }

        this.nrpSolutions[ targetIndex ] = new NRPSolution( currentSolution );
    }

    /**
     * Heuristic #2 Delete a customer with the highest cost and add customer(s) with
     * sufficient fund, start from the lowest cost customers
     */
    private void deleteHighestCostAddLowestCost( int sourceIndex, int targetIndex )
    {
        /*
         * Get currentSolution and customersList
         */
        NRPSolution currentSolution = new NRPSolution( this.nrpSolutions[ sourceIndex ] );
        List< Customer > acceptedCustomers = currentSolution.getAcceptedCustomers();

        Collections.sort( acceptedCustomers, new Comparator< Customer >() {

            @Override
            public int compare( Customer cust1, Customer cust2 )
            {
                return Double.compare( cust2.getOriginalCost(), cust1.getOriginalCost() );
            }

        } );

        /*
         * Remove the highest cost customer
         */
        Customer removedCustomer = acceptedCustomers.get( 0 );
        currentSolution.removeAnAcceptedCustomer( removedCustomer );

        /*
         * Reorder the haveNotBeenAcceptedCustomers list from lowest to highest cost
         */
        List< Customer > haveNotBeenAcceptedCustomers
                = currentSolution.getHaveNotBeenAcceptedCustomers();
        Collections.sort( haveNotBeenAcceptedCustomers, new Comparator< Customer >() {

            @Override
            public int compare( Customer cust1, Customer cust2 )
            {
                return Double.compare( cust2.getOriginalCost(), cust1.getOriginalCost() );
            }

        } );

        /*
         * Add all customer if cost is sufficient, start from the lowest cost
         */
        double costLimit = this.nrpInstance.getCostLimit();
        int removedCustomerID = removedCustomer.getId();
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            if ( currentSolution.isSafeAddingACustomer( customer, costLimit )
                    && ( customer.getId() != removedCustomerID ) ) {
                currentSolution.addAnAcceptedCustomer( customer );
            }
        }

        this.nrpSolutions[ targetIndex ] = new NRPSolution( currentSolution );
    }

    /**
     * Heuristic #3 Delete an acceptedCustomer with the lowest profit and add
     * customer(s) with sufficient fund with the highest profit
     */
    private void deleteLowestProfitAddHighestProfit( int sourceIndex, int targetIndex )
    {
        /*
         * Get currentSolution and customersList
         */
        NRPSolution currentSolution = new NRPSolution( this.nrpSolutions[ sourceIndex ] );
        List< Customer > acceptedCustomers = currentSolution.getAcceptedCustomers();

        Collections.sort( acceptedCustomers, new Comparator< Customer >() {

            @Override
            public int compare( Customer cust1, Customer cust2 )
            {
                return Double.compare( cust1.getProfit(), cust2.getProfit() );
            }

        } );

        /*
         * Remove the lowest profit customer
         */
        Customer removedCustomer = acceptedCustomers.get( 0 );
        currentSolution.removeAnAcceptedCustomer( removedCustomer );

        /*
         * Reorder the haveNotBeenAcceptedCustomers list from highest to lowest profit
         */
        List< Customer > haveNotBeenAcceptedCustomers
                = currentSolution.getHaveNotBeenAcceptedCustomers();
        Collections.sort( haveNotBeenAcceptedCustomers, new Comparator< Customer >() {

            @Override
            public int compare( Customer cust1, Customer cust2 )
            {
                return Double.compare( cust2.getProfit(), cust1.getProfit() );
            }

        } );

        /*
         * Add all customer if cost is sufficient, start from the highest profit
         */
        double costLimit = this.nrpInstance.getCostLimit();
        int removedCustomerID = removedCustomer.getId();
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            if ( currentSolution.isSafeAddingACustomer( customer, costLimit )
                    && ( customer.getId() != removedCustomerID ) ) {
                currentSolution.addAnAcceptedCustomer( customer );
            }
        }

        this.nrpSolutions[ targetIndex ] = new NRPSolution( currentSolution );
    }

    /**
     * Heuristic #4 Delete an acceptedCustomer with the lowest profit/cost ratio and
     * add customer(s) with sufficient fund with the highest profit/cost ratio
     */
    private void deleteLowestProfitCostRatioAddHighestProfitCostRatio( int sourceIndex,
            int targetIndex )
    {
        /*
         * Get currentSolution and customersList
         */
        NRPSolution currentSolution = new NRPSolution( this.nrpSolutions[ sourceIndex ] );
        List< Customer > acceptedCustomers = currentSolution.getAcceptedCustomers();

        Collections.sort( acceptedCustomers, new Comparator< Customer >() {

            @Override
            public int compare( Customer cust1, Customer cust2 )
            {
                return Double.compare( cust1.getProfit() / cust1.getOriginalCost(),
                        cust2.getProfit() / cust2.getOriginalCost() );
            }

        } );

        /*
         * Remove the lowest profit profit/cost ratio
         */
        Customer removedCustomer = acceptedCustomers.get( 0 );
        currentSolution.removeAnAcceptedCustomer( removedCustomer );

        /*
         * Reorder the haveNotBeenAcceptedCustomers from highest to lowest profit/cost
         * ratio
         */
        List< Customer > haveNotBeenAcceptedCustomers
                = currentSolution.getHaveNotBeenAcceptedCustomers();
        Collections.sort( haveNotBeenAcceptedCustomers, new Comparator< Customer >() {

            @Override
            public int compare( Customer cust1, Customer cust2 )
            {
                return Double.compare( cust2.getProfit() / cust2.getOriginalCost(),
                        cust1.getProfit() / cust1.getOriginalCost() );
            }

        } );

        /*
         * Add all customer if cost is sufficient, start from the highest profit/cost
         * ratio
         */
        double costLimit = this.nrpInstance.getCostLimit();
        int removedCustomerID = removedCustomer.getId();
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            if ( currentSolution.isSafeAddingACustomer( customer, costLimit )
                    && ( customer.getId() != removedCustomerID ) ) {
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
        List< Customer > solution1 = this.nrpSolutions[ solutionIndex1 ].getAcceptedCustomers();
        Set< Customer > solution1Set = new HashSet<>( solution1 );
        List< Customer > solution2 = this.nrpSolutions[ solutionIndex2 ].getAcceptedCustomers();
        Set< Customer > solution2Set = new HashSet<>( solution2 );

        return solution1Set.equals( solution2Set );
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
        return 1.0 / this.bestSolution.getTotalProfit();
    }

    @Override
    public double getFunctionValue( int solutionIndex )
    {
        return 1.0 / this.nrpSolutions[ solutionIndex ].getTotalProfit();
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
        NRPSolution initialSolution = new NRPSolution( this.nrpInstance.getCustomersList() );
        double costLimit = this.nrpInstance.getCostLimit();
        System.out.println( "Cost Limit: " + costLimit );

        List< Customer > haveNotBeenAcceptedCustomers
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
