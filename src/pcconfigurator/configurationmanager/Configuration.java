package pcconfigurator.configurationmanager;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;


public class Configuration {

    private Long id;
    private String name;
    private String creator;
    private LocalDateTime creationTime;
    private LocalDateTime lastUpdate;
    
    
    public Configuration(String name, String creator) {
        this.name = name;
        this.creator = creator;        
        this.creationTime = LocalDateTime.now();
        this.lastUpdate = LocalDateTime.now();
    }

    Configuration() {        
    }
    
    public LocalDateTime getCreationTime() {      
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lasUpdate) {
        this.lastUpdate = lasUpdate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (this.id ^ (this.id >>> 32));
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
        final Configuration other = (Configuration) obj;
        return Objects.equals(this.id, other.id);
    }
    
    public static final Comparator<Configuration> idComparator = (Configuration o1, Configuration o2) 
            -> o1.getId().compareTo(o2.getId());
    
}
