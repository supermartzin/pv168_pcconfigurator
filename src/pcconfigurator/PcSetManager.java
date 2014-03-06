package pcconfigurator;

import java.util.List;



public interface PcSetManager {

	/**
	 * 
	 * @param PcSet
	 */
	void createPcSet(int PcSet);

	/**
	 * 
	 * @param Configuration
	 * @param Component
	 */
	PcSet getPcSet(int Configuration, int Component);

	/**
	 * 
	 * @param PcSet
	 */
	void updatePcSet(int PcSet);

	/**
	 * 
	 * @param PcSet
	 */
	void deletePcSet(int PcSet);

	/**
	 * 
	 * @param Component
	 */
	List<Configuration> findConfigByComponent(int Component);

	/**
	 * 
	 * @param Configuration
	 */
	List<Component> listCompsInConfiguration(int Configuration);

}