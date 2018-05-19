package nrp;

import java.util.ArrayList;
import java.util.List;

class Enhancement
{
    private int id;
    private double cost;
    private List< Enhancement > dependencyEnhancementsList = new ArrayList<>();

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
     * @return the copy of dependencyEnhancementsList
     */
    protected List< Enhancement > getDependencyEnhancementsList()
    {
        List< Enhancement > copyOfDependencyEnhancementsList
                = new ArrayList<>( this.dependencyEnhancementsList );

        return copyOfDependencyEnhancementsList;
    }

    /**
     * Add a enhancement into enhancements set
     * 
     * @param enhancement
     */
    protected void addDependencyEnhancement( Enhancement enhancement )
    {
        if ( !this.dependencyEnhancementsList.contains( enhancement ) ) {
            this.dependencyEnhancementsList.add( enhancement );
        }
    }

    /**
     * @return string representation of the Enhancement class
     */
    @Override
    public String toString()
    {
        return "Enhancement ID: " + this.id + ", Cost: " + this.cost + ", Dependencies: "
                + this.dependencyEnhancementsList;
    }
}
