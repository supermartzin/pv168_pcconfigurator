/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pcconfigurator;

import java.util.Set;
import java.math.BigDecimal;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Martin Vrabel
 */
public class ComponentManagerImplTest {
    
    private ComponentManagerImpl compManager;
    
    public ComponentManagerImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        compManager = new ComponentManagerImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createComponent method, of class ComponentManagerImpl.
     */
    @Test
    public void testCreateComponent() {
        Component component = new Component(1, "Intel", new BigDecimal("25.50"), ComponentTypes.MOTHERBOARD, 45, "Zakladna doska Intel");
        compManager.createComponent(component);
        
        long compID = component.getId();
        assertNotNull("id of component is null", compID);
        
        Component result = compManager.getComponentById(compID);
        assertEquals("components do not match", component, result);
        assertNotSame("components must not be the same objects", component, result);
        
        assertEquals("number of components in set do not match", 1, compManager.findAllComponents().size());
    }

    /**
     * Test of getComponentById method, of class ComponentManagerImpl.
     */
    @Test
    public void testGetComponentById() {
        Component component = new Component(10, "ASUS", new BigDecimal(89.90), ComponentTypes.MOTHERBOARD, 38, "Zakladna doska ASUS");
        
    }

    /**
     * Test of findAllComponents method, of class ComponentManagerImpl.
     */
    @Test
    public void testFindAllComponents() {
        System.out.println("findAllComponents");
        ComponentManagerImpl instance = new ComponentManagerImpl();
        Set<Component> expResult = null;
        Set<Component> result = instance.findAllComponents();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateComponent method, of class ComponentManagerImpl.
     */
    @Test
    public void testUpdateComponent() {
        System.out.println("updateComponent");
        Component component = null;
        ComponentManagerImpl instance = new ComponentManagerImpl();
        instance.updateComponent(component);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteComponent method, of class ComponentManagerImpl.
     */
    @Test
    public void testDeleteComponent() {
        System.out.println("deleteComponent");
        Component component = null;
        ComponentManagerImpl instance = new ComponentManagerImpl();
        instance.deleteComponent(component);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findCompByType method, of class ComponentManagerImpl.
     */
    @Test
    public void testFindCompByType() {
        System.out.println("findCompByType");
        ComponentTypes type = null;
        ComponentManagerImpl instance = new ComponentManagerImpl();
        Set<Component> expResult = null;
        Set<Component> result = instance.findCompByType(type);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
