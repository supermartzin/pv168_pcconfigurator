/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pcconfigurator;

import java.util.Set;
import java.math.BigDecimal;
import java.util.HashSet;
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
        compManager.createComponent(component);
        
        Component result = compManager.getComponentById(component.getId());
        
        assertEquals("components do not match", component, result);
        assertNotSame("components must not be the same objects", component, result);
        
        try
        {
            result = compManager.getComponentById(new Long(-5));
            fail("ID cannot be negative number, exception must be thrown");
        } catch(IllegalArgumentException ex) {}
        
    }

    /**
     * Test of findAllComponents method, of class ComponentManagerImpl.
     */
    @Test
    public void testFindAllComponents() {
        Component comp1 = new Component(1, "AMD Graphics", new BigDecimal(149.80), ComponentTypes.GPU, 250, "R9 290X");
        Component comp2 = new Component(2, "Creative", new BigDecimal(24.00), ComponentTypes.SOUNDCARD, 15, "SoundBlaster S150");
        Component comp3 = new Component(3, "Kingston", new BigDecimal(37.50), ComponentTypes.RAM, 15, "DDR3 Memory 1600M");
        Set<Component> comps = new HashSet<>();
        comps.add(comp1);
        comps.add(comp2);
        comps.add(comp3);
        
        compManager.createComponent(comp1);
        compManager.createComponent(comp2);
        compManager.createComponent(comp3);
        
        Set<Component> result = compManager.findAllComponents();
        
        assertEquals("expected number of components does not match", comps.size(), result.size());
        assertEquals("components do not match", comps, result);
        assertNotSame("components must not be the same objects", comp1, compManager.getComponentById((long) 1));
        assertNotSame("components must not be the same objects", comp2, compManager.getComponentById((long) 2));
        assertNotSame("components must not be the same objects", comp3, compManager.getComponentById((long) 3));       
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
