package nrp;

import java.util.HashSet;
import java.util.Set;

public class Customer
{
    private int id;
    private int profit;
    private double profitRequirementsCostRatio;
    private Set<Requirement> requirementSet = new HashSet<>();
    
    public Customer(int id, int profit, Set<Requirement> requirementSet)
    {
        this.id = id;
        this.profit = profit;
        this.requirementSet = requirementSet;
    }

    public double getProfitRequirementsCostRatio()
    {
        return profitRequirementsCostRatio;
    }
    
    public void setProfitRequirementsCostRatio(double profitRequirementsCostRatio)
    {
        this.profitRequirementsCostRatio = profitRequirementsCostRatio;
    }

    public Set<Requirement> getRequirementSet()
    {
        Set<Requirement> copyOfRequirementSet = new HashSet<>(this.requirementSet);
        
        return copyOfRequirementSet;
    }

    public int getId()
    {
        return id;
    }

    public int getProfit()
    {
        return profit;
    }
    
    public String toString()
    {
        return "Customer ID: " + this.id
                + ", Profit: " + this.profit
                + ", Requirements: " + this.requirementSet;  
    }
}
