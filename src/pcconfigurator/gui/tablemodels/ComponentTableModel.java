/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pcconfigurator.gui.tablemodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import pcconfigurator.componentmanager.Component;

/**
 *
 * @author Martin
 */
public class ComponentTableModel extends AbstractTableModel {

    private List<Component> components = new ArrayList<>();
    
    public void loadComponents(Set<Component> components) {
        this.components = new ArrayList<>();
        this.components.addAll(components);
    }
    
    @Override
    public int getRowCount() {
        return components.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Component component = components.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return component.getVendor();
            case 1: return component.getName();
            case 2: return component.getType().getName();
            case 3: return component.getPrice().toString() + " €";
            case 4: return component.getPower() + " W";
            default: throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public Component getComponentAt(int rowIndex){
        return components.get(rowIndex);
    }
}
