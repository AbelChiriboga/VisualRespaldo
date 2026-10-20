/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

/**
 *
 * @author User
 */
import java.sql.*;
import javax.swing.JOptionPane;
public class Conexion {
   Connection conectar;
    public  Connection conectar(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
               conectar = DriverManager.getConnection("jdbc:mysql://localhost/biblioteca_huella","root","");
            
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return conectar;
    }
    
}
