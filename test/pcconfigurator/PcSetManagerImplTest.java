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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pcconfigurator.componentmanager.Component;
import pcconfigurator.componentmanager.ComponentManagerImpl;
import pcconfigurator.componentmanager.ComponentTypes;
import pcconfigurator.configurationmanager.Configuration;
import pcconfigurator.configurationmanager.ConfigurationManagerImpl;
import pcconfigurator.exception.InternalFailureException;
import pcconfigurator.pcsetmanager.PcSet;
import pcconfigurator.pcsetmanager.PcSetManager;
import pcconfigurator.pcsetmanager.PcSetManagerImpl;


/**
 *
 * @author davidkaya
 */
public class PcSetManagerImplTest {
    
    private PcSetManager pcSetManager;
    public static final Logger LOGGER = Logger.getLogger(PcSetManagerImpl.class.getName());
    private static DataSource dataSource;
    private static String name;
    private static String password;
    private static String dbURL;
    
    private DataSource setDataSource()
    {
        BasicDataSource ds = new BasicDataSource();
        if (name != null && password != null && dbURL != null) 
        {
            ds.setUrl(dbURL);
            ds.setUsername(name);
            ds.setPassword(password);
        }
        else throw new InternalFailureException("cannot create DataSource, properties are empty");
        
        return ds;
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
            LOGGER.log(Level.SEVERE, "Reading property file failed: ", ex);
        } finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                } catch (IOException ex)
                {
                    LOGGER.log(Level.SEVERE, "Closing of input failed: ", ex);
                }
            }   
        }
    }
    
    @AfterClass
    public static void tearDownClass(){
        
    }
    
    @Before
    public void setUp() {
        dataSource = setDataSource();
        pcSetManager = new PcSetManagerImpl(dataSource);
        SqlScriptRunner sr = new SqlScriptRunner(dataSource, true, true);
        FileReader fr = null;
        try {
            fr = new FileReader("createTables.sql");
            try {
                sr.runScript(fr);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error during executing script: ", ex);
            }
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE,"Error during reading file: ",ex);
        } finally {
            if (fr != null)
            {
                try {
                    fr.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error during closing File Reader: ", ex);
                }
            }
        }
    }
    
    @After
    public void tearDown() {
        SqlScriptRunner sr = new SqlScriptRunner(dataSource, true, true);
        FileReader fr = null;
        try {
            fr = new FileReader("dropTables.sql");
            try {
                sr.runScript(fr);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error during executing script: ", ex);
            }
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Error during reading file: ", ex);
        } finally {
            if (fr != null)
            {
                try {
                    fr.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error during closing File Reader: ", ex);
                }
            }
        }
    }

    /**
     * Test of createPcSet method, of class PcSetManagerImpl.
     */
    @Test
    public void testCreatePcSet() {
        Configuration config = new Configuration("Test configuration","David Kaya");
        ConfigurationManagerImpl confManager = new ConfigurationManagerImpl(dataSource);
        confManager.createConfiguration(config);
        
        Component comp = new Component("Intel", (new BigDecimal("25.50")).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.MOTHERBOARD, 45, "Zakladna doska Intel");
        ComponentManagerImpl compManager = new ComponentManagerImpl(dataSource);
        compManager.createComponent(comp);
        
        PcSet expected = new PcSet(comp, config);
        pcSetManager.createPcSet(expected);
        PcSet result = pcSetManager.getPcSet(config, comp);
        
        assertNotSame("PcSets are the same objects",expected,result);
        assertEquals("PcSets does not equal",expected, result);
        
        try {
            PcSet wrongPcSet = new PcSet(comp, config, 5);
            pcSetManager.createPcSet(wrongPcSet);
            fail("You can't add 5 motherboards");
        } catch(IllegalArgumentException ex){            
        }
    }

    /**
     * Test of getPcSet method, of class PcSetManagerImpl.
     */
    @Test
    public void testGetPcSet() {
        Configuration config = new Configuration("Test configuration","David Kaya");
        Component comp = new Component("Intel", new BigDecimal("25.50"), ComponentTypes.MOTHERBOARD, 45, "Zakladna doska Intel");
        PcSet expected = new PcSet(comp, config);
        pcSetManager.createPcSet(expected);
        PcSet result = pcSetManager.getPcSet(config, comp);
        
        try{
            pcSetManager.getPcSet(new Configuration("Test","Steven Segal"), null);
            fail("This PcSet does not exist!");
        } catch(IllegalArgumentException ex){
        }
        
        assertNotSame("PcSets are the same objects",expected,result);
        assertEquals("PcSets does not equal",expected, result);
    }

    /**
     * Test of updatePcSet method, of class PcSetManagerImpl.
     */
    @Test
    public void testUpdatePcSet() {
        Configuration config = new Configuration("Test configuration","David Kaya");
        Component comp = new Component("Intel", new BigDecimal("25.50"), ComponentTypes.MOTHERBOARD, 45, "Zakladna doska Intel");     
        PcSet expected = new PcSet(comp, config);
        
        try{
            pcSetManager.updatePcSet(null);
            fail("Null pointer in update");
        } catch (IllegalArgumentException ex){
        }
        
        pcSetManager.createPcSet(expected);        
        expected.setConfiguration(new Configuration("Test configuration","Chuck Norris"));
        pcSetManager.updatePcSet(expected);     
        PcSet result = pcSetManager.getPcSet(config, comp);
        
        assertEquals("Wrong update",expected,result);        
    }

    /**
     * Test of deletePcSet method, of class PcSetManagerImpl.
     */
    @Test
    public void testDeletePcSet() {
        Configuration config = new Configuration("Test configuration","David Kaya");
        Component comp = new Component("Intel", new BigDecimal("25.50"), ComponentTypes.MOTHERBOARD, 45, "Zakladna doska Intel"); 
        Configuration config2 = new Configuration("New configuration","Chuck Norris");
        Component comp2 = new Component("Asus", new BigDecimal("50.50"), ComponentTypes.MOTHERBOARD, 35, "Zakladna doska Asus");  
        PcSet expected = new PcSet(comp, config);
        PcSet expected2 = new PcSet(comp2,config2);
        
        pcSetManager.createPcSet(expected);
        pcSetManager.deletePcSet(expected);
        
        try{
            pcSetManager.getPcSet(config, comp);
            fail("This PCSet should be deleted!");
        } catch (IllegalArgumentException ex){
        }
        
        try{
            pcSetManager.deletePcSet(expected2);
            fail("This pc set does not exist!");
        }catch(IllegalArgumentException ex){
        }
    }

    /**
     * Test of findConfigByComponent method, of class PcSetManagerImpl.
     */
    @Test
    public void testFindConfigByComponent() {
        Configuration config = new Configuration("Test configuration","David Kaya");
        Component comp = new Component("Intel", new BigDecimal("25.50"), ComponentTypes.MOTHERBOARD, 45, "Zakladna doska Intel"); 
        Configuration config2 = new Configuration("Test configuration","David Kaya");
        Component comp2 = new Component("Asus", new BigDecimal("50.50"), ComponentTypes.MOTHERBOARD, 35, "Zakladna doska Asus");  
        Component comp3 = new Component("Samsung",new BigDecimal("20.20"),ComponentTypes.MOTHERBOARD,26, "Zakladna doska Samsung");
        PcSet exp = new PcSet(comp, config);
        PcSet exp2 = new PcSet(comp2, config2);
        pcSetManager.createPcSet(exp);
        pcSetManager.createPcSet(exp2);
        
        assertEquals("Should be equal",config2,pcSetManager.findConfigByComponent(comp2));
        try{
            pcSetManager.findConfigByComponent(comp3);
            fail("Component is not in any configuration!");
        } catch (IllegalArgumentException ex){
        }
        
    }

    /**
     * Test of listCompsInConfiguration method, of class PcSetManagerImpl.
     */
    @Test
    public void testListCompsInConfiguration() {
        Configuration config = new Configuration("Test configuration","David Kaya");
        Component comp = new Component("Intel", new BigDecimal("25.50"), ComponentTypes.MOTHERBOARD, 45, "Zakladna doska Intel"); 
        Component comp2 = new Component("Asus", new BigDecimal("50.50"), ComponentTypes.CPU, 35, "CPU");  
        Component comp3 = new Component("Samsung",new BigDecimal("20.20"),ComponentTypes.GPU,26, "GPU");
        pcSetManager.createPcSet(new PcSet(comp,config));
        pcSetManager.createPcSet(new PcSet(comp2,config));
        pcSetManager.createPcSet(new PcSet(comp3,config));
        List<Component> compList = new ArrayList<>();
        Collections.sort(compList, idComparator);
        compList.add(comp);
        compList.add(comp2);
        compList.add(comp3);
        List<Component> result = pcSetManager.listCompsInConfiguration(config);
        Collections.sort(result, idComparator);
        
        assertEquals("List of components are not the same!",compList,result);
        
        
        
    }
    
        private static final Comparator<Component> idComparator = (Component o1, Component o2) 
            -> Long.valueOf(o1.getId()).compareTo(o2.getId());
}
