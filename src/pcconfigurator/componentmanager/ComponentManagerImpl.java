package pcconfigurator.componentmanager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import pcconfigurator.exception.*;

public class ComponentManagerImpl implements ComponentManager {
    
    public static final Logger LOGGER = Logger.getLogger(ComponentManagerImpl.class.getName());
    private final DataSource dataSource;
    
    public ComponentManagerImpl(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public ComponentManagerImpl() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("./credentials.properties");
            prop.load(input); 
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error opening credentials file: ", ex);
        } finally {
            this.dataSource = setDataSource(prop.getProperty("db_url"), prop.getProperty("name"), prop.getProperty("password"));
            if (input != null)
            {
                try {
                    input.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error closing input stream: ", ex);
                }
            }
        }
    }
    
    private DataSource setDataSource(final String dbUrl, final String name, final String password)
    {
        BasicDataSource ds = new BasicDataSource();
        if (name != null && password != null && dbUrl != null) 
        {
            ds.setUrl(dbUrl);
            ds.setUsername(name);
            ds.setPassword(password);
        }
        else throw new InternalFailureException("Cannot create DataSource, properties are empty!");
        
        return ds;
    }

    @Override
    public void createComponent(Component component)
    {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        
        checkComponent(component);
        if (component.getId() != null || isInDatabase(component)) throw new IllegalArgumentException("Component is already in database");
        
        PreparedStatement st = null;
        Connection connection = null;
        try
        {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            st = connection.prepareStatement("INSERT INTO database.component (vendor, price, type, power, name) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, component.getVendor());
            st.setBigDecimal(2, component.getPrice());
            st.setString(3, component.getType().name());
            st.setInt(4, component.getPower());
            st.setString(5, component.getName());
            
            if (st.executeUpdate() != 1) throw new InternalFailureException("wrong number of inserted items");
            
            // setting generated id 
            ResultSet keys = st.getGeneratedKeys();
            if (keys.next())
            {
                if (keys.getMetaData().getColumnCount() != 1) throw new InternalFailureException("wrong key fields count");
                Long result = keys.getLong(1);
                if (keys.next()) throw new InternalFailureException("setting ID - more keys found");
                
                component.setId(result);
            }
            else throw new InternalFailureException("setting ID - no key found");
            
            connection.commit();
        } catch (SQLException | InternalFailureException ex)
        {
            rollbackChanges(connection);
            LOGGER.log(Level.SEVERE, "Inserting component into database failed: ", ex);
        } finally
        {
            closeSources(connection, st);
        }
    }

    @Override
    public Component getComponentById(Long id) 
    {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
	if (id == null) throw new IllegalArgumentException("id is null");
        
        Connection connection = null;
        PreparedStatement st = null;
        try
        {
            connection = dataSource.getConnection();
            st = connection.prepareStatement("SELECT comp_id, vendor, price, type, power, name FROM database.component WHERE comp_id = ?");
            st.setLong(1, id);
            
            ResultSet rs = st.executeQuery();
            if (rs.next())
            {
                Component component = new Component();
                component.setId(rs.getLong("comp_id"));
                component.setVendor(rs.getString("vendor"));
                component.setPrice((rs.getBigDecimal("price")));
                component.setType(ComponentTypes.valueOf(rs.getString("type")));
                component.setPower(rs.getInt("power"));
                component.setName(rs.getString("name"));
                
                if (rs.next()) throw new InternalFailureException("more components with the same ID found");
                
                return component;
            }
            else throw new SQLException("this ID does not exist");
        } catch (SQLException | InternalFailureException ex)
        {
            LOGGER.log(Level.SEVERE, "Getting component from database failed: ", ex);
            throw new InternalFailureException("Getting component from database failed: ", ex);
        } finally
        {
            closeSources(connection, st);
        }
    }

    @Override
    public Set<Component> findAllComponents() {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        
        Set<Component> components = new TreeSet<>(Component.idComparator);
        
        Connection connection = null;
        PreparedStatement st = null;        
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement("SELECT comp_id, vendor, price, type, power, name FROM database.component");
            ResultSet rs = st.executeQuery();
            while (rs.next())
            {
                Component component = new Component();
                component.setId(rs.getLong("comp_id"));
                component.setVendor(rs.getString("vendor"));
                component.setPrice((rs.getBigDecimal("price")));
                component.setType(ComponentTypes.valueOf(rs.getString("type")));
                component.setPower(rs.getInt("power"));
                component.setName(rs.getString("name"));
                
                components.add(component);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Getting all components from database failed: ", ex);
        } finally {
            closeSources(connection, st);
        }
        
        return components;
    }

    @Override
    public void updateComponent(Component component) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        checkComponent(component);
        if (isInDatabase(component)) throw new IllegalArgumentException("Component is already in database.");
        
        Connection connection = null;
        PreparedStatement st = null;        
        try
        {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            st = connection.prepareStatement("UPDATE database.component SET vendor=?, price=?, type=?, power=?, name=? WHERE comp_id=?");
            st.setString(1, component.getVendor());
            st.setBigDecimal(2, component.getPrice());
            st.setString(3, component.getType().name());
            st.setInt(4, component.getPower());
            st.setString(5, component.getName());
            st.setLong(6, component.getId());
            
            if (st.executeUpdate() != 1) throw new InternalFailureException("Updated more than 1 component in database");
            
            connection.commit();
        } catch (SQLException | InternalFailureException ex) {
            rollbackChanges(connection);
            LOGGER.log(Level.SEVERE, "Updating component in database failed: ", ex);
        } finally {
            closeSources(connection, st);
        }
    }

    @Override
    public void deleteComponent(Component component) { 
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
        checkComponent(component);
        if (component.getId() == null) throw new IllegalArgumentException("component without ID");
        
        Connection connection = null;
        PreparedStatement st = null;
        try
        {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            st = connection.prepareStatement("DELETE FROM database.component WHERE comp_id=?");
            st.setLong(1, component.getId());
            
            if (st.executeUpdate() != 1) throw new InternalFailureException("deleted more than one component form database");
            
            connection.commit();
        } catch (SQLException | InternalFailureException ex) {
            rollbackChanges(connection);
            LOGGER.log(Level.SEVERE, "Deleting component from database failed: ", ex);
        } finally {
            closeSources(connection, st);
        }
    }

    @Override
    public Set<Component> findCompByType(ComponentTypes type) {
        if (this.dataSource == null) throw new IllegalStateException("DataSource is not set.");
	if (type == null) throw new IllegalArgumentException("type is null");
        
        Set<Component> components = new TreeSet<>(Component.idComparator);
        
        Connection connection = null;
        PreparedStatement st = null;        
        try
        {
            connection = dataSource.getConnection();
            st = connection.prepareStatement("SELECT * FROM database.component WHERE type LIKE ?");
            st.setString(1, type.name());
            
            ResultSet rs = st.executeQuery();
            while (rs.next())
            {
                Component component = new Component();
                
                component.setId(rs.getLong("comp_id"));
                component.setVendor(rs.getString("vendor"));
                component.setPrice(rs.getBigDecimal("price"));
                component.setType(ComponentTypes.valueOf(rs.getString("type")));
                component.setPower(rs.getInt("power"));
                component.setName(rs.getString("name"));
                
                components.add(component);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error during getting components from database: ", ex);
        } finally {
            closeSources(connection, st);
        }
        
        return components;
    }
        
    public static void checkComponent(Component component) throws IllegalArgumentException
    {
        if                   (component == null) throw new IllegalArgumentException("Component is null");
        if         (component.getName() == null) throw new IllegalArgumentException("Name of component is null");
        if       (component.getName().isEmpty()) throw new IllegalArgumentException("Name of component is empty");
        if            (component.getPower() < 0) throw new IllegalArgumentException("Power of component is negative");
        if        (component.getPrice() == null) throw new IllegalArgumentException("Price of component is null");
        if (component.getPrice().signum() == -1) throw new IllegalArgumentException("Price of component is negative");
        if         (component.getType() == null) throw new IllegalArgumentException("Type of component is null");
        if       (component.getVendor() == null) throw new IllegalArgumentException("Vendor of component is null");
        if     (component.getVendor().isEmpty()) throw new IllegalArgumentException("Vednor of component is empty");
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

    private boolean isInDatabase(Component component) {
        return findAllComponents().stream().anyMatch((comp) -> (comp.getName().equals(component.getName()) && 
                comp.getVendor().equals(component.getVendor()) &&
                comp.getType().equals(component.getType()) && 
                comp.getPower() == component.getPower() &&
                comp.getPrice().equals(component.getPrice())));
    }
}