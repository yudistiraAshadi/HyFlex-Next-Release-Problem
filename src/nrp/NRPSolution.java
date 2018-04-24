package nrp;

import java.util.HashSet;
import java.util.Set;

public class NRPSolution
{
    private Set< Customer > acceptedCustomers = new HashSet<>();
    private Set< Enhancement > acceptedEnhancements = new HashSet<>();
    private double totalCost;
    private double totalProfit;

    /**
     * @param acceptedCustomers
     * @param acceptedEnhancements
     * @param totalCost
     * @param totalProfit
     */
    public NRPSolution( Set< Customer > acceptedCustomers, Set< Enhancement > acceptedEnhancements,
            double totalCost, double totalProfit )
    {
        this.acceptedCustomers = acceptedCustomers;
        this.acceptedEnhancements = acceptedEnhancements;
        this.totalCost = totalCost;
        this.totalProfit = totalProfit;
    }

    /**
     * A copy constructor
     * 
     * @param acceptedCustomers
     * @param acceptedEnhancements
     * @param totalCost
     * @param totalProfit
     */
    public NRPSolution( NRPSolution nrpSolution )
    {
        this.acceptedCustomers = nrpSolution.acceptedCustomers;
        this.acceptedEnhancements = nrpSolution.acceptedEnhancements;
        this.totalCost = nrpSolution.totalCost;
        this.totalProfit = nrpSolution.totalProfit;
    }

    /**
     * @return the copy of acceptedCustomers set
     */
    public Set< Customer > getAcceptedCustomers()
    {
        Set< Customer > copyOfAcceptedCustomers = new HashSet<>( this.acceptedCustomers );

        return copyOfAcceptedCustomers;
    }

    /**
     * @return the copy of acceptedEnhancements set
     */
    public Set< Enhancement > getAcceptedEnhancements()
    {
        Set< Enhancement > copyOfAcceptedEnhancements = new HashSet<>( this.acceptedEnhancements );

        return copyOfAcceptedEnhancements;
    }

    /**
     * @return the totalCost
     */
    public double getTotalCost()
    {
        return totalCost;
    }

    /**
     * @return the totalProfit
     */
    public double getTotalProfit()
    {
        return totalProfit;
    }
}
