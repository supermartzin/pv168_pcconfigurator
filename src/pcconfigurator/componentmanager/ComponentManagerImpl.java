package pcconfigurator.componentmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcconfigurator.exception.*;

public class ComponentManagerImpl implements ComponentManager {
    
    public static final Logger logger = Logger.getLogger(ComponentManagerImpl.class.getName());
    private Connection connection;
    
    public ComponentManagerImpl(Connection connection)
    {
        this.connection = connection;
    }

    @Override
    public void createComponent(Component component)
    {
        checkComponent(component);
        if (component.getId() != null) throw new IllegalArgumentException("Component is already in database");
        
        PreparedStatement st = null;
        try
        {
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
            connection.setAutoCommit(true);
        } catch (SQLException | InternalFailureException ex)
        {
            if (connection != null)
            {
                try
                {
                    connection.rollback();
                } catch (SQLException ex1)
                {
                    logger.log(Level.SEVERE, "Rollback of update failed: ", ex1);
                }
            }
            logger.log(Level.SEVERE, "Inserting component into database failed: ", ex);
        } finally
        {
            if (st != null)
            {
                try
                {
                    st.close();
                } catch (SQLException ex)
                {
                    logger.log(Level.SEVERE, "Closing of statement failed: ", ex);
                }
            }
        }
    }

    @Override
    public Component getComponentById(Long id) {
	if (id == null) throw new IllegalArgumentException("id is null");
        
        PreparedStatement st = null;
        try
        {
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
            else throw new InternalFailureException("this ID does not exist");
        } catch (SQLException | IllegalArgumentException ex)
        {
            logger.log(Level.SEVERE, "Getting of component from database failed: ", ex);
            throw new InternalFailureException("Getting of component from database failed: ", ex);
        } finally
        {
            if (st != null)
            {
                try
                {
                    st.close();
                } catch (SQLException ex)
                {
                    logger.log(Level.SEVERE, "Closing of statement failed: ", ex);
                }
            }
        }
    }

    @Override
    public Set<Component> findAllComponents() {
        Set<Component> components = new TreeSet<>(Component.idComparator);
        PreparedStatement st = null;
        
        try {
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
            logger.log(Level.SEVERE, "Getting all components from database failed: ", ex);
        } finally {
            if (st != null)
            {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Closing of statement failed: ", ex);
                }
            }
        }
        
        return components;
    }

    @Override
    public void updateComponent(Component component) {
	checkComponent(component);
        PreparedStatement st = null;
        
        try
        {
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
            connection.setAutoCommit(true);
        } catch (SQLException | InternalFailureException ex) {
            if (connection != null)
            {
                try {
                    connection.rollback();
                } catch (SQLException ex1) {
                    logger.log(Level.SEVERE, "Rollback of update failed: ", ex1);
                }
            }
            
            logger.log(Level.SEVERE, "Updating component in database failed: ", ex);
        } finally {
            if (st != null)
            {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Closing of statement failed: ", ex);
                }
            }
        }
    }

    @Override
    public void deleteComponent(Component component) { 
        checkComponent(component);
        
        if (component.getId() == null) throw new IllegalArgumentException("component without ID");
        
        PreparedStatement st = null;
        try
        {
            connection.setAutoCommit(false);
            st = connection.prepareStatement("DELETE FROM database.component WHERE comp_id=?");
            st.setLong(1, component.getId());
            
            if (st.executeUpdate() != 1) throw new InternalFailureException("deleted more than one component form database");
            
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException | InternalFailureException ex) {
            if (connection != null)
            {
                try {
                    connection.rollback();
                } catch (SQLException ex1) {
                    logger.log(Level.SEVERE, "Rollback of update failed: ", ex1);
                }
            }
            
            logger.log(Level.SEVERE, "Deleting component from database failed: ", ex);
        } finally {
            if (st != null)
            {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Closing of statement failed: ", ex);
                }
            }
        }
    }

    @Override
    public Set<Component> findCompByType(ComponentTypes type) {
	if (type == null) throw new IllegalArgumentException("type is null");
        
        Set<Component> components = new TreeSet<>(Component.idComparator);
        PreparedStatement st = null;
        
        try
        {
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
            logger.log(Level.SEVERE, "Error during getting components from database: ", ex);
        } finally {
            if (st != null)
            {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Closing of statement failed: ", ex);
                }
            }
        }
        
        return components;
    }
        
    private void checkComponent(Component component) throws IllegalArgumentException
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
}