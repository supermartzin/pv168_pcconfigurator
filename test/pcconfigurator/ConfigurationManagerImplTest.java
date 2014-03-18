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
        Configuration configuration = new Configuration("Test configuration","David Kaya");
        try{
            configManager.createConfiguration(null);
            fail("Created null configuration");
        } catch (IllegalArgumentException ex){
        }
        
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
        
        Configuration expResult = new Configuration("Test configuration","David Kaya");
        configManager.createConfiguration(expResult);
        Configuration result = configManager.getConfigurationById(1);
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
        Configuration configuration = new Configuration("First configuration","David Kaya");
        Configuration configuration2 = new Configuration("Second configuration","Steven Segal");
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
        
        
        configuration.setCreator("Chuck Norris");
        configManager.updateConfiguration(configuration);
        Configuration result = configManager.getConfigurationById(1);
        
        assertEquals("Configuration is not updated",configuration, result);
        assertEquals("This configuration should not be updated",configuration2,configManager.getConfigurationById(2));
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
        
        Set<Configuration> expResult = new HashSet<>();
        expResult.add(firstConfig);
        expResult.add(thirdConfig);
        
        String name = "configuration";
        Set<Configuration> result = configManager.findConfigurationByName(name);
        assertEquals("Filter by name does not work",expResult, result);       
    }
    
}
