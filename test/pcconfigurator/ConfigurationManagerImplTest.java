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
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
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
import pcconfigurator.configurationmanager.Configuration;
import pcconfigurator.configurationmanager.ConfigurationManager;
import pcconfigurator.configurationmanager.ConfigurationManagerImpl;
import pcconfigurator.exception.InternalFailureException;

/**
 *
 * @author davidkaya
 */
public class ConfigurationManagerImplTest {

    private static ConfigurationManager configManager;
    public static final Logger LOGGER = Logger.getLogger(ConfigurationManagerImpl.class.getName());
    private static DataSource dataSource;
    private static String name;
    private static String password;
    private static String dbUrl;
    
    private DataSource setDataSource()
    {
        BasicDataSource ds = new BasicDataSource();
        if (name != null && password != null && dbUrl != null) 
        {
            ds.setUrl(dbUrl);
            ds.setUsername(name);
            ds.setPassword(password);
        }
        else throw new InternalFailureException("cannot create DataSource, properties are empty");
        
        return ds;
    }
    
    @BeforeClass
    public static void setUpClass(){        
        Properties prop = new Properties();
        InputStream input = null;
        try{
            input = new FileInputStream("./test/pcconfigurator/test_credentials.properties");
            prop.load(input);
            dbUrl = prop.getProperty("db_url");
            name = prop.getProperty("name");
            password = prop.getProperty("password");
        } catch (IOException ex){
            LOGGER.log(Level.SEVERE,null,ex);
        } finally{
            if(input!=null){
                try{
                    input.close();
                } catch (IOException e){
                    LOGGER.log(Level.SEVERE,null,e);
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
        configManager = new ConfigurationManagerImpl(dataSource);
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
     * Test of createConfiguration method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testCreateConfiguration() {
        Configuration configuration = new Configuration("Test configuration","David Kaya");
        try{
            configManager.createConfiguration(null);
            fail("Created null configuration");
        } catch (IllegalArgumentException ex){
        }
        configManager.createConfiguration(configuration);
        assertNotNull("ID is null",configuration.getId());
                
        Configuration returnedConfiguration = configManager.getConfigurationById(configuration.getId());
        
        assertNotSame("Objects are the same one.",configuration, configManager.getConfigurationById(configuration.getId()));
        assertEquals("Objects does not equal",configuration, returnedConfiguration); 
       
    }

    /**
     * Test of getConfigurationById method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testGetConfigurationById() {
        try{
            configManager.getConfigurationById(new Long(10000));
            fail("Configuration with ID 10000 does not exist a has been returned!");
        } catch (IllegalArgumentException ex){   
        }
        
        Configuration expResult = new Configuration("Test configuration","David Kaya");
        configManager.createConfiguration(expResult);
        Configuration result = configManager.getConfigurationById(expResult.getId());
        assertEquals("Wrong configuration has been returned!",expResult, result);
    }

    /**
     * Test of findAllConfigurations method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testFindAllConfigurations() {
        Configuration firstConfig = new Configuration("First configuration","David Kaya");
        Configuration secondConfig = new Configuration("Second configuration", "Steven Segal");
        Configuration thirdConfig = new Configuration("Third configuration", "Chuck Norris");
        configManager.createConfiguration(firstConfig);
        configManager.createConfiguration(secondConfig);
        configManager.createConfiguration(thirdConfig);
        
        Set<Configuration> expResult = new TreeSet<>(Configuration.idComparator);
        expResult.add(firstConfig);
        expResult.add(secondConfig);
        expResult.add(thirdConfig);
        
        Set<Configuration> result = configManager.findAllConfigurations();
        
        assertEquals("Size of results are not the same",expResult.size(), result.size());
        assertEquals("Sets does not contain same configurations",expResult,result);
    }

    /**
     * Test of updateConfiguration method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testUpdateConfiguration() {
        Configuration configuration = new Configuration("First configuration","David Kaya");
        Configuration configuration2 = new Configuration("Second configuration","Steven Segal");
        configManager.createConfiguration(configuration);
        configManager.createConfiguration(configuration2);
        try{
            configManager.updateConfiguration(null);
            fail("Null argument in update!");
        } catch(IllegalArgumentException ex){            
        }
        
        try{
            configuration.setName(null);   
            configManager.updateConfiguration(configuration);
            fail("Null argument in name!");
        }catch (IllegalArgumentException ex){
        }
     
        
        try{          
            configuration.setCreator(null);
            configManager.updateConfiguration(configuration);
            fail("Null argument in creator!");
        }catch (IllegalArgumentException ex){
        }
        
        configuration.setName("First configuration");
        configuration.setCreator("Chuck Norris");
        configManager.updateConfiguration(configuration);
        Configuration result = configManager.getConfigurationById(configuration.getId());
        
        assertEquals("Configuration is not updated",configuration, result);
        assertEquals("This configuration should not be updated",configuration2,configManager.getConfigurationById(configuration2.getId()));
    }

    /**
     * Test of deleteConfiguration method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testDeleteConfiguration() {         
        Configuration configuration = new Configuration("First Configuration","David Kaya");
        Configuration configuration2 = new Configuration("Second Configuration","Chuck Norris");
        Configuration configuration3 = new Configuration("Third Configuration","Steven Segal");
        configManager.createConfiguration(configuration);
        configManager.createConfiguration(configuration2);
        
        configManager.deleteConfiguration(configuration);
        
        assertEquals("Size is not same! Should be 1",1,configManager.findAllConfigurations().size());
        assertEquals("Configuration has not been found in DB",configuration2, configManager.getConfigurationById(configuration2.getId()));
   
        try{
            Configuration tempConfig = configManager.getConfigurationById(configuration.getId());
            fail("Configuration has not been deleted!");
        } catch (IllegalArgumentException ex){
        }
        try{
            configManager.deleteConfiguration(configuration3);
            fail("Removed nonexisting configuration");
        } catch (IllegalArgumentException ex){
        }
        
    }

    /**
     * Test of findConfigurationByName method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testFindConfigurationByName() {
        Configuration firstConfig = new Configuration("First configuration","David Kaya");
        Configuration secondConfig = new Configuration("Second", "Steven Segal");
        Configuration thirdConfig = new Configuration("configuration", "Chuck Norris");
        configManager.createConfiguration(firstConfig);
        configManager.createConfiguration(secondConfig);
        configManager.createConfiguration(thirdConfig);
        
        Set<Configuration> expResult = new TreeSet<>(Configuration.idComparator);
        expResult.add(firstConfig);
        expResult.add(thirdConfig);
        
        String configName = "configuration";
        Set<Configuration> result = configManager.findConfigurationByName(configName);
        assertEquals("Filter by name does not work",expResult, result);  
        
        String name2 = "test";
        Set<Configuration> result2 = configManager.findConfigurationByName(name2);
        assertEquals("Result should by empty",0,result2.size());
    }
    
}
