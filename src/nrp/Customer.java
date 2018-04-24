package nrp;

import java.util.HashSet;
import java.util.Set;

public class Customer
{
	private int id;
	private double profit;

	private Set< Enhancement > originalEnhancementsSet = new HashSet<>();
	private double originalCost;

	private Set< Enhancement > currentEnhancementsSet = new HashSet<>();
	private double currentCost;

	/**
	 * @param id
	 * @param profit
	 * @param originalEnhancementsSet
	 */
	public Customer( int id, double profit, Set< Enhancement > originalEnhancementsSet )
	{
		this.id = id;
		this.profit = profit;
		this.originalEnhancementsSet = originalEnhancementsSet;

		double originalCost = 0;
		for ( Enhancement enhancement : this.originalEnhancementsSet ) {
			originalCost += enhancement.getCost();
		}

		this.originalCost = originalCost;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @return the profit
	 */
	public double getProfit()
	{
		return profit;
	}

	/**
	 * @return the copy of originalEnhancementSet
	 */
	public Set< Enhancement > getOriginalEnhancementsSet()
	{
		Set< Enhancement > copyOfOriginalEnhancementsSet
		        = new HashSet<>( this.originalEnhancementsSet );

		return copyOfOriginalEnhancementsSet;
	}

	/**
	 * @return the originalCost
	 */
	public double getOriginalCost()
	{
		return originalCost;
	}

	/**
	 * @return the currentEnhancementsSet
	 */
	public Set< Enhancement > getCurrentEnhancementsSet()
	{
		return currentEnhancementsSet;
	}

	/**
	 * @param currentEnhancementsSet
	 */
	public void setCurrentEnhancementsSet( Set< Enhancement > currentEnhancementsSet )
	{
		Set< Enhancement > copyOfCurrentEnhancementSet = new HashSet<>( currentEnhancementsSet );

		this.currentEnhancementsSet = copyOfCurrentEnhancementSet;
		
		double currentCost = 0;
		for ( Enhancement enhancement : this.currentEnhancementsSet ) {
			currentCost += enhancement.getCost();
		}

		this.currentCost = currentCost;
	}

	/**
	 * @return the currentCost
	 */
	public double getCurrentCost()
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
