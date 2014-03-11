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
    
    public ConfigurationManagerImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
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
        System.out.println("createConfiguration");
        Configuration configuration = new Configuration(1,"David Kaya");        
        
        ConfigurationManagerImpl instance = new ConfigurationManagerImpl();
        instance.createConfiguration(configuration);
        Configuration returnedConfiguration = instance.getConfigurationById(configuration.getId());
        
        assertNotSame("Objects are the same one.",configuration, instance.getConfigurationById(configuration.getId()));
        assertEquals("Objects does not equal",configuration, returnedConfiguration);
        
    }

    /**
     * Test of getConfigurationById method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testGetConfigurationById() {
        System.out.println("getConfigurationById");
        Long id = null;
        ConfigurationManagerImpl instance = new ConfigurationManagerImpl();
        Configuration expResult = null;
        Configuration result = instance.getConfigurationById(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAllConfigurations method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testFindAllConfigurations() {
        System.out.println("findAllConfigurations");
        ConfigurationManagerImpl instance = new ConfigurationManagerImpl();
        Configuration firstConfig = new Configuration(1,"David Kaya");
        Configuration secondConfig = new Configuration(2, "Steven Segal");
        Configuration thirdConfig = new Configuration(3, "Chuck Norris");
        instance.createConfiguration(firstConfig);
        instance.createConfiguration(secondConfig);
        instance.createConfiguration(thirdConfig);
        
        Set<Configuration> expResult = new HashSet<>();
        expResult.add(firstConfig);
        expResult.add(secondConfig);
        expResult.add(thirdConfig);
        
        Set<Configuration> result = instance.findAllConfigurations();
        
        assertEquals("Size of results are not the same",expResult.size(), result.size());
        assertEquals("Sets does not contain same configurations",expResult,result);
        
        
    }

    /**
     * Test of updateConfiguration method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testUpdateConfiguration() {
        System.out.println("updateConfiguration");
        Configuration configuration = null;
        ConfigurationManagerImpl instance = new ConfigurationManagerImpl();
        instance.updateConfiguration(configuration);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteConfiguration method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testDeleteConfiguration() {
        System.out.println("deleteConfiguration");
        Configuration configuration = new Configuration(1,"David Kaya");
        ConfigurationManagerImpl instance = new ConfigurationManagerImpl();
        instance.createConfiguration(configuration);
        instance.deleteConfiguration(configuration);
        
        try{
            Configuration tempConfig = instance.getConfigurationById(configuration.getId());
            fail("Configuration has not been deleted!");
        } catch (IndexOutOfBoundsException ex){
        }
    }

    /**
     * Test of findConfigurationByName method, of class ConfigurationManagerImpl.
     */
    @Test
    public void testFindConfigurationByName() {
        System.out.println("findConfigurationByName");
        String name = "";
        ConfigurationManagerImpl instance = new ConfigurationManagerImpl();
        Set<Configuration> expResult = null;
        Set<Configuration> result = instance.findConfigurationByName(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
