package nrp;

import java.util.ArrayList;
import java.util.List;

class Customer
{
    private int id;
    private double profit;
    private int totalRequestedEnhancements;

//    private Set< Enhancement > originalEnhancementsSet = new HashSet<>();
    private List< Enhancement > originalEnhancementsList = new ArrayList<>();
    private double originalCost;

//    private Set< Enhancement > currentEnhancementsSet = new HashSet<>();
    private List< Enhancement > currentEnhancementsList = new ArrayList<>();
    private double currentCost;

    /**
     * Empty constructor
     */
    protected Customer()
    {

    }

    /**
     * @param id
     * @param profit
     * @param originalEnhancementsSet
     */
    protected Customer( int id, double profit, List< Enhancement > originalEnhancementsList,
            int totalRequestedEnhancements )
    {
        this.id = id;
        this.profit = profit;
        this.totalRequestedEnhancements = totalRequestedEnhancements;
        this.originalEnhancementsList = originalEnhancementsList;
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
    protected int getTotalRequestedEnhancements()
    {
        return totalRequestedEnhancements;
    }

    /**
     * @return the originalCost
     */
    protected double getOriginalCost()
    {
        return originalCost;
    }

    /**
     * @return the copy of originalEnhancementSet
     */
    protected List< Enhancement > getOriginalEnhancementsList()
    {
        List< Enhancement > copyOfOriginalEnhancementsList
                = new ArrayList<>( this.originalEnhancementsList );

        return copyOfOriginalEnhancementsList;
    }
    
    /**
     * @return the currentCost
     */
    protected double getCurrentCost()
    {
        return currentCost;
    }
    
    /**
     * @return the currentEnhancementsSet
     */
    protected List< Enhancement > getCurrentEnhancementsList()
    {
        return currentEnhancementsList;
    }


    /**
     * Set the originalCost
     */
    private void setOriginalCost()
    {
        double originalCost = 0;
        for ( Enhancement enhancement : this.originalEnhancementsList ) {
            originalCost += enhancement.getCost();
        }

        this.originalCost = originalCost;
    }

    /**
     * Set the currentCost
     */
    private void setCurrentCost()
    {
        double currentCost = 0;
        for ( Enhancement enhancement : this.currentEnhancementsList ) {
            currentCost += enhancement.getCost();
        }

        this.currentCost = currentCost;
    }

    /**
     * @param currentEnhancementsSet
     */
    protected void setCurrentEnhancementsList( List< Enhancement > currentEnhancementsList )
    {
        List< Enhancement > copyOfCurrentEnhancementsList = new ArrayList<>( currentEnhancementsList );

        this.currentEnhancementsList = copyOfCurrentEnhancementsList;
        this.setCurrentCost();
    }

    /**
     * @return string representation of the Customer class
     */
    @Override
    public String toString()
    {
        return "Customer ID: " + this.id + ", Profit: " + this.profit + ", Enhancements: "
                + ", Cost: " + this.originalCost + this.originalEnhancementsList + "\n";
    }
}
