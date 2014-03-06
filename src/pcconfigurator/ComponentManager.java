package pcconfigurator;

import java.util.Set;

public interface ComponentManager {

	/**
	 * 
	 * @param Component
	 */
	void createComponent(int Component);

	/**
	 * 
	 * @param Long
	 */
	Component getComponentById(int Long);

	Set<Component> findAllComponents();

	/**
	 * 
	 * @param Component
	 */
	void updateComponent(int Component);

	/**
	 * 
	 * @param Component
	 */
	void deleteComponent(int Component);

	/**
	 * 
	 * @param ComponentTypes
	 */
	Set<Component> findCompByType(int ComponentTypes);

}