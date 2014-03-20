package pcconfigurator.configurationmanager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.TreeSet;
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
        testConfiguration(configuration);
        if (configuration.getId() != null) {
            throw new IllegalArgumentException("Configuration is already in database");
        }
        PreparedStatement st = null;
        try {
            conn.setAutoCommit(false);
            st = conn.prepareStatement("INSERT INTO database.configuration (name,creator,creation_time,last_update) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, configuration.getName());
            st.setString(2, configuration.getCreator());
//            Instant instant = configuration.getCreationTime().toInstant(ZoneOffset.UTC);            
            Date creationDate = new Date(configuration.getCreationTime().toEpochSecond(ZoneOffset.UTC) * 1000);
            st.setDate(3, creationDate);
//            Instant instant2 = configuration.getLasUpdate().toInstant(ZoneOffset.UTC);
            Date updateDate = new Date(configuration.getLastUpdate().toEpochSecond(ZoneOffset.UTC) * 1000);
            st.setDate(4, updateDate);

            int totalRows = st.executeUpdate();
            if (totalRows != 1) {
                throw new InternalFailureException("Wrong number of rows!");
            }
            ResultSet keys = st.getGeneratedKeys();           
            configuration.setId(getKey(keys, configuration));

            conn.commit();
        } catch (SQLException | InternalFailureException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }


    /**
     *
     * @param id
     */
    @Override
    public Configuration getConfigurationById(long id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "SELECT id,name,creator,creation_time,last_update FROM database.configuration WHERE id = ?");
            st.setLong(1, id);
            ResultSet resultSet = st.executeQuery();

            if (resultSet.next()) {
                Configuration configuration = new Configuration();
                configuration.setId(resultSet.getLong("id"));
                configuration.setName(resultSet.getString("name"));
                configuration.setCreator(resultSet.getString("creator"));
                Instant instant = Instant.ofEpochMilli(resultSet.getDate("creation_time").getTime());
                configuration.setCreationTime(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
                Instant instant2 = Instant.ofEpochMilli(resultSet.getDate("last_update").getTime());
                configuration.setLastUpdate(LocalDateTime.ofInstant(instant2, ZoneOffset.UTC));

                if (resultSet.next()) {
                    throw new InternalFailureException("More configurations with same id found");
                }
                return configuration;
            } else {
                throw new IllegalArgumentException("This ID does not EXIST");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalFailureException("Error while get configuration", ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public Set<Configuration> findAllConfigurations() {
        PreparedStatement st = null;
        Set<Configuration> configSet = new TreeSet<>(Configuration.idComparator);
        try {
            st = conn.prepareStatement("SELECT id FROM database.configuration");
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                configSet.add(getConfigurationById((resultSet.getLong("id"))));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return configSet;
    }

    /**
     *
     * @param configuration
     */
    @Override
    public void updateConfiguration(Configuration configuration) {
        PreparedStatement st = null;
        testConfiguration(configuration);
        
        try {
            conn.setAutoCommit(false);
            st = conn.prepareStatement("UPDATE database.configuration SET name=?,creator=?,last_update=? WHERE id=?");
            st.setString(1, configuration.getName());
            st.setString(2, configuration.getCreator());
            Date updateDate = new Date(configuration.getLastUpdate().toEpochSecond(ZoneOffset.UTC) * 1000);
            st.setDate(3, updateDate);
            st.setLong(4, configuration.getId());
            if (st.executeUpdate() != 1) {
                throw new InternalFailureException("Updated more than 1 record");
            }
            conn.commit();
        } catch (SQLException | InternalFailureException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     *
     * @param Configuration
     */
    @Override
    public void deleteConfiguration(Configuration configuration) {
        PreparedStatement st = null;
        if(configuration.getId()==null){
            throw new IllegalArgumentException("NO ID");
        }
        testConfiguration(configuration);
        try {
            conn.setAutoCommit(false);
            st = conn.prepareStatement("DELETE FROM database.configuration WHERE id=?");
            st.setLong(1, configuration.getId());
            if (st.executeUpdate() != 1) {
                throw new InternalFailureException("Deleted more than 1 record");
            }
            conn.commit();
        } catch (SQLException | InternalFailureException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     *
     * @param String
     * @return
     */
    @Override
    public Set<Configuration> findConfigurationByName(String name) {
        PreparedStatement st = null;
        Set<Configuration> result = new TreeSet<>(Configuration.idComparator);
        try{
            st = conn.prepareStatement("SELECT * FROM database.configuration WHERE name LIKE ?");
            st.setString(1, "%"+name+"%");
            ResultSet resultSet = st.executeQuery();
            while(resultSet.next()){
                Configuration configuration = new Configuration();
                configuration.setId(resultSet.getLong("id"));
                configuration.setName(resultSet.getString("name"));
                configuration.setCreator(resultSet.getString("creator"));
                Instant instant = Instant.ofEpochMilli(resultSet.getDate("creation_time").getTime());
                configuration.setCreationTime(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
                Instant instant2 = Instant.ofEpochMilli(resultSet.getDate("last_update").getTime());
                configuration.setLastUpdate(LocalDateTime.ofInstant(instant2, ZoneOffset.UTC));
                result.add(configuration);
            }
        } catch (SQLException ex){
            Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE,null,ex);
        } finally {
            if(st!=null){
                try {
                    st.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ConfigurationManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
        }
        return result;
    }

    private Long getKey(ResultSet keys, Configuration configuration) throws SQLException {
        if (keys.next()) {
            if (keys.getMetaData().getColumnCount() != 1) {
                throw new InternalFailureException("Wrong key fields count");
            }
            Long result = keys.getLong(1);
            if (keys.next()) {
                throw new InternalFailureException("More keys found");
            }
            return result;
        } else {
            throw new InternalFailureException("No key found!");
        }
    }

       private void testConfiguration(Configuration configuration) throws IllegalArgumentException {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration argument is null");
        }
        if (configuration.getCreator() == null){
            throw new IllegalArgumentException("Creatir is null");
        }
        if (configuration.getName() == null){
            throw new IllegalArgumentException("Name is null");
        }
        if (configuration.getCreator().isEmpty()) {
            throw new IllegalArgumentException("Configuration does not have a creator.");
        }
        if (configuration.getName().isEmpty()) {
            throw new IllegalArgumentException("Configuration does not have a name.");
        }        
        if (configuration.getCreationTime() == null) {
            throw new IllegalArgumentException("Time is null");
        }
        if (configuration.getLastUpdate() == null) {
            throw new IllegalArgumentException("Last update time is null");
        }
    }
}
