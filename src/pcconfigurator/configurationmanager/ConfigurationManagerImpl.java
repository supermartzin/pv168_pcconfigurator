package pcconfigurator.configurationmanager;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcconfigurator.exception.*;

public class ConfigurationManagerImpl implements ConfigurationManager {

    public static final Logger logger = Logger.getLogger(ConfigurationManagerImpl.class.getName());
    
    private Connection conn;

    public ConfigurationManagerImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     *
     * @param configuration
     */
    @Override
    public void createConfiguration(Configuration configuration) {
        if(configuration == null) throw new IllegalArgumentException("Configuration argument is null");
        if(configuration.getCreator().isEmpty()) throw new IllegalArgumentException("Configuration does not have a creator.");
        if(configuration.getName().isEmpty()) throw new IllegalArgumentException("Configuration does not have a name.");
        if(configuration.getId()!=null) throw new IllegalArgumentException("Configuration is already in database");
        if(configuration.getCreationTime()==null) throw new IllegalArgumentException("Time is null");
        if(configuration.getLasUpdate()==null) throw new IllegalArgumentException("Last update time is null");
        
        PreparedStatement st = null;
        try{
            conn.setAutoCommit(false);
            st = conn.prepareStatement("INSERT INTO CONFIGURATION (name,creator,creation,update) VALUE (?,?,?,?)", 
                                       Statement.RETURN_GENERATED_KEYS);
            st.setString(1, configuration.getName());
            st.setString(2, configuration.getCreator());            
            Instant instant = configuration.getCreationTime().toInstant(ZoneOffset.UTC);            
            Date creationDate = Date.from(instant);
            st.setDate(3, (java.sql.Date) creationDate);
            Instant instant2 = configuration.getLasUpdate().toInstant(ZoneOffset.UTC);
            Date updateDate = Date.from(instant2);
            st.setDate(4, (java.sql.Date) updateDate);
            
            int totalRows = st.executeUpdate();
            if(totalRows!=1) 
                throw new InternalFailureException("Wrong number of rows!");
            ResultSet keys = st.getGeneratedKeys();
            configuration.setId(getKey(keys,configuration));
            
            conn.commit();
        } catch (SQLException ex){
            if(conn!=null){
                try {
                    conn.rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE,null,ex);
        } finally {
            if(conn!=null)
                try {
                    conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     * @param id
     */
    @Override
    public Configuration getConfigurationById(long id) {
        return null;        
    }

    @Override
    public Set<Configuration> findAllConfigurations() {
        // TODO - implement ConfigurationManagerImpl.findAllConfigurations
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param Configuration
     */
    @Override
    public void updateConfiguration(Configuration configuration) {
        // TODO - implement ConfigurationManagerImpl.updateConfiguration
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param Configuration
     */
    @Override
    public void deleteConfiguration(Configuration configuration) {
        // TODO - implement ConfigurationManagerImpl.deleteConfiguration
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param String
     * @return
     */
    @Override
    public Set<Configuration> findConfigurationByName(String name) {
        // TODO - implement ConfigurationManagerImpl.findConfigurationByName
        throw new UnsupportedOperationException();
    }

    private Long getKey(ResultSet keys, Configuration configuration) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
