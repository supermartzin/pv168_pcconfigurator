package pcconfigurator.componentmanager;

import java.util.Set;

public interface ComponentManager {

    void createComponent(Component component);

    Component getComponentById(Long id);

    Set<Component> findAllComponents();

    void updateComponent(Component component);

    void deleteComponent(Component component);

    Set<Component> findCompByType(ComponentTypes type);
}
