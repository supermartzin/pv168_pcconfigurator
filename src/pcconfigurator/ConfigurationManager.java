package pcconfigurator;

import java.util.Set;

public interface ConfigurationManager {

	/**
	 * 
	 * @param Configuration
	 */
	void createConfiguration(int Configuration);

	/**
	 * 
	 * @param Long
         * @return 
	 */
	public Configuration getConfigurationById(int Long);

	Set<Configuration> findAllConfigurations();

	/**
	 * 
	 * @param Configuration
	 */
	void updateConfiguration(int Configuration);

	/**
	 * 
	 * @param Configuration
	 */
	void deleteConfiguration(int Configuration);

	/**
	 * 
	 * @param String
	 */
	Set<Configuration> findConfigurationByName(int String);

}