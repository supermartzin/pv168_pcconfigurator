/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pcconfigurator.gui;

import java.awt.Window;
import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.SwingWorker;
import pcconfigurator.componentmanager.Component;
import pcconfigurator.componentmanager.ComponentManager;
import pcconfigurator.componentmanager.ComponentManagerImpl;
import pcconfigurator.componentmanager.ComponentTypes;

/**
 *
 * @author Martin
 */
public class ComponentManagerFrame extends javax.swing.JFrame {
    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pcconfigurator/gui/Strings");   
    private static final Logger LOGGER = Logger.getLogger(ComponentManagerFrame.class.getName());
    private final ComponentManager compManager;
    private final ComponentTableModel compModel;
    private Component currentComponent;
    
    private final Window thisWindow = this;
    /**
     * Creates new form ComponentManagerFrame
     */
    public ComponentManagerFrame() {
        initComponents();
        this.compManager = new ComponentManagerImpl();
        this.compModel = (ComponentTableModel) componentsTable.getModel();
        
        findAllComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        deleteComponentButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        componentsTable = new javax.swing.JTable();
        addComponentButton = new javax.swing.JButton();
        editComponentButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pcconfigurator/gui/Strings"); // NOI18N
        setTitle(bundle.getString("componentManager")); // NOI18N
        setResizable(false);

        deleteComponentButton.setText(bundle.getString("delete")); // NOI18N
        deleteComponentButton.setEnabled(false);
        deleteComponentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteComponentButtonActionPerformed(evt);
            }
        });

        componentsTable.setModel(new ComponentTableModel());
        componentsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        componentsTable.getTableHeader().setReorderingAllowed(false);
        componentsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                componentsTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(componentsTable);
        if (componentsTable.getColumnModel().getColumnCount() > 0) {
            componentsTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("vendor")); // NOI18N
            componentsTable.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("name")); // NOI18N
            componentsTable.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("type")); // NOI18N
            componentsTable.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("price")); // NOI18N
            componentsTable.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("power")); // NOI18N
        }

        addComponentButton.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        addComponentButton.setText(bundle.getString("addComponent")); // NOI18N
        addComponentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addComponentButtonActionPerformed(evt);
            }
        });

        editComponentButton.setText(bundle.getString("edit")); // NOI18N
        editComponentButton.setEnabled(false);
        editComponentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editComponentButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addComponentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 217, Short.MAX_VALUE)
                        .addComponent(editComponentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteComponentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addComponentButton, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(editComponentButton)
                    .addComponent(deleteComponentButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addComponentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addComponentButtonActionPerformed
        JDialog addComponent = new AddComponentDialog(this, true);
        addComponent.setVisible(true);
    }//GEN-LAST:event_addComponentButtonActionPerformed

    private void editComponentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editComponentButtonActionPerformed
        EditComponentDialog editComponent = new EditComponentDialog(this, true);
        editComponent.setTextFields(currentComponent.getName(),currentComponent.getVendor(),currentComponent.getPrice(),currentComponent.getPower());
        editComponent.setVisible(true);
    }//GEN-LAST:event_editComponentButtonActionPerformed

    private void deleteComponentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteComponentButtonActionPerformed
        deleteComponent(currentComponent);
    }//GEN-LAST:event_deleteComponentButtonActionPerformed

    private void componentsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_componentsTableMouseClicked
        deleteComponentButton.setEnabled(true);
        editComponentButton.setEnabled(true);
        currentComponent = compModel.getComponentAt(componentsTable.convertRowIndexToModel(componentsTable.getSelectedRow()));
    }//GEN-LAST:event_componentsTableMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ComponentManagerFrame().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addComponentButton;
    private javax.swing.JTable componentsTable;
    private javax.swing.JButton deleteComponentButton;
    private javax.swing.JButton editComponentButton;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables

    private void findAllComponents() {
        SwingWorker<Set<Component>, Void> worker = new SwingWorker<Set<Component>, Void>() {

            @Override
            protected Set<Component> doInBackground() throws Exception {
                /*// test
                Component component1 = new Component("Intel", (new BigDecimal(25.50)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.CPU, 45, "Pentium IV 4200X");
                Component component2 = new Component("Kingston", (new BigDecimal(37.50)).setScale(2, BigDecimal.ROUND_HALF_UP), ComponentTypes.RAM, 15, "DDR3 Memory 1600M"); 
                compManager.createComponent(component1);
                compManager.createComponent(component2);
                // end test*/
                return compManager.findAllComponents();
            }

            @Override
            protected void done() {
                try {
                    if (get().isEmpty()) {
                        WarningDialog warningDialog = new WarningDialog(thisWindow, true);
                        warningDialog.setSize(365, 140);
                        warningDialog.setWarningLabel(bundle.getString("noConfigurationFound"));
                        warningDialog.setVisible(true);
                    }
                    
                    compModel.loadComponents(get());
                    compModel.fireTableDataChanged();
                } catch (InterruptedException | ExecutionException ex) {
                    LOGGER.log(Level.SEVERE, "Error getting components from database to table: ", ex);
                }
            }
        };
        
        worker.execute();
    }
    
    public void findComponentsByType(ComponentTypes type){
        SwingWorker<Set, Void> worker = new SwingWorker<Set, Void>() {

            @Override
            protected Set<Component> doInBackground() throws Exception {
                return compManager.findCompByType(type);
            }
            
            @Override
            protected void done(){
                try{
                    compModel.loadComponents(get());
                    compModel.fireTableDataChanged();
                } catch (InterruptedException | ExecutionException ex){
                    LOGGER.log(Level.SEVERE, "Erro getting components from database to table: ",ex);
                }
            }
        };
        
        worker.execute();
    }
    
    public void createComponent(String vendor, BigDecimal price, ComponentTypes type, int power, String name ) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                Component comp = new Component(vendor, price, type, power, name);
                compManager.createComponent(comp);
                return null;
            }

            @Override
            protected void done() {
                compModel.loadComponents(compManager.findAllComponents());
                compModel.fireTableDataChanged();
            }
        };
        
        worker.execute();
    }
    
    public void deleteComponent(Component component){
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                compManager.deleteComponent(component);
                return null;
            }
            
            @Override
            protected void done(){
                compModel.loadComponents(compManager.findAllComponents());
                compModel.fireTableDataChanged();
            }
        };
                
        worker.execute();
    }
    
    public void updateComponent(String vendor, String name, BigDecimal price, Integer power){
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                currentComponent.setName(name);
                currentComponent.setVendor(vendor);
                currentComponent.setPower(power);
                currentComponent.setPrice(price);
                compManager.updateComponent(currentComponent);
                return null;
            }
            
            @Override
            protected void done(){
                compModel.loadComponents(compManager.findAllComponents());
                compModel.fireTableDataChanged();
            }
        };
        
        worker.execute();
    }
}
