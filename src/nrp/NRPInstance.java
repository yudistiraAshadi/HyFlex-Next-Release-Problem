package nrp;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class NRPInstance
{
    private List< Enhancement > enhancementsList = new ArrayList<>();
    private List< Customer > customersList = new ArrayList<>();

    private int numberOfEnhancements;
    private int numberOfCustomers;

    private int instanceId;
    private double totalCost;
    private double costLimit;
    private double costLimitRatio = 0.7;
    private String fileName = "src/nrp/instance/nrp";

    protected NRPInstance( int instanceId )
    {
        /*
         * read the instance file
         */
        this.instanceId = instanceId;
        initializeTheInstance( instanceId );

        /*
         * At the end of initialization, count the total of enhancements and customers
         */
        this.numberOfEnhancements = this.enhancementsList.size();
        this.numberOfCustomers = this.customersList.size();
        this.setTotalCost();
        this.setCostLimit();
    }

    /**
     * Initialize the instance by reading the instance files
     * 
     * @param instanceId
     */
    private void initializeTheInstance( int instanceId )
    {
        int totalEnhancement = 0;
        List< Integer > enhancementCosts = new ArrayList< Integer >();

        Path pathToInstanceFile
                = FileSystems.getDefault().getPath( ".", fileName + instanceId + ".txt" );
        try ( BufferedReader br = Files.newBufferedReader( pathToInstanceFile ) ) {

            /*
             * Get enhancement costs array, read it per level, as the format of the file,
             * also count how many enhancements expected
             */
            int enhancementLevel = Integer.parseInt( br.readLine() );
            for ( int level = 0; level < enhancementLevel; level++ ) {

                int numberOfEnhancements = Integer.parseInt( br.readLine() );
                totalEnhancement += numberOfEnhancements;

                String[] enhancementCostsInString = br.readLine().split( "\\s+" );
                for ( int i = 0; i < numberOfEnhancements; i++ ) {
                    enhancementCosts.add( Integer.parseInt( enhancementCostsInString[ i ] ) );
                }
            }

            /*
             * Build enhancements list
             */
            for ( int i = 0; i < totalEnhancement; i++ ) {

                int enhancementId = i + 1;
                Enhancement enhancement
                        = new Enhancement( enhancementId, enhancementCosts.get( i ) );

                this.enhancementsList.add( enhancement );
            }

            /*
             * Add dependency enhancement to the list
             */
            int numberOfDependencies = Integer.parseInt( br.readLine() );
            for ( int i = 0; i < numberOfDependencies; i++ ) {

                String[] enhancementAndDependencyPair = br.readLine().split( "\\s+" );
                int enhancementId = Integer.parseInt( enhancementAndDependencyPair[ 1 ] );
                int dependencyId = Integer.parseInt( enhancementAndDependencyPair[ 0 ] );

                Enhancement enhancement = this.enhancementsList.get( enhancementId - 1 );
                Enhancement dependency = this.enhancementsList.get( dependencyId - 1 );
                enhancement.addDependencyEnhancement( dependency );
            }

            /*
             * Build customer map
             */
            int numberOfCustomers = Integer.parseInt( br.readLine() );
            for ( int i = 0; i < numberOfCustomers; i++ ) {

                // Create initial information
                String[] customerInformation = br.readLine().split( "\\s+" );
                int profitOfCustomer = Integer.parseInt( customerInformation[ 0 ] );
                int numberOfRequests = Integer.parseInt( customerInformation[ 1 ] );
                List< Enhancement > enhancementsList = new ArrayList<>();
                for ( int j = 0; j < numberOfRequests; j++ ) {

                    int enhancementId = Integer.parseInt( customerInformation[ j + 2 ] );
                    enhancementsList.add( this.enhancementsList.get( enhancementId - 1 ) );
                }

                // Add enhancements dependencies
                enhancementsList = this.getTheEnhancementsListWithItsDependencies(
                        new ArrayList<>( enhancementsList ) );

                int customerId = i + 1;
                Customer customer = new Customer( customerId, profitOfCustomer, enhancementsList,
                        numberOfRequests );
                this.customersList.add( customer );
            }

        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Set the totalCost
     */
    private void setTotalCost()
    {
        double totalCost = 0.0;
        for ( Enhancement enhancement : this.enhancementsList ) {
            totalCost += enhancement.getCost();
        }

        this.totalCost = totalCost;
    }

    /**
     * Set the costLimit
     */
    private void setCostLimit()
    {
        double costLimit = 0.0;
        if ( this.totalCost == 0 ) {
            this.setTotalCost();
        }

        costLimit = this.totalCost * this.costLimitRatio;
        this.costLimit = costLimit;
    }

    /**
     * @return the enhancementsList
     */
    protected List< Enhancement > getEnhancementsList()
    {
        List< Enhancement > copyOfEnhancementsList = new ArrayList<>( this.enhancementsList );

        return copyOfEnhancementsList;
    }

    /**
     * @return the customersList
     */
    protected List< Customer > getCustomersList()
    {
        List< Customer > copyOfCustomersList = new ArrayList<>( this.customersList );

        return copyOfCustomersList;
    }

    /**
     * @return the instanceId
     */
    protected int getInstanceId()
    {
        return instanceId;
    }

    /**
     * @return the totalCost
     */
    protected double getTotalCost()
    {
        return totalCost;
    }

    /**
     * @return the costLimit
     */
    protected double getCostLimit()
    {
        return costLimit;
    }

    /**
     * @return the numberOfEnhancements
     */
    protected int getNumberOfEnhancements()
    {
        return numberOfEnhancements;
    }

    /**
     * @return the numberOfCustomers
     */
    protected int getNumberOfCustomers()
    {
        return numberOfCustomers;
    }

    /**
     * @return the costLimitRatio
     */
    protected double getCostLimitRatio()
    {
        return costLimitRatio;
    }

    /**
     * @param enhancementIdSet
     * @return the enhancement set along with its dependencies
     */
    private List< Enhancement > getTheEnhancementsListWithItsDependencies(
            List< Enhancement > enhancementsList )
    {
        List< Enhancement > newEnhancementsList = new ArrayList<>( enhancementsList );

        for ( Enhancement enhancement : enhancementsList ) {

            List< Enhancement > currentDependenciesList
                    = this.getEnhancementDependencies( enhancement );

            if ( !currentDependenciesList.isEmpty() ) {

                if ( currentDependenciesList.size() > 1 ) {
                    List< Enhancement > dependenciesList = this
                            .getTheEnhancementsListWithItsDependencies( currentDependenciesList );

                    for ( Enhancement dependency : dependenciesList ) {
                        if ( !newEnhancementsList.contains( dependency ) ) {
                            newEnhancementsList.add( dependency );
                        }
                    }
                } else {
                    for ( Enhancement dependency : currentDependenciesList ) {
                        if ( !newEnhancementsList.contains( dependency ) ) {
                            newEnhancementsList.add( dependency );
                        }
                    }
                }
            }
        }

        return newEnhancementsList;
    }

    /**
     * @param enhancement
     * @return the dependencies set of an enhancement
     */
    private List< Enhancement > getEnhancementDependencies( Enhancement enhancement )
    {
        int enhancementId = enhancement.getId();
        Enhancement parentEnhancement = this.enhancementsList.get( enhancementId - 1 );

        List< Enhancement > parentEnhancementsList
                = parentEnhancement.getDependencyEnhancementsList();

        return parentEnhancementsList;
    }

}
