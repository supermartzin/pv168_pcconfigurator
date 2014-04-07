package pcconfigurator.pcsetmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import pcconfigurator.componentmanager.Component;
import pcconfigurator.componentmanager.ComponentManagerImpl;
import pcconfigurator.configurationmanager.Configuration;
import pcconfigurator.configurationmanager.ConfigurationManagerImpl;
import pcconfigurator.exception.InternalFailureException;

public class PcSetManagerImpl implements PcSetManager {

    public static final Logger LOGGER = Logger.getLogger(PcSetManagerImpl.class.getName());
    private final DataSource dataSource;
    
    public PcSetManagerImpl(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
    
    /**
     *
     * @param PcSet
     */
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

    /**
     *
     * @param Configuration
     * @param Component
     */
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
                
                PcSet pcSet = new PcSet(comp, conf, rs.getInt("quantity"));
                
                if (rs.next()) throw new InternalFailureException("more PC Sets found");
                
                return pcSet;                
            }
            else throw new SQLException("this PC Set does not exists");
        } catch (SQLException | InternalFailureException ex) {
            LOGGER.log(Level.SEVERE, "Getting PC Set from database failed: ", ex);
            throw new InternalFailureException("Getting PC Set from database failed: ");
        } finally {
            closeSources(connection, st);
        }
    }

    /**
     *
     * @param PcSet
     */
    @Override
    public void updatePcSet(PcSet pcSet) {
        // TODO - implement PcSetManagerImp.updatePcSet
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param PcSet
     */
    @Override
    public void deletePcSet(PcSet pcSet) {
        // TODO - implement PcSetManagerImp.deletePcSet
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param Component
     */
    @Override
    public List<Configuration> findConfigByComponent(Component component) {
        // TODO - implement PcSetManagerImp.findConfigByComponent ava
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param Configuration
     */
    @Override
    public List<Component> listCompsInConfiguration(Configuration configuration) {
        // TODO - implement PcSetManagerImp.listCompsInConfiguration
        throw new UnsupportedOperationException();
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
