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
import javax.sql.DataSource;
import pcconfigurator.exception.*;

public class ConfigurationManagerImpl implements ConfigurationManager {

    public static final Logger LOGGER = Logger.getLogger(ConfigurationManagerImpl.class.getName());
    private final DataSource dataSource;

    public ConfigurationManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createConfiguration(Configuration configuration) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        
        testConfiguration(configuration);
        if (configuration.getId() != null) {
            throw new IllegalArgumentException("Configuration is already in database");
        }
        
        PreparedStatement st = null;
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement("INSERT INTO database.configuration (name,creator,creation_time,last_update) "
                                     + "VALUES (?,?,?,?)",
                                        Statement.RETURN_GENERATED_KEYS);
            st.setString(1, configuration.getName());
            st.setString(2, configuration.getCreator());
          
            Date creationDate = new Date(configuration.getCreationTime().toEpochSecond(ZoneOffset.UTC) * 1000);
            st.setDate(3, creationDate);

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
            rollbackChanges(conn);
            LOGGER.log(Level.SEVERE, "Inserting configuration into database failed: ", ex);
        } finally {
            closeSources(conn, st);
        }
    }

    @Override
    public Configuration getConfigurationById(Long id) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
	if (id == null) throw new IllegalArgumentException("id is null");
        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT conf_id,name,creator,creation_time,last_update "
                  + "FROM database.configuration "
                  + "WHERE conf_id = ?");
            st.setLong(1, id);
            
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                Configuration configuration = new Configuration();
                configuration.setId(resultSet.getLong("conf_id"));
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
            LOGGER.log(Level.SEVERE, "Getting of configuration from database failed: ", ex);
            throw new InternalFailureException("Error while get configuration", ex);
        } finally {
            closeSources(conn, st);
        }
    }

    @Override
    public Set<Configuration> findAllConfigurations() {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        
        Set<Configuration> configSet = new TreeSet<>(Configuration.idComparator);
        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement("SELECT conf_id "
                                     + "FROM database.configuration");
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                configSet.add(getConfigurationById((resultSet.getLong("conf_id"))));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Getting all configurations from database failed: ", ex);
        } finally {
            closeSources(conn, st);
        }
        
        return configSet;
    }

    @Override
    public void updateConfiguration(Configuration configuration) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        testConfiguration(configuration);
        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement("UPDATE database.configuration "
                                     + "SET name=?,creator=?,last_update=? "
                                     + "WHERE conf_id=?");
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
            rollbackChanges(conn);
            LOGGER.log(Level.SEVERE, "Updating configuration in database failed: ", ex);
        } finally {
            closeSources(conn, st);
        }
    }

    @Override
    public void deleteConfiguration(Configuration configuration) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        testConfiguration(configuration);
        if(configuration.getId()==null) throw new IllegalArgumentException("NO ID");
        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement("DELETE FROM database.configuration "
                                     + "WHERE conf_id=?");
            st.setLong(1, configuration.getId());
            if (st.executeUpdate() != 1) {
                throw new InternalFailureException("Deleted more than 1 record");
            }
            conn.commit();
        } catch (SQLException | InternalFailureException ex) {
            rollbackChanges(conn);
            LOGGER.log(Level.SEVERE, "Deleting component from database failed: ", ex);
        } finally {
            closeSources(conn, st);
        }
    }

    @Override
    public Set<Configuration> findConfigurationByName(String name) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        if (name == null) throw new IllegalArgumentException("name is null");
        
        Set<Configuration> result = new TreeSet<>(Configuration.idComparator);
        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement("SELECT * "
                                     + "FROM database.configuration "
                                     + "WHERE name "
                                     + "LIKE ?");
            st.setString(1, "%"+name+"%");
            ResultSet resultSet = st.executeQuery();
            while(resultSet.next()){
                Configuration configuration = new Configuration();
                configuration.setId(resultSet.getLong("conf_id"));
                configuration.setName(resultSet.getString("name"));
                configuration.setCreator(resultSet.getString("creator"));
                Instant instant = Instant.ofEpochMilli(resultSet.getDate("creation_time").getTime());
                configuration.setCreationTime(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
                Instant instant2 = Instant.ofEpochMilli(resultSet.getDate("last_update").getTime());
                configuration.setLastUpdate(LocalDateTime.ofInstant(instant2, ZoneOffset.UTC));
                result.add(configuration);
            }
        } catch (SQLException ex){
            LOGGER.log(Level.SEVERE,"Error during getting configurations from database: ",ex);
        } finally {
            closeSources(conn, st);
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

    public static void testConfiguration(Configuration configuration) throws IllegalArgumentException {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration argument is null");
        }
        if (configuration.getCreator() == null){
            throw new IllegalArgumentException("Creator is null");
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
    
    private void closeSources(Connection connection, Statement statement)
    {
        if (statement != null)
        {
            try {
                statement.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Closing of statement failed: ", ex);
            }
        }
            
        if (connection != null) 
        {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error during switching autocommit to true: ", ex);
            }
            try {
                connection.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error closing connection: ", ex);
            }
        }
    }
    
    private void rollbackChanges(Connection connection) {
        if (connection != null)
        {
            try {
                if (connection.getAutoCommit()) throw new IllegalStateException("Connection is in autocommit mode!");
                connection.rollback();
            } catch (SQLException ex1) {
                LOGGER.log(Level.SEVERE, "Rollback of update failed: ", ex1);
            }
        }
    }
}
