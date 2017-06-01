/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.net.URI;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author pdavila
 */
public class ReceptorVetador implements VetoableChangeListener {
    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (evt.getPropertyName().equals("url")) {
            String url = (String) evt.getNewValue();
            final URI uri;
            
            try {
                if (!url.matches("^jdbc:.*$")) throw new Exception();
                String cleanURI = url.substring(5);
                uri = URI.create(cleanURI);
                initJOptionPaneDialog("La actualización no pudo ser vetada.", JOptionPane.INFORMATION_MESSAGE, "Actualización correcta!!", 1000);
            } catch (Exception e) {
                initJOptionPaneDialog("No se pudo modificar el valor de la propiedad :(. \nMotivo: URL invalida.", JOptionPane.ERROR_MESSAGE, "URL invalida!!", 3000);
                throw new PropertyVetoException("Error", evt);
            }
        }
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
