package pcconfigurator.componentmanager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
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
        testComponent(component);
        if (component.getId() != null) throw new IllegalArgumentException("Component is already in database");
        
        PreparedStatement st = null;
        try
        {
            connection.setAutoCommit(false);
            st = connection.prepareStatement("INSERT INTO database.configuration (vendor, price, type, power, name) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
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
	public Component getComponentById(long id) {
		// TODO - implement ComponentManagerImpl.getComponentById
		throw new UnsupportedOperationException();
	}

        @Override
	public Set<Component> findAllComponents() {
		// TODO - implement ComponentManagerImpl.findAllComponents
		throw new UnsupportedOperationException();
	}

        @Override
	public void updateComponent(Component component) {
		// TODO - implement ComponentManagerImpl.updateComponent
		throw new UnsupportedOperationException();
	}

        @Override
	public void deleteComponent(Component component) {
		// TODO - implement ComponentManagerImpl.deleteComponent
		throw new UnsupportedOperationException();
	}

        @Override
	public Set<Component> findCompByType(ComponentTypes type) {
		// TODO - implement ComponentManagerImpl.findCompByType
		throw new UnsupportedOperationException();
	}
        
    private void testComponent(Component component) throws IllegalArgumentException
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