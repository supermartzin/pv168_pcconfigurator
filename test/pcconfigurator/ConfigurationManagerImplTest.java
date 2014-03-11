/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pcconfigurator;

import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author davidkaya
 */
public class ConfigurationManagerImplTest {
    
    private ConfigurationManager configManager;
    
    @Before
    public void setUp() {
        configManager = new ConfigurationManagerImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createConfiguration method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testCreateConfiguration() {
        Configuration configuration = new Configuration(1,"Test configuration","David Kaya");        
        assertNotNull("ID is null",configuration.getId());
        
        configManager.createConfiguration(configuration);
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
            configManager.getConfigurationById(10);
            fail("Configuration with ID 10 does not exist a has been returned!");
        } catch (IllegalArgumentException ex){   
        }
        
        long id = 1;
        Configuration expResult = new Configuration(id,"Test configuration","David Kaya");
        configManager.createConfiguration(expResult);
        Configuration result = configManager.getConfigurationById(id);
        assertEquals("Wrong configuration has been returned!",expResult, result);
    }

    /**
     * Test of findAllConfigurations method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testFindAllConfigurations() {
        Configuration firstConfig = new Configuration(1, "First configuration","David Kaya");
        Configuration secondConfig = new Configuration(2, "Second configuration", "Steven Segal");
        Configuration thirdConfig = new Configuration(3, "Third configuration", "Chuck Norris");
        configManager.createConfiguration(firstConfig);
        configManager.createConfiguration(secondConfig);
        configManager.createConfiguration(thirdConfig);
        
        Set<Configuration> expResult = new HashSet<>();
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
        Configuration configuration = new Configuration(1,"First configuration","David Kaya");
        configManager.createConfiguration(configuration);
        
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
        
        configuration.setName("New Configuration");
        configuration.setCreator("Chuck Norris");
        configManager.updateConfiguration(configuration);
        Configuration result = configManager.getConfigurationById(configuration.getId());
        
        assertEquals(configuration, result);
    }

    /**
     * Test of deleteConfiguration method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testDeleteConfiguration() {        
        Configuration configuration = new Configuration(1,"First Configuration","David Kaya");
        
        configManager.createConfiguration(configuration);
        configManager.deleteConfiguration(configuration);
        
        try{
            Configuration tempConfig = configManager.getConfigurationById(configuration.getId());
            fail("Configuration has not been deleted!");
        } catch (IllegalArgumentException ex){
        }
    }

    /**
     * Test of findConfigurationByName method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testFindConfigurationByName() {
        Configuration firstConfig = new Configuration(1, "First configuration","David Kaya");
        Configuration secondConfig = new Configuration(2, "Second", "Steven Segal");
        Configuration thirdConfig = new Configuration(3, "configuration", "Chuck Norris");
        configManager.createConfiguration(firstConfig);
        configManager.createConfiguration(secondConfig);
        configManager.createConfiguration(thirdConfig);
        
        Set<Configuration> expResult = new HashSet<>();
        expResult.add(firstConfig);
        expResult.add(thirdConfig);
        
        String name = "configuration";
        Set<Configuration> result = configManager.findConfigurationByName(name);
        assertEquals("Filter by name does not work",expResult, result);       
    }
    
}
