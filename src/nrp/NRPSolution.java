package nrp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class NRPSolution
{
    private List< Customer > acceptedCustomers = new ArrayList<>();
    private List< Customer > haveNotBeenAcceptedCustomers = new ArrayList<>();
    private List< Enhancement > acceptedEnhancements = new ArrayList<>();

    private double totalCost = 0.0;
    private double totalProfit = 0.0;

    /**
     * Empty constructor
     */
    public NRPSolution()
    {
    }

    /**
     * Basic Constructor
     * 
     * @param customersMap
     */
    protected NRPSolution( List< Customer > customersList )
    {
        /*
         * Create the haveNotBeenAcceptedCustomers list
         */
        List< Customer > haveNotBeenAcceptedCustomers = new ArrayList<>( customersList );

        /*
         * Iterate through haveNotBeenAcceptedCustomers list and fixes their
         * currentRequirements list
         */
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            customer.setCurrentEnhancementsList( customer.getOriginalEnhancementsList() );
        }

        this.haveNotBeenAcceptedCustomers = haveNotBeenAcceptedCustomers;
    }

    /**
     * @param acceptedCustomers
     * @param customersMap
     */
    protected NRPSolution( List< Customer > acceptedCustomers, List< Customer > customersList )
    {
        /*
         * Create the haveNotBeenAcceptedCustomers and acceptedEnhancements list
         */
        List< Customer > haveNotBeenAcceptedCustomers = new ArrayList<>( customersList );
        haveNotBeenAcceptedCustomers.removeAll( acceptedCustomers );

        /*
         * Create the acceptedEnhancements list
         */
        List< Enhancement > acceptedEnhancements = new ArrayList<>();
        for ( Customer customer : acceptedCustomers ) {
            List< Enhancement > originalEnhancementsList = customer.getOriginalEnhancementsList();

            for ( Enhancement enhancement : originalEnhancementsList ) {
                if ( !acceptedEnhancements.contains( enhancement ) ) {
                    acceptedEnhancements.add( enhancement );
                }
            }
        }

        /*
         * Iterate through haveNotBeenAcceptedCustomers list and fixes their
         * currentRequirements list
         */
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            List< Enhancement > currentEnhancementsList = customer.getOriginalEnhancementsList();
            currentEnhancementsList.removeAll( acceptedEnhancements );

            customer.setCurrentEnhancementsList( currentEnhancementsList );
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
     * @return the haveNotBeenAcceptedCustomers
     */
    protected List< Customer > getHaveNotBeenAcceptedCustomers()
    {
        List< Customer > copyOfHaveNotBeenAcceptedCustomers
                = new ArrayList<>( this.haveNotBeenAcceptedCustomers );

        return copyOfHaveNotBeenAcceptedCustomers;
    }

    /**
     * @return the copy of acceptedCustomers list
     */
    protected List< Customer > getAcceptedCustomers()
    {
        List< Customer > copyOfAcceptedCustomers = new ArrayList<>( this.acceptedCustomers );

        return copyOfAcceptedCustomers;
    }

    /**
     * @return the copy of acceptedEnhancements list
     */
    protected List< Enhancement > getAcceptedEnhancements()
    {
        List< Enhancement > copyOfAcceptedEnhancements
                = new ArrayList<>( this.acceptedEnhancements );

        return copyOfAcceptedEnhancements;
    }

    /**
     * @return the totalCost
     */
    protected double getTotalCost()
    {
        return totalCost;
    }

    /**
     * @return the totalProfit
     */
    protected double getTotalProfit()
    {
        return totalProfit;
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
     * @param customer
     * @param costLimit
     * @return true if the currentTotalCost isn't exceeding the cost limit, else
     *         false
     */
    protected boolean isSafeAddingACustomer( Customer customer, double costLimit )
    {
        List< Enhancement > copyOfAcceptedEnhancements
                = new ArrayList<>( this.acceptedEnhancements );
        List< Enhancement > customerOriginalEnhancementsList
                = new ArrayList<>( customer.getOriginalEnhancementsList() );

        for ( Enhancement enhancement : customerOriginalEnhancementsList ) {
            if ( !copyOfAcceptedEnhancements.contains( enhancement ) ) {
                copyOfAcceptedEnhancements.add( enhancement );
            }
        }

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
     * Add a customer to the acceptedCustomers list
     * 
     * @param addedCustomer
     */
    protected void addAnAcceptedCustomer( Customer addedCustomer )
    {
        List< Customer > acceptedCustomers = new ArrayList<>( this.acceptedCustomers );
        List< Customer > haveNotBeenAcceptedCustomers
                = new ArrayList<>( this.haveNotBeenAcceptedCustomers );

        /*
         * Update the haveNotBeenAcceptedCustomers and acceptedEnhancements list
         */
        acceptedCustomers.add( addedCustomer );
        haveNotBeenAcceptedCustomers.remove( addedCustomer );

        /*
         * Update the acceptedEnhancements list
         */
        List< Enhancement > acceptedEnhancements = new ArrayList<>();
        for ( Customer customer : acceptedCustomers ) {
            List< Enhancement > customerOriginalEnhancementsList
                    = customer.getOriginalEnhancementsList();

            for ( Enhancement enhancement : customerOriginalEnhancementsList ) {
                if ( !acceptedEnhancements.contains( enhancement ) ) {
                    acceptedEnhancements.add( enhancement );
                }
            }
        }

        /*
         * Iterate through haveNotBeenAcceptedCustomers list and fixes their
         * currentRequirements list
         */
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            List< Enhancement > currentEnhancementsList = customer.getOriginalEnhancementsList();
            currentEnhancementsList.removeAll( acceptedEnhancements );

            customer.setCurrentEnhancementsList( currentEnhancementsList );
        }

        this.acceptedCustomers = acceptedCustomers;
        this.haveNotBeenAcceptedCustomers = haveNotBeenAcceptedCustomers;
        this.acceptedEnhancements = acceptedEnhancements;
        this.setTotalCost();
        this.setTotalProfit();
    }

    /**
     * Remove a customer from the acceptedCustomers list
     * 
     * @param addedCustomer
     */
    protected void removeAnAcceptedCustomer( Customer removedCustomer )
    {
        List< Customer > acceptedCustomers = new ArrayList<>( this.acceptedCustomers );
        List< Customer > haveNotBeenAcceptedCustomers
                = new ArrayList<>( this.haveNotBeenAcceptedCustomers );

        /*
         * Update the haveNotBeenAcceptedCustomers and acceptedEnhancements list
         */
        haveNotBeenAcceptedCustomers.add( removedCustomer );
        acceptedCustomers.remove( removedCustomer );

        /*
         * Update the acceptedEnhancements list
         */
        List< Enhancement > acceptedEnhancements = new ArrayList<>();
        for ( Customer customer : acceptedCustomers ) {
            List< Enhancement > customerOriginalEnhancementsList
                    = customer.getOriginalEnhancementsList();

            for ( Enhancement enhancement : customerOriginalEnhancementsList ) {
                if ( !acceptedEnhancements.contains( enhancement ) ) {
                    acceptedEnhancements.add( enhancement );
                }
            }
        }

        /*
         * Iterate through haveNotBeenAcceptedCustomers list and fixes their
         * currentRequirements list
         */
        for ( Customer customer : haveNotBeenAcceptedCustomers ) {
            List< Enhancement > currentEnhancementsList = customer.getOriginalEnhancementsList();
            currentEnhancementsList.removeAll( acceptedEnhancements );

            customer.setCurrentEnhancementsList( currentEnhancementsList );
        }

        this.acceptedCustomers = acceptedCustomers;
        this.haveNotBeenAcceptedCustomers = haveNotBeenAcceptedCustomers;
        this.acceptedEnhancements = acceptedEnhancements;
        this.setTotalCost();
        this.setTotalProfit();
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append( "Total cost: " + this.totalCost + "\n" );
        stringBuilder.append( "Total profit: " + this.totalProfit + "\n" );

        stringBuilder.append( "Accepted customers: " );
        Iterator< Customer > acceptedCustomersIterator = this.acceptedCustomers.iterator();
        if ( acceptedCustomersIterator.hasNext() ) {
            Customer customer = acceptedCustomersIterator.next();

            stringBuilder.append( customer.getId() );

            while ( acceptedCustomersIterator.hasNext() ) {
                customer = acceptedCustomersIterator.next();

                stringBuilder.append( ", " + customer.getId() );
            }
        }

        stringBuilder.append( "\nHave not been accepted customers: " );
        Iterator< Customer > haveNotBeenAcceptedCustomersIterator
                = this.haveNotBeenAcceptedCustomers.iterator();
        if ( haveNotBeenAcceptedCustomersIterator.hasNext() ) {
            Customer customer = haveNotBeenAcceptedCustomersIterator.next();

            stringBuilder.append( customer.getId() );

            while ( haveNotBeenAcceptedCustomersIterator.hasNext() ) {
                customer = haveNotBeenAcceptedCustomersIterator.next();

                stringBuilder.append( ", " + customer.getId() );
            }
        }

        stringBuilder.append( "\nEnhancements: " );
        Iterator< Enhancement > acceptedEnhancementsIterator
                = this.acceptedEnhancements.iterator();
        if ( acceptedEnhancementsIterator.hasNext() ) {
            Enhancement enhancement = acceptedEnhancementsIterator.next();

            stringBuilder.append( enhancement.getId() );

            while ( acceptedEnhancementsIterator.hasNext() ) {
                enhancement = acceptedEnhancementsIterator.next();

                stringBuilder.append( ", " + enhancement.getId() );
            }
        }

        return stringBuilder.toString();
    }
}
