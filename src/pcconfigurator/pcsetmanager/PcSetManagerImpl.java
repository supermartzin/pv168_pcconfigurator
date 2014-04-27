package pcconfigurator.pcsetmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import pcconfigurator.componentmanager.Component;
import pcconfigurator.componentmanager.ComponentManager;
import pcconfigurator.componentmanager.ComponentManagerImpl;
import pcconfigurator.configurationmanager.Configuration;
import pcconfigurator.configurationmanager.ConfigurationManager;
import pcconfigurator.configurationmanager.ConfigurationManagerImpl;
import pcconfigurator.exception.InternalFailureException;

public class PcSetManagerImpl implements PcSetManager {

    public static final Logger LOGGER = Logger.getLogger(PcSetManagerImpl.class.getName());
    private final DataSource dataSource;
    
    public PcSetManagerImpl(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Override
    public void createPcSet(PcSet pcSet) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        
        checkPcSet(pcSet);
        try {
            this.getPcSet(pcSet.getConfiguration(), pcSet.getComponent());
            throw new IllegalArgumentException("This PC Set already exists.");
        } catch (InternalFailureException ex) {            
        }
        
        PreparedStatement st = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(true);
            st = connection.prepareStatement("INSERT INTO database.pcset (comp_id, conf_id, quantity) VALUES (?,?,?)");
            st.setLong(1, pcSet.getComponent().getId());
            st.setLong(2, pcSet.getConfiguration().getId());
            st.setInt(3, pcSet.getNumberOfComponents());
            
            if (st.executeUpdate() != 1) throw new InternalFailureException("wrong number of inserted items");
            
            connection.commit();
        } catch (SQLException | InternalFailureException ex) {
            rollbackChanges(connection);
            LOGGER.log(Level.SEVERE, "Inserting PC Set into database failed: ", ex);
        } finally {
            closeSources(connection, st);
        }
    }

    @Override
    public PcSet getPcSet(Configuration configuration, Component component) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        
        ComponentManagerImpl.checkComponent(component);
        ConfigurationManagerImpl.testConfiguration(configuration);
        if (component.getId() == null) throw new IllegalArgumentException("id of component is null");
        if (configuration.getId() == null) throw new IllegalArgumentException("id of configuration is null");
        
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement("SELECT comp_id, conf_id, quantity FROM database.pcset WHERE comp_id=? AND conf_id=?");
            st.setLong(1, component.getId());
            st.setLong(2, configuration.getId());
            
            ResultSet rs = st.executeQuery();
            if (rs.next())
            {
                ComponentManagerImpl compManager = new ComponentManagerImpl(dataSource);
                Long comp_id = rs.getLong("comp_id");
                Component comp = compManager.getComponentById(comp_id);
                
                ConfigurationManagerImpl confManager = new ConfigurationManagerImpl(dataSource);
                Long conf_id = rs.getLong("conf_id");
                Configuration conf = confManager.getConfigurationById(conf_id);
                
                int quantity = rs.getInt("quantity");
                PcSet pcSet = new PcSet(comp, conf, quantity);
                
                if (rs.next()) throw new InternalFailureException("more PC Sets found");
                
                return pcSet;                
            }
            else throw new SQLException("this PC Set does not exists");
        } catch (SQLException | InternalFailureException ex) {
            LOGGER.log(Level.SEVERE, "Getting PC Set from database failed: ", ex);
            throw new InternalFailureException("Getting PC Set from database failed: ",ex);
        } finally {
            closeSources(connection, st);
        }
    }

    @Override
    public void updatePcSet(PcSet pcSet) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        checkPcSet(pcSet);
        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement("UPDATE database.pcset "
                                     + "SET quantity=? "
                                     + "WHERE conf_id=? AND comp_id=?");            
            st.setLong(1, pcSet.getNumberOfComponents());
            st.setLong(2, pcSet.getConfiguration().getId());
            st.setLong(3, pcSet.getComponent().getId());
            if (st.executeUpdate() != 1) {
                throw new InternalFailureException("Updated more than 1 record");
            }
            conn.commit();
        } catch (SQLException | InternalFailureException ex) {
            rollbackChanges(conn);
            LOGGER.log(Level.SEVERE, "Updating PCSET in database failed: ", ex);
        } finally {
            closeSources(conn, st);
        }
    }

    @Override
    public void deletePcSet(PcSet pcSet) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        checkPcSet(pcSet);
        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement("DELETE FROM database.pcset "                                     
                                     + "WHERE conf_id=? AND comp_id=?");                        
            st.setLong(1, pcSet.getConfiguration().getId());
            st.setLong(2, pcSet.getComponent().getId());
            if (st.executeUpdate() != 1) {
                throw new InternalFailureException("Updated more than 1 record");
            }
            conn.commit();
        } catch (SQLException | InternalFailureException ex) {
            rollbackChanges(conn);
            LOGGER.log(Level.SEVERE, "Deleting PCSET in database failed: ", ex);
        } finally {
            closeSources(conn, st);
        }
    }

    @Override
    public Set<Configuration> findConfigByComponent(Component component) {
        if (this.dataSource == null) 
            throw new IllegalStateException("DataSource is not set.");
        if (component.getId() == null) 
            throw new IllegalArgumentException("id of component is null");
        ComponentManagerImpl.checkComponent(component);
        
        Set<Configuration> result = new TreeSet<>(Configuration.idComparator);
        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement("SELECT * "
                                     + "FROM database.pcset "
                                     + "WHERE comp_id=? ");
            st.setLong(1, component.getId());
            ResultSet resultSet = st.executeQuery();
            while(resultSet.next()){
                ConfigurationManager configManager = new ConfigurationManagerImpl(dataSource);                
                Configuration configuration = configManager.getConfigurationById(resultSet.getLong("conf_id"));                
                result.add(configuration);
            }
        } catch (SQLException ex){
            LOGGER.log(Level.SEVERE,"Error during getting configurations from database: ",ex);
        } finally {
            closeSources(conn, st);
        }
        
        return result;
    }

    @Override
    public Map<Component,Integer> listCompsInConfiguration(Configuration configuration) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        
        ConfigurationManagerImpl.testConfiguration(configuration);
        if (configuration.getId() == null) throw new IllegalArgumentException("id of configuration is null");
        
        Map<Component,Integer> components = new TreeMap<>(Component.idComparator);
        
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement("SELECT comp_id, quantity FROM database.pcset WHERE conf_id=?");
            st.setLong(1, configuration.getId());
            
            ResultSet rs = st.executeQuery();
            while(rs.next())
            {
                Long comp_id = rs.getLong("comp_id");
                ComponentManager compManager = new ComponentManagerImpl(dataSource);
                Component component = compManager.getComponentById(comp_id);
                
                int quantity = rs.getInt("quantity");
                
                components.put(component, quantity);
            }
        } catch (SQLException | InternalFailureException ex) {
            LOGGER.log(Level.SEVERE, "Error during getting components in selected configuration: ", ex);
        } finally {
            closeSources(connection, st);
        }
        
        return components;
    }
    
    private void checkPcSet(PcSet pcSet) throws IllegalArgumentException
    {
        // PC Set
        if                                  (pcSet == null) throw new IllegalArgumentException("PC Set is null");
        if                   (pcSet.getComponent() == null) throw new IllegalArgumentException("Component is null");
        if               (pcSet.getConfiguration() == null) throw new IllegalArgumentException("Configuration is null");
        if              (pcSet.getNumberOfComponents() < 0) throw new IllegalArgumentException("Invalid number of components in PC Set");
        
        // Component
        if           (pcSet.getComponent().getId() == null) throw new IllegalArgumentException("ID of component is null");
        if         (pcSet.getComponent().getName() == null) throw new IllegalArgumentException("Name of component is null");
        if       (pcSet.getComponent().getName().isEmpty()) throw new IllegalArgumentException("Name of component is empty");
        if            (pcSet.getComponent().getPower() < 0) throw new IllegalArgumentException("Power of component is negative");
        if        (pcSet.getComponent().getPrice() == null) throw new IllegalArgumentException("Price of component is null");
        if (pcSet.getComponent().getPrice().signum() == -1) throw new IllegalArgumentException("Price of component is negative");
        if         (pcSet.getComponent().getType() == null) throw new IllegalArgumentException("Type of component is null");
        if       (pcSet.getComponent().getVendor() == null) throw new IllegalArgumentException("Vendor of component is null");
        if     (pcSet.getComponent().getVendor().isEmpty()) throw new IllegalArgumentException("Vednor of component is empty");
        
        // Configuration
        if           (pcSet.getConfiguration().getId() == null) throw new IllegalArgumentException("ID of configuration is null");
        if      (pcSet.getConfiguration().getCreator() == null) throw new IllegalArgumentException("Creator is null");
        if         (pcSet.getConfiguration().getName() == null) throw new IllegalArgumentException("Name is null");
        if    (pcSet.getConfiguration().getCreator().isEmpty()) throw new IllegalArgumentException("Configuration does not have a creator.");
        if       (pcSet.getConfiguration().getName().isEmpty()) throw new IllegalArgumentException("Configuration does not have a name.");        
        if (pcSet.getConfiguration().getCreationTime() == null) throw new IllegalArgumentException("Time is null");
        if   (pcSet.getConfiguration().getLastUpdate() == null) throw new IllegalArgumentException("Last update time is null");
        
        // valid quantity of components
        if (pcSet.getComponent().getType().name().equals("CPU") && pcSet.getNumberOfComponents() > 1) 
                    throw new IllegalArgumentException("Cannot add more CPUs to one configuration");
        if (pcSet.getComponent().getType().name().equals("MOTHERBOARD") && pcSet.getNumberOfComponents() > 1)
                    throw new IllegalArgumentException("Cannot add more motherboards to one configuration");
        if (pcSet.getComponent().getType().name().equals("CASE") && pcSet.getNumberOfComponents() > 1)
                    throw new IllegalArgumentException("Cannot add more cases to one configuration");
        if (pcSet.getComponent().getType().name().equals("POWER_SUPPLY") && pcSet.getNumberOfComponents() > 1)
                    throw new IllegalArgumentException("Cannot add more power supplies to one configuration");
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
