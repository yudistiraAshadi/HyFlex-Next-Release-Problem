package nrp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	private int numberOfEnhancements;
	private int numberOfCustomers;

	private double costRatio = 0.7;
	private String fileName = "src/nrp/instance/nrp1.txt";

	public NRPInstance()
	{
		/*
		 * read the instance file
		 */
		initializeTheInstance();

		/*
		 * At the end of initialization, count the total of enhancements and customers
		 */
		this.numberOfEnhancements = this.enhancementsMap.size();
		this.numberOfCustomers = this.customersMap.size();
	}

	/**
	 * Initialize the instance by reading the instance files
	 */
	private void initializeTheInstance()
	{
		int totalEnhancement = 0;
        List< Integer > enhancementCosts = new ArrayList< Integer >();

		try ( BufferedReader br = new BufferedReader( new FileReader( fileName ) ) ) {

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
				Customer customer = new Customer( customerId, profitOfCustomer, enhancementSet );
				this.customersMap.put( customerId, customer );
			}

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
	 * @return the enhancementsMap
	 */
	public Map< Integer, Enhancement > getEnhancementMap()
	{
		return enhancementsMap;
	}

	/**
	 * @return the customersMap
	 */
	public Map< Integer, Customer > getCustomerMap()
	{
		return customersMap;
	}

	/**
	 * @return the numberOfEnhancements
	 */
	public int getNumberOfEnhancements()
	{
		return numberOfEnhancements;
	}

	/**
	 * @return the numberOfCustomers
	 */
	public int getNumberOfCustomers()
	{
		return numberOfCustomers;
	}

	/**
	 * @return the getCostRatio
	 */
	public double getCostRatio()
	{
		return costRatio;
	}
}
