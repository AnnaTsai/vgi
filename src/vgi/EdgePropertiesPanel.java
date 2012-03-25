package vgi;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;

/*
 * edge_properties.java
 *
 * Created on 2012/1/6, 下午 03:59:32
 */
/**
 *
 * @author bl606
 */
public class EdgePropertiesPanel extends javax.swing.JPanel {

    /**
     * Creates new form edge_properties
     */
    public EdgePropertiesPanel(mxGraph graph, mxCell cell, Transition transition) {
        
        initComponents();
        this.graph = graph;
        this.cell = cell;
        this.automata = automata;
        this.transition = transition;
        
        showLabel();
        Map<String,Object> styles=graph.getCellStyle(cell);
        String color=(String)styles.get("strokeColor");
        strokeColor=Color.decode(color);
        strokeColorButton.setBackground(strokeColor);
        
        strokeWidth=(String)styles.get("strokeWidth");
        if(strokeWidth!=null){
            float width=Float.parseFloat(strokeWidth);
            int ind=(int)width;
            strokeWidthBox.setSelectedIndex(ind-1);
        }
    }
    
    private void showLabel() {
        labelTextField.setText(cell.getValue().toString());
    }
    
    private void setStartEndArrow(JComboBox comboBox, Boolean startEnd) {
        String arrowDir = (startEnd) ? "startArrow" : "endArrow";
        String arrowType = ((String)comboBox.getSelectedItem()).toLowerCase();
        Object[] edge = {cell};
        
        graph.setCellStyles(arrowDir, arrowType, edge);
    }
    
    public void setStrokeColor(mxGraph graph, Color color) {
        Object[] edge = {cell};
        graph.setCellStyles("strokeColor", mxUtils.hexString(color), edge);
    }

    public void setStrokeWidth(mxGraph graph, float width) {
        Object[] edge = {cell};
        String wid = String.valueOf(width);
        graph.setCellStyles("strokeWidth", wid, edge);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel3 = new javax.swing.JLabel();
        labelLabel = new javax.swing.JLabel();
        labelTextField = new javax.swing.JTextField();
        startStyleLabel = new javax.swing.JLabel();
        startStyleComboBox = new javax.swing.JComboBox();
        ednStyleLabel = new javax.swing.JLabel();
        endStyleComboBox = new javax.swing.JComboBox();
        strokeLabel = new javax.swing.JLabel();
        strokeColorButton = new javax.swing.JButton();
        strokeWidthBox = new javax.swing.JComboBox();

        jLabel3.setText("jLabel3");

        setPreferredSize(new java.awt.Dimension(325, 362));
        setLayout(new java.awt.GridBagLayout());

        labelLabel.setText("Label :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(labelLabel, gridBagConstraints);

        labelTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelTextFieldMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(labelTextField, gridBagConstraints);

        startStyleLabel.setText("Start_Style :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(startStyleLabel, gridBagConstraints);

        startStyleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "classic", "block", "open", "oval", "diamond" }));
        startStyleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStyleComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(startStyleComboBox, gridBagConstraints);

        ednStyleLabel.setText("End_Style :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(ednStyleLabel, gridBagConstraints);

        endStyleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "classic", "block", "open", "oval", "diamond" }));
        endStyleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endStyleComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(endStyleComboBox, gridBagConstraints);

        strokeLabel.setText("Stroke :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(strokeLabel, gridBagConstraints);

        strokeColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strokeColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(strokeColorButton, gridBagConstraints);

        strokeWidthBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        strokeWidthBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strokeWidthBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        add(strokeWidthBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void startStyleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStyleComboBoxActionPerformed
        setStartEndArrow((JComboBox)evt.getSource(), true);
    }//GEN-LAST:event_startStyleComboBoxActionPerformed

    private void endStyleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endStyleComboBoxActionPerformed
        setStartEndArrow((JComboBox)evt.getSource(), false);
    }//GEN-LAST:event_endStyleComboBoxActionPerformed

    private void labelTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelTextFieldMouseClicked
        ExpressionEditor editor = new ExpressionEditor(
                new JFrame(), 
                true, 
                (WeightedRegularExpression) ((mxCell) cell).getValue());
        editor.setVisible(true);
        ((mxCell) cell).setValue(editor.getExpression());
    }//GEN-LAST:event_labelTextFieldMouseClicked

    private void strokeColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strokeColorButtonActionPerformed
        strokeColor=JColorChooser.showDialog( this,
                     "Fill color", strokeColor );
        setStrokeColor(graph,strokeColor);
        strokeColorButton.setBackground(strokeColor);
    }//GEN-LAST:event_strokeColorButtonActionPerformed

    private void strokeWidthBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strokeWidthBoxActionPerformed
        JComboBox cb = (JComboBox)evt.getSource();
        String width=(String)cb.getSelectedItem();
        float wid=Float.parseFloat(width);
        setStrokeWidth(graph,wid);
    }//GEN-LAST:event_strokeWidthBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ednStyleLabel;
    private javax.swing.JComboBox endStyleComboBox;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel labelLabel;
    private javax.swing.JTextField labelTextField;
    private javax.swing.JComboBox startStyleComboBox;
    private javax.swing.JLabel startStyleLabel;
    private javax.swing.JButton strokeColorButton;
    private javax.swing.JLabel strokeLabel;
    private javax.swing.JComboBox strokeWidthBox;
    // End of variables declaration//GEN-END:variables
    private mxCell cell;
    private mxGraph graph;
    private Automata automata;
    private Transition transition;
    private Color strokeColor=Color.white;
    private String strokeWidth=null;
}
