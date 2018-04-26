package nrp;

import java.util.HashSet;
import java.util.Set;

public class Enhancement
{
	private int id;
	private double cost;
	private Set< Enhancement > dependencyEnhancementsSet = new HashSet<>();

	/**
	 * @param id
	 * @param cost
	 */
	protected Enhancement( int id, double cost )
	{
		this.id = id;
		this.cost = cost;
	}

	/**
	 * @return the id
	 */
	protected int getId()
	{
		return this.id;
	}

	/**
	 * @return the cost
	 */
	protected double getCost()
	{
		return this.cost;
	}

	/**
	 * @return the copy of enhancementSet
	 */
	protected Set< Enhancement > getDependencyEnhancementsSet()
	{
		Set< Enhancement > copyOfDependencyEnhancementsSet = new HashSet<>( this.dependencyEnhancementsSet );

		return copyOfDependencyEnhancementsSet;
	}

	/**
	 * Add a enhancement into enhancements set
	 * 
	 * @param enhancement
	 */
    protected void addDependencyEnhancement( Enhancement enhancement )
	{
		this.dependencyEnhancementsSet.add( enhancement );
	}

	/**
	 * @return string representation of the Enhancement class
	 */
	@Override
	public String toString()
	{
		return "Enhancement ID: " + this.id + ", Cost: " + this.cost + ", Dependencies: "
		        + this.dependencyEnhancementsSet;
	}
}
