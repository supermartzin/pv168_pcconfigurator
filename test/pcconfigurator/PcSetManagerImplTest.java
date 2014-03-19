/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pcconfigurator;

import java.math.BigDecimal;
import java.util.List;
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
public class PcSetManagerImplTest {
    
    private PcSetManager pcSetManager;
    
    @BeforeClass
    public static void setUpClass(){
        
    }
    
    @AfterClass
    public static void tearDownClass(){
        
    }
    
    @Before
    public void setUp() {
        pcSetManager = new PcSetManagerImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createPcSet method, of class PcSetManagerImpl.
     */
    @Test
    public void testCreatePcSet() {
        Configuration config = new Configuration("Test configuration","David Kaya");
        Component comp = new Component("Intel", new BigDecimal("25.50"), ComponentTypes.MOTHERBOARD, 45, "Zakladna doska Intel");
        PcSet expected = new PcSet(comp, config);
        pcSetManager.createPcSet(expected);
        PcSet result = pcSetManager.getPcSet(config, comp);
        
        assertNotSame("PcSets are the same objects",expected,result);
        assertEquals("PcSets does not equal",expected, result);
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
        System.out.println("deletePcSet");
        PcSet pcSet = null;
        PcSetManagerImpl instance = new PcSetManagerImpl();
        instance.deletePcSet(pcSet);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findConfigByComponent method, of class PcSetManagerImpl.
     */
    @Test
    public void testFindConfigByComponent() {
        System.out.println("findConfigByComponent");
        Component component = null;
        PcSetManagerImpl instance = new PcSetManagerImpl();
        List<Configuration> expResult = null;
        List<Configuration> result = instance.findConfigByComponent(component);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listCompsInConfiguration method, of class PcSetManagerImpl.
     */
    @Test
    public void testListCompsInConfiguration() {
        System.out.println("listCompsInConfiguration");
        Configuration configuration = null;
        PcSetManagerImpl instance = new PcSetManagerImpl();
        List<Component> expResult = null;
        List<Component> result = instance.listCompsInConfiguration(configuration);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
