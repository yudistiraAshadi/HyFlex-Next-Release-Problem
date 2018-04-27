package nrp;

import java.util.HashSet;
import java.util.Set;

public class NRPSolution
{
    private Set< Customer > acceptedCustomers = new HashSet<>();
    private Set< Customer > haveNotBeenAcceptedCustomers = new HashSet<>();
    private Set< Enhancement > acceptedEnhancements = new HashSet<>();
    private double totalCost;
    private double totalProfit;

    /**
     * Basic Constructor
     * 
     * @param customersMap
     */
    public NRPSolution(  Set< Customer > customersSet )
    {
        /*
         * Create the haveNotBeenAcceptedCustomers set
         */
        Set< Customer > haveNotBeenAcceptedCustomers = new HashSet<>( customersSet );

        /*
         * Iterate through haveNotBeenAcceptedCustomers set and fixes their
         * currentRequirements set
         */
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            customer.setCurrentEnhancementsSet( customer.getOriginalEnhancementsSet() );
        }

        this.haveNotBeenAcceptedCustomers = haveNotBeenAcceptedCustomers;
    }

    /**
     * @param acceptedCustomers
     * @param customersMap
     */
    protected NRPSolution( Set< Customer > acceptedCustomers,
            Set< Customer > customersSet )
    {
        /*
         * Create the haveNotBeenAcceptedCustomers and acceptedEnhancements set
         */
        Set< Customer > haveNotBeenAcceptedCustomers = new HashSet<>( customersSet );
        haveNotBeenAcceptedCustomers.removeAll( acceptedCustomers );

        /*
         * Create the acceptedEnhancements set
         */
        Set< Enhancement > acceptedEnhancements = new HashSet<>();
        for ( Customer customer : acceptedCustomers ) {
            acceptedEnhancements.addAll( customer.getOriginalEnhancementsSet() );
        }

        /*
         * Iterate through haveNotBeenAcceptedCustomers set and fixes their
         * currentRequirements set
         */
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            Set< Enhancement > currentEnhancementsSet = customer.getOriginalEnhancementsSet();
            currentEnhancementsSet.removeAll( acceptedEnhancements );

            customer.setCurrentEnhancementsSet( currentEnhancementsSet );
        }

        this.acceptedCustomers = acceptedCustomers;
        this.acceptedEnhancements = acceptedEnhancements;
        this.haveNotBeenAcceptedCustomers = haveNotBeenAcceptedCustomers;
        this.setTotalCost();
        this.setTotalProfit();
    }

    /**
     * A copy constructor
     * 
     * @param acceptedCustomers
     * @param acceptedEnhancements
     * @param totalCost
     * @param totalProfit
     */
    protected NRPSolution( NRPSolution nrpSolution )
    {
        this.acceptedCustomers = nrpSolution.acceptedCustomers;
        this.haveNotBeenAcceptedCustomers = nrpSolution.haveNotBeenAcceptedCustomers;
        this.acceptedEnhancements = nrpSolution.acceptedEnhancements;
        this.totalCost = nrpSolution.totalCost;
        this.totalProfit = nrpSolution.totalProfit;
    }

    /**
     * @param customer
     * @param costLimit
     * @return true if the currentTotalCost isn't exceeding the cost limit, else
     *         false
     */
    protected boolean isSafeAddingACustomer( Customer customer, double costLimit )
    {
        Set< Enhancement > copyOfAcceptedEnhancements = new HashSet<>( this.acceptedEnhancements );
        double currentTotalCost = 0.0;

        copyOfAcceptedEnhancements.addAll( customer.getOriginalEnhancementsSet() );

        for ( Enhancement enhancement : copyOfAcceptedEnhancements ) {
            currentTotalCost += enhancement.getCost();
        }

        if ( currentTotalCost < costLimit ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add a customer to the acceptedCustomers set
     * 
     * @param customer
     */
    protected void addAnAcceptedCustomer( Customer customer )
    {
        Set< Customer > acceptedCustomers = new HashSet<>( this.acceptedCustomers );
        Set< Customer > haveNotBeenAcceptedCustomers
                = new HashSet<>( this.haveNotBeenAcceptedCustomers );
        Set< Enhancement > acceptedEnhancements = new HashSet<>();

        /*
         * Update the haveNotBeenAcceptedCustomers and acceptedEnhancements set
         */
        acceptedCustomers.add( customer );
        haveNotBeenAcceptedCustomers.remove( customer );

        /*
         * Update the acceptedEnhancements set
         */
        for ( Customer cust : acceptedCustomers ) {
            acceptedEnhancements.addAll( cust.getOriginalEnhancementsSet() );
        }

        /*
         * Iterate through haveNotBeenAcceptedCustomers set and fixes their
         * currentRequirements set
         */
        for ( Customer cust : haveNotBeenAcceptedCustomers ) {
            Set< Enhancement > currentEnhancementsSet = cust.getOriginalEnhancementsSet();
            currentEnhancementsSet.removeAll( this.acceptedEnhancements );

            customer.setCurrentEnhancementsSet( currentEnhancementsSet );
        }

        this.acceptedCustomers = acceptedCustomers;
        this.haveNotBeenAcceptedCustomers = haveNotBeenAcceptedCustomers;
        this.acceptedCustomers = acceptedCustomers;
        this.setTotalCost();
        this.setTotalProfit();
    }

    protected void removeAnAcceptedCustomer( Customer customer )
    {
        Set< Customer > acceptedCustomers = new HashSet<>( this.acceptedCustomers );
        Set< Customer > haveNotBeenAcceptedCustomers
                = new HashSet<>( this.haveNotBeenAcceptedCustomers );
        Set< Enhancement > acceptedEnhancements = new HashSet<>();

        /*
         * Update the haveNotBeenAcceptedCustomers and acceptedEnhancements set
         */
        haveNotBeenAcceptedCustomers.add( customer );
        acceptedCustomers.remove( customer );

        /*
         * Update the acceptedEnhancements set
         */
        for ( Customer cust : acceptedCustomers ) {
            acceptedEnhancements.addAll( cust.getOriginalEnhancementsSet() );
        }

        /*
         * Iterate through haveNotBeenAcceptedCustomers set and fixes their
         * currentRequirements set
         */
        for ( Customer cust : haveNotBeenAcceptedCustomers ) {
            Set< Enhancement > currentEnhancementsSet = cust.getOriginalEnhancementsSet();
            currentEnhancementsSet.removeAll( this.acceptedEnhancements );

            customer.setCurrentEnhancementsSet( currentEnhancementsSet );
        }

        this.acceptedCustomers = acceptedCustomers;
        this.haveNotBeenAcceptedCustomers = haveNotBeenAcceptedCustomers;
        this.acceptedCustomers = acceptedCustomers;
        this.setTotalCost();
        this.setTotalProfit();
    }

    /**
     * @return the haveNotBeenAcceptedCustomers
     */
    protected Set< Customer > getHaveNotBeenAcceptedCustomers()
    {
        Set< Customer > copyOfHaveNotBeenAcceptedCustomers
                = new HashSet<>( this.haveNotBeenAcceptedCustomers );

        return copyOfHaveNotBeenAcceptedCustomers;
    }

    /**
     * @return the copy of acceptedCustomers set
     */
    protected Set< Customer > getAcceptedCustomers()
    {
        Set< Customer > copyOfAcceptedCustomers = new HashSet<>( this.acceptedCustomers );

        return copyOfAcceptedCustomers;
    }

    /**
     * @return the copy of acceptedEnhancements set
     */
    protected Set< Enhancement > getAcceptedEnhancements()
    {
        Set< Enhancement > copyOfAcceptedEnhancements = new HashSet<>( this.acceptedEnhancements );

        return copyOfAcceptedEnhancements;
    }

    /*
     * set the totalCost
     */
    private void setTotalCost()
    {
        double totalCost = 0.0;
        for ( Enhancement enhancement : this.acceptedEnhancements ) {
            totalCost += enhancement.getCost();
        }

        this.totalCost = totalCost;
    }

    /**
     * @return the totalCost
     */
    protected double getTotalCost()
    {
        return totalCost;
    }

    /*
     * set the totalProfit
     */
    private void setTotalProfit()
    {
        double totalProfit = 0.0;
        for ( Customer customer : this.acceptedCustomers ) {
            totalProfit += customer.getProfit();
        }

        this.totalProfit = totalProfit;
    }

    /**
     * @return the totalProfit
     */
    protected double getTotalProfit()
    {
        return totalProfit;
    }
}
