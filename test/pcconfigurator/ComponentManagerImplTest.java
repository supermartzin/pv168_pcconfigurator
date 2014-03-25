/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pcconfigurator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pcconfigurator.componentmanager.Component;
import pcconfigurator.componentmanager.ComponentManagerImpl;
import pcconfigurator.componentmanager.ComponentTypes;
import pcconfigurator.exception.InternalFailureException;

/**
 *
 * @author Martin Vrabel
 */
public class ComponentManagerImplTest {
    
    private static ComponentManagerImpl compManager;
    public static final Logger logger = Logger.getLogger(ComponentManagerImpl.class.getName());
    private static Connection connection;
    private static String name;
    private static String password;
    private static String dbURL;
    
    public ComponentManagerImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Properties properties = new Properties();
        InputStream input = null;
        try
        {
            input = new FileInputStream("./test/pcconfigurator/test_credentials.properties");
            properties.load(input);
            dbURL = properties.getProperty("db_url");
            name = properties.getProperty("name");
            password = properties.getProperty("password");
        } catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Reading property file failed: ", ex);
        } finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                } catch (IOException ex)
                {
                    logger.log(Level.SEVERE, "Closing of input failed: ", ex);
                }
            }   
        }
        
        // create connection
        try
        {
            if (name != null && password != null && dbURL != null) connection = DriverManager.getConnection("jdbc:derby://localhost:1527/pcconfiguration_test", name, password);
            else throw new InternalFailureException("property file is empty");
        } catch (SQLException | InternalFailureException ex)
        {
            logger.log(Level.SEVERE, "Connecting to database failed: ", ex);
        }
        
        compManager = new ComponentManagerImpl(connection);
    }
    
    @AfterClass
    public static void tearDownClass() {
        // close connection
        if (connection != null)
            try {
                connection.close();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error during closing connection to database: ", ex);
            }
    } 
    
    @Before
    public void setUp() {
        SqlScriptRunner sr = new SqlScriptRunner(connection, true, true);
        FileReader fr = null;
        try {
            fr = new FileReader("createTables.sql");
            try {
                sr.runScript(fr);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error during executing script: ", ex);
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE,"Error during reading file: ",ex);
        } finally {
            if (fr != null)
            {
                try {
                    fr.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Error during closing File Reader: ", ex);
                }
            }
        }        
    }
    
    @After
    public void tearDown() {
        SqlScriptRunner sr = new SqlScriptRunner(connection, true, true);
        FileReader fr = null;
        try {
            fr = new FileReader("dropTables.sql");
            try {
                sr.runScript(fr);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error during executing script: ", ex);
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "Error during reading file: ", ex);
        } finally {
            if (fr != null)
            {
                try {
                    fr.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Error during closing File Reader: ", ex);
                }
            }
        }
    } 

    /**
     * Test of createComponent method, of class ComponentManagerImpl.
     */
    @Test
    public void testCreateComponent() {
        // test validneho komponentu
        Component component = new Component("Intel", (new BigDecimal(25.50)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.MOTHERBOARD, 45, "Zakladna doska Intel");
        compManager.createComponent(component);
        
        long compID = component.getId();
        assertNotNull("id of component is null", compID);
        
        Component result = compManager.getComponentById(compID);
        assertFullEquals("components do not match: ", component, result);
        assertNotSame("components must not be the same objects", component, result);        
        assertEquals("number of components in set do not match", 1, compManager.findAllComponents().size());
        
        // test invalidneho komponentu
        try
        {
            compManager.createComponent(null);
            fail("cannot add null component, exception must be thrown");
        } catch (IllegalArgumentException ex) { }  
    }

    /**
     * Test of getComponentById method, of class ComponentManagerImpl.
     */
    @Test
    public void testGetComponentById() {
        // test validneho komponentu
        Component component = new Component("ASUS", (new BigDecimal(89.95)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.MOTHERBOARD, 38, "Zakladna doska ASUS");
        compManager.createComponent(component);
        
        Component result = compManager.getComponentById(component.getId());
        
        assertFullEquals("components do not match", component, result);
        assertNotSame("components must not be the same objects", component, result);
        
        // test invalidneho komponentu
        try
        {
            Component componentById = compManager.getComponentById((long) -5);
            fail("ID cannot be negative number, exception must be thrown");
        } catch(IllegalArgumentException | InternalFailureException ex) { }   
    }

    /**
     * Test of findAllComponents method, of class ComponentManagerImpl.
     */
    @Test
    public void testFindAllComponents() {
        // test prazdneho zoznamu komponentov
        ComponentManagerImpl compManagerEmpty = new ComponentManagerImpl(connection);
        assertTrue("set of components should be empty", compManagerEmpty.findAllComponents().isEmpty());
        
        // test neprazdneho zoznamu komponentov
        Component comp1 = new Component("AMD Graphics", (new BigDecimal(149.80)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.GPU, 250, "R9 290X");
        Component comp2 = new Component("Creative", (new BigDecimal(24.00)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.SOUNDCARD, 15, "SoundBlaster S150");
        Component comp3 = new Component("Kingston", (new BigDecimal(37.50)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.RAM, 15, "DDR3 Memory 1600M");        
        compManager.createComponent(comp1);
        compManager.createComponent(comp2);
        compManager.createComponent(comp3);
        
        Set<Component> comps = new TreeSet<>(Component.idComparator);
        comps.add(comp1);
        comps.add(comp2);
        comps.add(comp3);
        
        Set<Component> result = new TreeSet<>(Component.idComparator);
        result.addAll(compManager.findAllComponents());
        
        assertEquals("expected number of components does not match", comps.size(), result.size());
        assertEquals("components do not match", comps, result);
        
        Iterator iterA = comps.iterator();
        Iterator iterB = result.iterator();
        while (iterA.hasNext() && iterB.hasNext())
        {
            Component nextA = (Component) iterA.next();
            Component nextB = (Component) iterB.next();
            assertFullEquals("components do not match: ", nextA, nextB);
            assertNotSame("components must not be the same objects", nextA, nextB);
        }
    }

    /**
     * Test of updateComponent method, of class ComponentManagerImpl.
     */
    @Test
    public void testUpdateComponent() {
        Component comp1 = new Component("AMD Graphics", (new BigDecimal(149.80)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.GPU, 250, "R9 290X");
        Component comp2 = new Component("Creative", (new BigDecimal(24.00)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.SOUNDCARD, 15, "SoundBlaster S150");
        compManager.createComponent(comp1);
        compManager.createComponent(comp2);
        
        long compID = comp1.getId();
        
        try
        {
            compManager.updateComponent(null);
            fail("cannot update null component, exception must be thrown");
        } catch (IllegalArgumentException ex) {}
        
        try
        {
            Component comp = compManager.getComponentById(compID);
            comp.setName(null);
            compManager.updateComponent(comp);
            fail("cannot update component with null name, exception must be thrown");
        } catch (IllegalArgumentException ex) {}
        
        try
        {
            Component comp = compManager.getComponentById(compID);
            comp.setPower(-150);
            compManager.updateComponent(comp);
            fail("cannot update component with negative power, exception must be thrown");
        } catch (IllegalArgumentException ex) {}
        
        try
        {
            Component comp = compManager.getComponentById(compID);
            comp.setPrice((new BigDecimal(-149.80)).setScale(2, BigDecimal.ROUND_HALF_UP));
            compManager.updateComponent(comp);
            fail("cannot update component with negative price, exception must be thrown");
        } catch (IllegalArgumentException ex) {}
        
        try
        {
            Component comp = compManager.getComponentById(compID);
            comp.setVendor(null);
            compManager.updateComponent(comp);
            fail("cannot update component with null vendor, exception must be thrown");
        } catch (IllegalArgumentException ex) {}
        
        Component comp = compManager.getComponentById(compID);
        comp.setName("Zakladna doska P3X2");
        comp.setPower(23);
        comp.setPrice((new BigDecimal(124)).setScale(2, BigDecimal.ROUND_HALF_UP));
        comp.setType(ComponentTypes.MOTHERBOARD);
        comp.setVendor("Intel");
        
        compManager.updateComponent(comp);
        Component result = compManager.getComponentById(comp.getId());
        
        assertEquals("name do not match", "Zakladna doska P3X2", result.getName());
        assertEquals("power do not match", 23, result.getPower());
        assertEquals("price do not match", (new BigDecimal(124)).setScale(2, BigDecimal.ROUND_HALF_UP), result.getPrice());
        assertEquals("type do not match", ComponentTypes.MOTHERBOARD, result.getType());
        assertEquals("vendor do not match", "Intel", result.getVendor());
        
        // test ci sa updatom nezmenili ine komponenty
        Component component = compManager.getComponentById(comp2.getId());
        assertFullEquals("component should not be modified by updating other components: ", comp2, component);
        
    }

    /**
     * Test of deleteComponent method, of class ComponentManagerImpl.
     */
    @Test
    public void testDeleteComponent() {
        // vymazanie validneho komponentu
        Component comp1 = new Component("AMD Graphics", (new BigDecimal(149.80)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.GPU, 250, "R9 290X");
        Component comp2 = new Component("Creative", (new BigDecimal(24.00)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.SOUNDCARD, 15, "SoundBlaster S150");
        compManager.createComponent(comp1);
        compManager.createComponent(comp2);
        
        assertNotNull("id cannot be null", comp1.getId());
        assertNotNull("id cannot be null", comp2.getId());
        
        compManager.deleteComponent(comp1);
        try {
            Component deletedComp = compManager.getComponentById(comp1.getId());
            fail("Component with this ID should be deleted and no longer in database.");
        } catch (InternalFailureException ex) {
            
        }
        
        assertNotNull("component should not be modified by deleting other components", compManager.getComponentById(comp2.getId()));
        assertFullEquals("component should not be modified by deleting other components: ", comp2, compManager.getComponentById(comp2.getId()));
        
        // vymazanie invalidnych komponentov
        Component comp3 = new Component("Kingston", (new BigDecimal(37.50)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.RAM, 15, "DDR3 Memory 1600M");
        compManager.createComponent(comp3);
        
        try
        {
            compManager.deleteComponent(null);
            fail("cannot delete null component, exception should be thrown");
        } catch (IllegalArgumentException ex) { }
        
        try
        {
            comp3.setId(null);
            compManager.deleteComponent(comp3);
            fail("cannot delete component with null id, exception must be thrown");
        } catch (IllegalArgumentException ex) { }
    }

    /**
     * Test of findCompByType method, of class ComponentManagerImpl.
     */
    @Test
    public void testFindCompByType() {
        Component comp1 = new Component("AMD Graphics", (new BigDecimal(149.80)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.GPU, 250, "R9 290X");
        Component comp2 = new Component("Creative", (new BigDecimal(24.00)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.SOUNDCARD, 15, "SoundBlaster S150");
        Component comp3 = new Component("Kingston", (new BigDecimal(37.50)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.RAM, 15, "DDR3 Memory 1600M");
        Component comp4 = new Component("Intel", (new BigDecimal("25.50")).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.MOTHERBOARD, 45, "Zakladna doska Intel");
        Component comp5 = new Component("AMD", (new BigDecimal("37.90")).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.MOTHERBOARD, 39, "Základná doska AMD 760G/SB710");
        compManager.createComponent(comp1);
        compManager.createComponent(comp2);
        compManager.createComponent(comp3);
        compManager.createComponent(comp4);
        compManager.createComponent(comp5);
        
        Set<Component> expected = new TreeSet<>(Component.idComparator);
        expected.add(comp4);
        expected.add(comp5);
        
        Set<Component> result = new TreeSet<>(Component.idComparator);
        result.addAll(compManager.findCompByType(ComponentTypes.MOTHERBOARD));
        
        assertEquals("number of components do not match", expected.size(), result.size());
        Iterator iterA = expected.iterator();
        Iterator iterB = result.iterator();
        while (iterA.hasNext() && iterB.hasNext())
        {
            Component nextA = (Component) iterA.next();
            Component nextB = (Component) iterB.next();
            assertFullEquals("components do not match: ", nextA, nextB);
            assertNotSame("object must not be the same", nextA, nextB);
        }     
    }
    
    private void assertFullEquals(String message, Component expected, Component actual)
    {
        assertEquals(message + "ids do not match", expected.getId(), actual.getId());
        assertEquals(message + "vendor's name do not match", expected.getVendor(), actual.getVendor());
        assertEquals(message + "price do not match", expected.getPrice(), actual.getPrice());
        assertEquals(message + "type do not match", expected.getType(), actual.getType());
        assertEquals(message + "power do not match", expected.getPower(), actual.getPower());
        assertEquals(message + "name do not match", expected.getName(), actual.getName());
    }
    
    private void assertFullEquals(Component expected, Component actual)
    {
        assertFullEquals("", expected, actual);
    }
}
