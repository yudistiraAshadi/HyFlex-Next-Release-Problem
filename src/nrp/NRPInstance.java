package nrp;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NRPInstance
{
    private Map< Integer, Enhancement > enhancementsMap = new HashMap<>();
    private Map< Integer, Customer > customersMap = new HashMap<>();
    private Set< Enhancement > enhancementsSet = new HashSet<>();
    private Set< Customer > customersSet = new HashSet<>();
    private int numberOfEnhancements;
    private int numberOfCustomers;

    private double totalCost;
    private double costLimit;
    private double costLimitRatio = 0.7;
    private String fileName = "src/nrp/instance/nrp";

    protected NRPInstance( int instanceId )
    {
        /*
         * read the instance file
         */
        initializeTheInstance( instanceId );

        /*
         * At the end of initialization, count the total of enhancements and customers
         */
        this.numberOfEnhancements = this.enhancementsMap.size();
        this.numberOfCustomers = this.customersMap.size();
        this.setTotalCost();
        this.setCostLimit();
    }

    /**
     * Initialize the instance by reading the instance files
     */
    private void initializeTheInstance( int instanceId )
    {
        int totalEnhancement = 0;
        List< Integer > enhancementCosts = new ArrayList< Integer >();

        // new BufferedReader( new FileReader( fileName ) )
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
             * Build enhancements map
             */
            for ( int i = 0; i < totalEnhancement; i++ ) {

                int enhancementId = i + 1;
                Enhancement enhancement
                        = new Enhancement( enhancementId, enhancementCosts.get( i ) );

                this.enhancementsMap.put( enhancementId, enhancement );
            }

            /*
             * Add dependency enhancement to the map
             */
            int numberOfDependencies = Integer.parseInt( br.readLine() );
            for ( int i = 0; i < numberOfDependencies; i++ ) {

                String[] enhancementAndDependencyPair = br.readLine().split( "\\s+" );
                int enhancementId = Integer.parseInt( enhancementAndDependencyPair[ 1 ] );
                int dependencyId = Integer.parseInt( enhancementAndDependencyPair[ 0 ] );

                Enhancement enhancement = this.enhancementsMap.get( enhancementId );
                Enhancement dependency = this.enhancementsMap.get( dependencyId );
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
                Set< Enhancement > enhancementSet = new HashSet<>();
                for ( int j = 0; j < numberOfRequests; j++ ) {

                    int enhancementId = Integer.parseInt( customerInformation[ j + 2 ] );
                    enhancementSet.add( enhancementsMap.get( enhancementId ) );
                }

                // Add enhancements dependencies
                enhancementSet = this.getTheEnhancementSetWithItsDependencies( enhancementSet );

                int customerId = i + 1;
                Customer customer = new Customer( customerId, profitOfCustomer, enhancementSet,
                        numberOfRequests );
                this.customersMap.put( customerId, customer );
            }

            /*
             * Create enhancementsSet and customersSet from enhancementsMap and customersMap
             */
            this.enhancementsSet = new HashSet<>( this.enhancementsMap.values() );
            this.customersSet = new HashSet<>( this.customersMap.values() );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param enhancementIdSet
     * @return the enhancement set along with its dependencies
     */
    private Set< Enhancement > getTheEnhancementSetWithItsDependencies(
            Set< Enhancement > enhancementIdSet )
    {
        Set< Enhancement > newEnhancementIdSet = new HashSet<>( enhancementIdSet );
        for ( Enhancement enhancement : enhancementIdSet ) {

            Set< Enhancement > currentIntegerSet = this.getEnhancementDependencies( enhancement );

            if ( !currentIntegerSet.isEmpty() ) {

                if ( currentIntegerSet.size() > 1 ) {
                    Set< Enhancement > tempIntegerSet
                            = this.getTheEnhancementSetWithItsDependencies( currentIntegerSet );

                    newEnhancementIdSet.addAll( tempIntegerSet );
                } else {
                    newEnhancementIdSet.addAll( currentIntegerSet );
                }
            }
        }

        return newEnhancementIdSet;
    }

    /**
     * @param enhancement
     * @return the dependencies set of an enhancement
     */
    private Set< Enhancement > getEnhancementDependencies( Enhancement enhancement )
    {
        int enhancementId = enhancement.getId();
        Enhancement parentEnhancement = this.enhancementsMap.get( enhancementId );

        Set< Enhancement > parentEnhancementSet = parentEnhancement.getDependencyEnhancementsSet();

        return parentEnhancementSet;
    }

    /**
     * Set the totalCost
     */
    private void setTotalCost()
    {
        double totalCost = 0.0;
        for ( Enhancement enhancement : this.enhancementsMap.values() ) {
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
     * @return the enhancementsMap
     */
    protected Map< Integer, Enhancement > getEnhancementsMap()
    {
        Map< Integer, Enhancement > copyOfEnhancementsMap = new HashMap<>( this.enhancementsMap );

        return copyOfEnhancementsMap;
    }

    /**
     * @return the customersMap
     */
    protected Map< Integer, Customer > getCustomersMap()
    {
        Map< Integer, Customer > copyOfCustomersMap = new HashMap<>( this.customersMap );

        return copyOfCustomersMap;
    }

    /**
     * @return the enhancementsSet
     */
    protected Set< Enhancement > getEnhancementsSet()
    {
        Set< Enhancement > copyOfEnhancementsSet = new HashSet<>( this.enhancementsSet );

        return copyOfEnhancementsSet;
    }

    /**
     * @return the customersSet
     */
    protected Set< Customer > getCustomersSet()
    {
        Set< Customer > copyOfCustomersSet = new HashSet<>( this.customersSet );

        return copyOfCustomersSet;
    }

    /**
     * @return the totalCost
     */
    public double getTotalCost()
    {
        return totalCost;
    }

    /**
     * @return the costLimit
     */
    public double getCostLimit()
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
}
