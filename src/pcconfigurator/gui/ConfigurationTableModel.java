/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pcconfigurator.gui;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import pcconfigurator.configurationmanager.Configuration;

/**
 *
 * @author davidkaya
 */
public class ConfigurationTableModel extends AbstractTableModel {
    private List<Configuration> configurations = new ArrayList<>();
    
    public Configuration getConfiguration(int row) {
        return configurations.get(row);
    }
    
    public void loadConfigurations(Set<Configuration> configurations){
        this.configurations = new ArrayList<>();
        this.configurations.addAll(configurations);
    }
    
    @Override
    public int getRowCount() {
        return configurations.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Configuration configuration = configurations.get(rowIndex);
        switch (columnIndex){
            case 0:
                return configuration.getName();
            case 1:
                return configuration.getCreator();
            case 2:
                return configuration.getCreationTime().format(DateTimeFormatter.ISO_LOCAL_TIME) 
                        + " - " + configuration.getCreationTime().format(DateTimeFormatter.ISO_LOCAL_DATE);
            case 3:
                return configuration.getLastUpdate().format(DateTimeFormatter.ISO_LOCAL_TIME)
                        + " - " + configuration.getLastUpdate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }    
    
    public Configuration getConfigurationAt(int rowIndex){
        return configurations.get(rowIndex);
    }
}
