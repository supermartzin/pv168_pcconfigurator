package pcconfigurator;

import java.util.Objects;

public class PcSet {

    private Component component;
    private Configuration configuration;
    private int numberOfComponents;

  
    public PcSet(Component component, Configuration configuration) {
        this.component = component;
        this.configuration = configuration;
    }

    public PcSet(Component component, Configuration configuration, int numberOfComponents) {
        this(component,configuration);
        this.numberOfComponents = numberOfComponents;
    }

    public int getNumberOfComponents() {
        return numberOfComponents;
    }

    public void setNumberOfComponents(int numberOfComponents) {
        this.numberOfComponents = numberOfComponents;
    }
    
    
    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.component);
        hash = 89 * hash + Objects.hashCode(this.configuration);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PcSet other = (PcSet) obj;
        if (!Objects.equals(this.component, other.component)) {
            return false;
        }
        if (!Objects.equals(this.configuration, other.configuration)) {
            return false;
        }
        return true;
    }
    
    
    

}
