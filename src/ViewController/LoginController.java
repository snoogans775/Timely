/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewController;

import java.net.URL;
import java.time.*;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.sql.*;
import java.sql.SQLException;

import Model.*;
import static Model.mySQLConnection.conn;

/**
 *
 * @author kevin
 */
public class LoginController implements Initializable {
    @FXML TextField headerTextField;
    @FXML TextField loginUsernameTextField;
    @FXML TextField loginPasswordTextField;
    
    @FXML Label welcomeLabel;
    
    @FXML Button loginSubmitButton;
    
    public ResourceBundle localResourceBundle;
    
    String errorMessage = "Validation failed. Please try a different username or password.";
    
    
    public void getTimeZone() {
        Calendar cal = Calendar.getInstance();
        //
        TimeZone timeZone = cal.getTimeZone();
        String timeZoneText = timeZone.getDisplayName();
        
        welcomeLabel.setText("Your timezone is " + timeZoneText);
    }
    
    public ResourceBundle getLanguageResource() {
        ResourceBundle rb;
        rb = ResourceBundle.getBundle("Resource/Nat", Locale.getDefault() );
        
        return rb;
 
    }
    
    public boolean validateLogin(String username, String password) throws SQLException {
        //FIXME: implement mySQL checks
        try {
            //Validate username
            String sql;
            sql = "SELECT * FROM user WHERE userName = " + username +"AND password = " + password;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);
            
            if ( rs.wasNull() ) {
                welcomeLabel.setText(errorMessage);
                return false;
            }  
          
        } catch (SQLException e) {
            welcomeLabel.setText("Error code:1 A mySQL exception occurred.");
        }
        return true;
    }
   
    public void initialize(URL url, ResourceBundle rb) {

        // Localization for text prompt
        if(     Locale.getDefault().getLanguage().equals("es")
             || Locale.getDefault().getLanguage().equals("fr") ) 
        {
            loginUsernameTextField.setPromptText( getLanguageResource().getString("username"));
            loginPasswordTextField.setPromptText( getLanguageResource().getString("password"));
            loginSubmitButton.setText( getLanguageResource().getString("login"));
            
            //Error handling
            errorMessage = getLanguageResource().getString("validationError");
        }
        
    }    
    
}
