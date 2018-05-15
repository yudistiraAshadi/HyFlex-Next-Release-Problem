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
    public NRPSolution( Set< Customer > customersSet )
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
        this.setTotalCost();
        this.setTotalProfit();
    }

    /**
     * @param acceptedCustomers
     * @param customersMap
     */
    protected NRPSolution( Set< Customer > acceptedCustomers, Set< Customer > customersSet )
    {
        /*
         * Create the acceptedEnhancements set
         */
        Set< Enhancement > acceptedEnhancements = new HashSet<>();
        for ( Customer customer : acceptedCustomers ) {
            acceptedEnhancements.addAll( customer.getOriginalEnhancementsSet() );
        }

        Set< Customer > haveNotBeenAcceptedCustomers = new HashSet<>();
        for ( Customer customer : customersSet ) {
            if ( acceptedEnhancements.containsAll( customer.getOriginalEnhancementsSet() ) ) {
                acceptedCustomers.add( customer );
            } else {
                haveNotBeenAcceptedCustomers.add( customer );
            }
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
        this.haveNotBeenAcceptedCustomers = haveNotBeenAcceptedCustomers;
        this.acceptedEnhancements = acceptedEnhancements;
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

        copyOfAcceptedEnhancements.addAll( customer.getOriginalEnhancementsSet() );

        double currentTotalCost = 0.0;
        for ( Enhancement enhancement : copyOfAcceptedEnhancements ) {
            currentTotalCost += enhancement.getCost();
        }

        if ( currentTotalCost <= costLimit ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add a customer to the acceptedCustomers set
     * 
     * @param addedCustomer
     */
    protected void addAnAcceptedCustomer( Customer addedCustomer )
    {
        /*
         * Update the acceptedEnhancements set
         */
        Set< Enhancement > copyOfAcceptedEnhancements = new HashSet<>( this.acceptedEnhancements );
        copyOfAcceptedEnhancements.addAll( addedCustomer.getOriginalEnhancementsSet() );

        /*
         * Create a new acceptedCustomers and haveNotBeenAcceptedCustomers
         */
        Set< Customer > customersSet = new HashSet<>( this.acceptedCustomers );
        customersSet.addAll( this.haveNotBeenAcceptedCustomers );

        Set< Customer > acceptedCustomers = new HashSet<>();
        Set< Customer > haveNotBeenAcceptedCustomers = new HashSet<>();
        for ( Customer customer : customersSet ) {
            if ( copyOfAcceptedEnhancements.containsAll( customer.getOriginalEnhancementsSet() ) ) {
                acceptedCustomers.add( customer );
            } else {
                haveNotBeenAcceptedCustomers.add( customer );
            }
        }

        /*
         * Iterate through haveNotBeenAcceptedCustomers set and fixes their
         * currentRequirements set
         */
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            Set< Enhancement > currentEnhancementsSet = customer.getOriginalEnhancementsSet();
            currentEnhancementsSet.removeAll( copyOfAcceptedEnhancements );

            customer.setCurrentEnhancementsSet( currentEnhancementsSet );
        }

        this.acceptedCustomers = acceptedCustomers;
        this.haveNotBeenAcceptedCustomers = haveNotBeenAcceptedCustomers;
        this.acceptedEnhancements = copyOfAcceptedEnhancements;
        this.setTotalCost();
        this.setTotalProfit();
    }

    /**
     * Remove a customer from the acceptedCustomers set
     * 
     * @param removedCustomer
     */
    protected void removeAnAcceptedCustomer( Customer removedCustomer )
    {
        /*
         * Update the acceptedEnhancements set
         */
        Set< Enhancement > copyOfAcceptedEnhancements = new HashSet<>( this.acceptedEnhancements );
        copyOfAcceptedEnhancements.removeAll( removedCustomer.getOriginalEnhancementsSet() );

        /*
         * Create a new acceptedCustomers and haveNotBeenAcceptedCustomers
         */
        Set< Customer > customersSet = new HashSet<>( this.acceptedCustomers );
        customersSet.addAll( this.haveNotBeenAcceptedCustomers );

        Set< Customer > acceptedCustomers = new HashSet<>();
        Set< Customer > haveNotBeenAcceptedCustomers = new HashSet<>();
        for ( Customer customer : customersSet ) {
            if ( copyOfAcceptedEnhancements.containsAll( customer.getOriginalEnhancementsSet() ) ) {
                acceptedCustomers.add( customer );
            } else {
                haveNotBeenAcceptedCustomers.add( customer );
            }
        }

        /*
         * Iterate through haveNotBeenAcceptedCustomers set and fixes their
         * currentRequirements set
         */
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            Set< Enhancement > currentEnhancementsSet = customer.getOriginalEnhancementsSet();
            currentEnhancementsSet.removeAll( copyOfAcceptedEnhancements );

            customer.setCurrentEnhancementsSet( currentEnhancementsSet );
        }

        this.acceptedCustomers = acceptedCustomers;
        this.haveNotBeenAcceptedCustomers = haveNotBeenAcceptedCustomers;
        this.acceptedEnhancements = copyOfAcceptedEnhancements;
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

    /**
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

    /**
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
