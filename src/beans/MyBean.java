package beans;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.beans.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pdavila
 */
public class MyBean implements Serializable {
    
    public static final String PROP_URL = "url";
    public static final String PROP_PASSWORD = "password";
    
    private String url;
    private String user;
    private String password;
    private Connection connection;
    private Properties properties;
    
    private PropertyChangeSupport propertySupport;
    private VetoableChangeSupport vetoableSupport;
    
    public MyBean() {
        this.propertySupport = propertySupport;
        this.vetoableSupport = vetoableSupport;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) throws PropertyVetoException {
        String oldValue = url;
        vetoableSupport.fireVetoableChange(PROP_URL, oldValue, url);
        
        url = value;
        propertySupport.firePropertyChange(PROP_URL, oldValue, url);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        String oldValue = password;
        password = value;
        
        propertySupport.firePropertyChange(PROP_PASSWORD, oldValue, password);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
    public void initConnection(String propertiesFile) throws PropertyVetoException {
        Properties prop = new Properties();
        String url, user, password;
        url = user = password = null;
        
        try(FileInputStream in = new FileInputStream(propertiesFile)) {
            prop.load(in);
            
            url = prop.getProperty("db.url");
            this.setUrl(url);
            
            user = prop.getProperty("db.user");
            this.setUser(user);
            
            password = prop.getProperty("db.password");
            this.setPassword(password);
            
            createConnection(url, user, password);
        } catch (IOException ex) {
            System.err.println("No se ha podido establecer la conexión con la BD... :(");
            System.exit(0);
        }  
    }
    
    private void createConnection(String url, String user, String password){
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conectando con la BD...");
        } catch (SQLException ex) {
            System.err.println("No se ha podido establecer la conexión con la BD...");
            System.exit(0);
        }
    }
    
    public void finalize() throws Throwable {
        if(connection != null) connection.close();
        System.out.println("Cerrando la conexión con la BD...");
        super.finalize();
    }
    
    public <T> ArrayList<T> listarObjeto(Class<?> classe, String className){
        ResultSet resultSet = null;
        ArrayList<T> lista = new ArrayList<>();
        String sql = "SELECT * FROM " + className + " ORDER BY 1;";
        try(PreparedStatement sentenciaObj = connection.prepareStatement(sql)) {
            resultSet = sentenciaObj.executeQuery();
            
            if(resultSet != null) {
                while(resultSet.next()) {
                    T classInstance = (T) classe.newInstance();
                    PropertyDescriptor[] descriptors = Introspector.getBeanInfo(classe).getPropertyDescriptors();
                    
                    try {
                        int index = 1;
                        
                        for (PropertyDescriptor pd : descriptors) {
                            Method mr = pd.getReadMethod();
                            Method mw = pd.getWriteMethod();
                            
                            if (mr != null && !mr.getName().equals("getClass") && mw != null) {
                                if (mr.getReturnType().equals(Integer.TYPE)) {
                                    mw.invoke(classInstance, resultSet.getInt(index));
                                } else {
                                    if (mr.getReturnType().equals(String.class)) {
                                        mw.invoke(classInstance, resultSet.getString(index));
                                    }
                                }
                            }
                            
                            index++;
                        }
                        
                        lista.add(classInstance);

                    } catch (IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(MyBean.class.getName()).log(Level.SEVERE, null, ex);
                    }             
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al listrar la clase " + classe + "!!");
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MyBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IntrospectionException ex) {
            Logger.getLogger(MyBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return lista;    
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public void addVetoableChangeListener(VetoableChangeListener listener) {
        vetoableSupport.addVetoableChangeListener(listener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoableSupport.removeVetoableChangeListener(listener);
    }
}
