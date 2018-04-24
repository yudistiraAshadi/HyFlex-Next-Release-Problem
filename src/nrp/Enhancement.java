package nrp;

import java.util.HashSet;
import java.util.Set;

public class Enhancement
{
	private int id;
	private double cost;
	private Set< Enhancement > enhancementsSet = new HashSet<>();

	/**
	 * @param id
	 * @param cost
	 */
	public Enhancement( int id, int cost )
	{
		this.id = id;
		this.cost = cost;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return this.id;
	}

	/**
	 * @return the cost
	 */
	public double getCost()
	{
		return this.cost;
	}

	/**
	 * @return the enhancements set
	 */
	public Set< Enhancement > getEnhancementsSet()
	{
		Set< Enhancement > copyOfEnhancementsSet = new HashSet<>( this.enhancementsSet );

		return copyOfEnhancementsSet;
	}

	/**
	 * Add a enhancement into enhancements set
	 * 
	 * @param enhancement
	 */
	protected void addEnhancement( Enhancement enhancement )
	{
		this.enhancementsSet.add( enhancement );
	}

	@Override
	public String toString()
	{
		return "Requirement ID: " + this.id + ", Cost: " + this.cost + ", Dependencies: "
		        + this.enhancementsSet;
	}
}
