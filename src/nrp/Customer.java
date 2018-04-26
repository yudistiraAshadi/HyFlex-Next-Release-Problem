package nrp;

import java.util.HashSet;
import java.util.Set;

public class Customer
{
    private int id;
    private double profit;
    private int totalRequestedEnhancements;

    private Set< Enhancement > originalEnhancementsSet = new HashSet<>();
    private double originalCost;

    private Set< Enhancement > currentEnhancementsSet = new HashSet<>();
    private double currentCost;

    /**
     * @param id
     * @param profit
     * @param originalEnhancementsSet
     */
    protected Customer( int id, double profit, Set< Enhancement > originalEnhancementsSet,
            int totalRequestedEnhancements )
    {
        this.id = id;
        this.profit = profit;
        this.originalEnhancementsSet = originalEnhancementsSet;
        this.totalRequestedEnhancements = totalRequestedEnhancements;
        this.setOriginalCost();
    }

    /**
     * @return the id
     */
    protected int getId()
    {
        return id;
    }

    /**
     * @return the profit
     */
    protected double getProfit()
    {
        return profit;
    }

    /**
     * @return the totalRequestedEnhancements
     */
    public int getTotalRequestedEnhancements()
    {
        return totalRequestedEnhancements;
    }

    /**
     * @return the copy of originalEnhancementSet
     */
    protected Set< Enhancement > getOriginalEnhancementsSet()
    {
        Set< Enhancement > copyOfOriginalEnhancementsSet
                = new HashSet<>( this.originalEnhancementsSet );

        return copyOfOriginalEnhancementsSet;
    }

    /**
     * Set the originalCost
     */
    private void setOriginalCost()
    {
        double originalCost = 0;
        for ( Enhancement enhancement : this.originalEnhancementsSet ) {
            originalCost += enhancement.getCost();
        }

        this.originalCost = originalCost;
    }

    /**
     * @return the originalCost
     */
    protected double getOriginalCost()
    {
        return originalCost;
    }

    /**
     * @return the currentEnhancementsSet
     */
    protected Set< Enhancement > getCurrentEnhancementsSet()
    {
        return currentEnhancementsSet;
    }

    /**
     * @param currentEnhancementsSet
     */
    protected void setCurrentEnhancementsSet( Set< Enhancement > currentEnhancementsSet )
    {
        Set< Enhancement > copyOfCurrentEnhancementSet = new HashSet<>( currentEnhancementsSet );

        this.currentEnhancementsSet = copyOfCurrentEnhancementSet;
        this.setCurrentCost();
    }

    /**
     * Set the currentCost
     */
    private void setCurrentCost()
    {
        double currentCost = 0;
        for ( Enhancement enhancement : this.currentEnhancementsSet ) {
            currentCost += enhancement.getCost();
        }

        this.currentCost = currentCost;
    }

    /**
     * @return the currentCost
     */
    protected double getCurrentCost()
    {
        return currentCost;
    }

    /**
     * @return string representation of the Customer class
     */
    @Override
    public String toString()
    {
        return "Customer ID: " + this.id + ", Profit: " + this.profit + ", Enhancements: "
                + this.originalEnhancementsSet;
    }
}
