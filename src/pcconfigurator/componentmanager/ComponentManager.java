package pcconfigurator.componentmanager;

import java.util.Set;

public interface ComponentManager {

    /**
     *
     * @param component
     */
    void createComponent(Component component);

    /**
     *
     * @param id
     * @return 
     */
    Component getComponentById(Long id);

    Set<Component> findAllComponents();

    /**
     *
     * @param component
     */
    void updateComponent(Component component);

    /**
     *
     * @param component
     */
    void deleteComponent(Component component);

    /**
     *
     * @param type
     * @return 
     */
    Set<Component> findCompByType(ComponentTypes type);

}
