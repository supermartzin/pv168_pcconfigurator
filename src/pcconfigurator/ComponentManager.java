package pcconfigurator;

import java.util.Set;

public interface ComponentManager {

    /**
     *
     * @param component
     */
    void createComponent(Component component);

    /**
     *
     * @param Long
     */
    Component getComponentById(Long id);

    Set<Component> findAllComponents();

    /**
     *
     * @param Component
     */
    void updateComponent(Component component);

    /**
     *
     * @param Component
     */
    void deleteComponent(Component component);

    /**
     *
     * @param ComponentTypes
     */
    Set<Component> findCompByType(ComponentTypes type);

}
