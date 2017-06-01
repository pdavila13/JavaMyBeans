/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author pdavila
 */
public class Receptor implements PropertyChangeListener {
    
    public Receptor() {
        //
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        initJOptionPaneDialog(
                "Cambio de la propiedad \"" + evt.getPropertyName() 
                + "\" con valor anterior \"" + evt.getOldValue() 
                + "\" a valor nuevo \"" + evt.getNewValue() + "\".", 
                JOptionPane.INFORMATION_MESSAGE, "Actualizando...", 3000
        );
        
        if (evt.getPropertyName().equals("password")) {
            String password = (String)evt.getNewValue();
            String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";
            if (!password.matches(regex)) initJOptionPaneDialog(
                    "ALERTA: La contraseña no es segura.", 
                    JOptionPane.WARNING_MESSAGE, "Contraseña insegura", 2000);
        }
        
        initJOptionPaneDialog("La actualización se completo correctamente.", JOptionPane.INFORMATION_MESSAGE, "Actualización correcta", 1000);
    }
    
    private void initJOptionPaneDialog(String message, int type, String shortMessage, int time) {
        JOptionPane pane = new JOptionPane(message, type, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        JDialog dialog = pane.createDialog(shortMessage);
        dialog.addWindowListener(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        Timer timer = new Timer(time, new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        timer.start();

        dialog.setVisible(true);
    }
}
