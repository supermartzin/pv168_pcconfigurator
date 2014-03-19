package pcconfigurator;

import java.time.ZonedDateTime;

public class Configuration {

    private long id;
    private String name;
    private String creator;
    private ZonedDateTime creationTime;
    private ZonedDateTime lastUpdate;
    
    
    public Configuration(String name, String creator) {
        this.name = name;
        this.creator = creator;
        this.creationTime = ZonedDateTime.now();
        this.lastUpdate = ZonedDateTime.now();
    }
    
    public ZonedDateTime getCreationTime() {      
        return creationTime;
    }

    public void setCreationTime(ZonedDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public ZonedDateTime getLasUpdate() {
        return lastUpdate;
    }

    public void setLasUpdate(ZonedDateTime lasUpdate) {
        this.lastUpdate = lasUpdate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    
    

}
