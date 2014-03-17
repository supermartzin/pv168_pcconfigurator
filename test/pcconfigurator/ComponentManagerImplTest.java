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
        // test validneho komponentu
        Component component = new Component("Intel", new BigDecimal("25.50"), ComponentTypes.MOTHERBOARD, 45, "Zakladna doska Intel");
        compManager.createComponent(component);
        
        long compID = component.getId();
        assertNotNull("id of component is null", compID);
        
        Component result = compManager.getComponentById(compID);
        assertEquals("components do not match", component, result);
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
        Component component = new Component("ASUS", new BigDecimal(89.90), ComponentTypes.MOTHERBOARD, 38, "Zakladna doska ASUS");
        compManager.createComponent(component);
        
        Component result = compManager.getComponentById(component.getId());
        
        assertEquals("components do not match", component, result);
        assertNotSame("components must not be the same objects", component, result);
        
        // test invalidneho komponentu
        try
        {
            result = compManager.getComponentById((long) (-5));
            fail("ID cannot be negative number, exception must be thrown");
        } catch(IllegalArgumentException ex) { }
        
    }

    /**
     * Test of findAllComponents method, of class ComponentManagerImpl.
     */
    @Test
    public void testFindAllComponents() {
        // test neprazdneho zoznamu komponentov
        Component comp1 = new Component("AMD Graphics", new BigDecimal(149.80), ComponentTypes.GPU, 250, "R9 290X");
        Component comp2 = new Component("Creative", new BigDecimal(24.00), ComponentTypes.SOUNDCARD, 15, "SoundBlaster S150");
        Component comp3 = new Component("Kingston", new BigDecimal(37.50), ComponentTypes.RAM, 15, "DDR3 Memory 1600M");
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
        assertNotSame("components must not be the same objects", comp1, compManager.getComponentById(comp1.getId()));
        assertNotSame("components must not be the same objects", comp2, compManager.getComponentById(comp2.getId()));
        assertNotSame("components must not be the same objects", comp3, compManager.getComponentById(comp3.getId()));
        
        // test prazdneho zoznamu komponentov
        ComponentManagerImpl compManagerEmpty = new ComponentManagerImpl();
        assertTrue("set of components should be empty", compManagerEmpty.findAllComponents().isEmpty());
    }

    /**
     * Test of updateComponent method, of class ComponentManagerImpl.
     */
    @Test
    public void testUpdateComponent() {
        Component comp1 = new Component("AMD Graphics", new BigDecimal(149.80), ComponentTypes.GPU, 250, "R9 290X");
        Component comp2 = new Component("Creative", new BigDecimal(24.00), ComponentTypes.SOUNDCARD, 15, "SoundBlaster S150");
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
            comp.setPrice(new BigDecimal(-149.80));
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
        comp.setPrice(new BigDecimal(124));
        comp.setType(ComponentTypes.MOTHERBOARD);
        comp.setVendor("Intel");
        compManager.updateComponent(comp);
        
        assertEquals("name do not match", "Zakladna doska P3X2", comp.getName());
        assertEquals("power do not match", 23, comp.getPower());
        assertEquals("price do not match", new BigDecimal(124), comp.getPrice());
        assertEquals("type do not match", ComponentTypes.MOTHERBOARD, comp.getType());
        assertEquals("vendor do not match", "Intel", comp.getVendor());
        
        // test ci sa updatom nezmenili ine komponenty
        Component component = compManager.getComponentById(comp2.getId());
        assertEquals("name of other component has been modified", comp2.getName(), component.getName());
        assertEquals("power of other component has been modified", comp2.getPower(), component.getPower());
        assertEquals("price of other component has been modified", comp2.getPrice(), component.getPrice());
        assertEquals("type of other component has been modified", comp2.getType(), component.getType());
        assertEquals("vendor of other component has been modified", comp2.getVendor(), component.getVendor());
        
    }

    /**
     * Test of deleteComponent method, of class ComponentManagerImpl.
     */
    @Test
    public void testDeleteComponent() {
        // vymazanie validneho komponentu
        Component comp1 = new Component("AMD Graphics", new BigDecimal(149.80), ComponentTypes.GPU, 250, "R9 290X");
        Component comp2 = new Component("Creative", new BigDecimal(24.00), ComponentTypes.SOUNDCARD, 15, "SoundBlaster S150");
        compManager.createComponent(comp1);
        compManager.createComponent(comp2);
        
        assertNotNull("id cannot be null", comp1.getId());
        assertNotNull("id cannot be null", comp2.getId());
        
        compManager.deleteComponent(comp1);
        
        assertNull("component should be deleted and null", compManager.getComponentById(comp1.getId()));
        assertNotNull("component should not be modified by deleting other components", compManager.getComponentById(comp2.getId()));
        
        // vymazanie invalidnych komponentov
        Component comp3 = new Component("Kingston", new BigDecimal(37.50), ComponentTypes.RAM, 15, "DDR3 Memory 1600M");
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
        
    }
    
}
