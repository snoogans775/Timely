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
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import java.sql.*;
import java.sql.SQLException;

import Model.*;
import static Model.mySQLConnection.conn;
import java.awt.Color;
import java.io.IOException;

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
    String confirmationMessage = "Login successful.";
    
    Session session;
    
    
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
    
    public void loadEventsView(ActionEvent event) throws IOException, SQLException {
        
        validateLogin();
        
        if (validateLogin()) {

        //Change Scene to event view
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation( getClass().getResource("/ViewController/Event.fxml") );
        Parent parent = loader.load();

        Scene addParentScene = new Scene(parent);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(addParentScene);
        window.show();
        }
          
    }
    
    public boolean validateLogin() throws SQLException, IOException {
        
        //Format user input for sql query
        String username = "'" + loginUsernameTextField.getText() + "'";
        String password = "'" + loginPasswordTextField.getText() + "'";
        
        try {
            //Validate username
            String sql;
            sql = "SELECT * FROM user WHERE userName = " + username +"AND password = " + password;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);
            
            if ( rs.next() ) {
                //Create a new User to be stored in the session
                session.setCurrentUser( new User(
                    rs.getInt("userid"),
                    rs.getString("userName"),
                    rs.getString("password"),
                    rs.getInt("active"),
                    rs.getTimestamp("createDate").toLocalDateTime(),
                    rs.getString("createdBy"),
                    rs.getTimestamp("lastUpdate").toLocalDateTime(),
                    rs.getString("lastUpdateBy")
                    )
                );

                //Set text to green color
                welcomeLabel.setStyle("-fx-text-fill: #009900");
                
                //Notiy user before loading next scene
                String currentUserName = session.getCurrentUser().getUserName();
                welcomeLabel.setText(confirmationMessage + " Welcome " + currentUserName + ".");
                
                return true;
                
            }  else  throw new SQLException("");
          
        } catch (SQLException e) {
            welcomeLabel.setStyle("-fx-text-fill: #990000");
            welcomeLabel.setText(errorMessage);
            
            return false;
        }
    }
   
    public void initialize(URL url, ResourceBundle rb) {
        
        //New session to hold current user and locale
        session = new Session();
        
        //Initialize Database connection
        try {
            mySQLConnection.makeConnection();
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // Localization for text prompt
        // Session is not used until login is successful
        if(     Locale.getDefault().getLanguage().equals("es")
             || Locale.getDefault().getLanguage().equals("fr") ) 
        {
            loginUsernameTextField.setPromptText( getLanguageResource().getString("username"));
            loginPasswordTextField.setPromptText( getLanguageResource().getString("password"));
            loginSubmitButton.setText( getLanguageResource().getString("login"));
            
            //Error handling
            errorMessage = getLanguageResource().getString("validationError");
            confirmationMessage = getLanguageResource().getString("validationSuccess");
        }
        
    }    
    
}
