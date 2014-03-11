package pcconfigurator;

import org.joda.time.*;

public class Configuration {

    private long id;
    private String name;
    private String creator;
    private DateTime creationTime;
    private DateTime lastUpdate;
    
    
    public Configuration(long id, String name, String creator) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.creationTime = new DateTime();
        this.lastUpdate = new DateTime();
    }

    public DateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(DateTime creationTime) {
        this.creationTime = creationTime;
    }

    public DateTime getLasUpdate() {
        return lastUpdate;
    }

    public void setLasUpdate(DateTime lasUpdate) {
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
