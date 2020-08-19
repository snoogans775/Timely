/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewController;

import DB.Session;
import DB.mySQLConnection;
import static DB.mySQLConnection.conn;
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
import java.io.IOException;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;

/**
 *
 * @author kevin
 */
public class LoginController implements Initializable {
    @FXML TextField headerTextField;
    @FXML TextField loginUsernameTextField;
    @FXML TextField loginPasswordTextField;
    
    @FXML ToggleGroup language;
    @FXML RadioButton englishRadioButton;
    @FXML RadioButton spanishRadioButton;
    @FXML RadioButton frenchRadioButton;
    
    @FXML Label welcomeLabel;
    @FXML Button loginSubmitButton;
    @FXML Button spanishButton;
    
    public ResourceBundle localResourceBundle;
    
    String errorMessage = "Validation failed. Please try a different username or password.";
    String confirmationMessage = "Login successful.";
    
    //Get connection object for all other scenes
    //Connection conn = mySQLConnection.getConnection();
    
    Session session;
    
//
    //BEGIN METHODS
    //
    
    //LANGUAGE RESOURCE METHODS
    
    public ResourceBundle getLanguageResource() {
        ResourceBundle rb;
        rb = ResourceBundle.getBundle("Resource/Nat", Locale.getDefault() );
        
        return rb;
 
    }
    
    public void initLanguage( String language ) {
        // Localization for text prompt
        // Session is not used until login is successful
        if(     language.equals("es")
             || language.equals("fr")
             || language.equals("en") ) 
        {
            ResourceBundle rb = ResourceBundle.getBundle("Resource/Nat_" + language);
            setLanguageResource( rb );
            
        }
    }
    
    public void setLanguageResource(ResourceBundle rb) {
        loginUsernameTextField.setPromptText( rb.getString("username"));
        loginPasswordTextField.setPromptText( rb.getString("password"));
        loginSubmitButton.setText( rb.getString("login"));

        //Error handling
        errorMessage = rb.getString("validationError");
        confirmationMessage = rb.getString("validationSuccess");
    }
    
    public void englishButtonPushed(ActionEvent event)  {
        
        ResourceBundle rb = ResourceBundle.getBundle("Resource/Nat_en");
        setLanguageResource( rb );
        
        initLanguage( "en" );
    }
    
    public void spanishButtonPushed(ActionEvent event)  {
        
        ResourceBundle rb = ResourceBundle.getBundle("Resource/Nat_es");
        setLanguageResource( rb );
        
        initLanguage( "es" );
    }
    
public void frenchButtonPushed(ActionEvent event)  {
        
        ResourceBundle rb = ResourceBundle.getBundle("Resource/Nat_fr");
        setLanguageResource( rb );
        
        initLanguage( "fr" );
    }
    
    public void loadEventView(ActionEvent event) throws IOException, SQLException {
        
        if ( validateLogin() ) {

            //Change Scene to event view
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation( getClass().getResource("/ViewController/Event.fxml") );
            Parent parent = loader.load();
            Scene addParentScene = new Scene(parent);

            EventController controller = loader.getController();
            controller.initSession( session );

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
                    rs.getTimestamp("createDate"),
                    rs.getString("createdBy"),
                    rs.getTimestamp("lastUpdate"),
                    rs.getString("lastUpdateBy")
                    )
                );
                
                //Log login
                Logger.recordLogin( session.getCurrentUser() );
                
                //Set timezone
                session.getCurrentUser().setTimezone( TimeZone.getDefault() );

                //Set text to green color
                welcomeLabel.setStyle("-fx-text-fill: #009900");

                //Notiy user before loading next scene
                String currentUserName = session.getCurrentUser().getUserName();
                welcomeLabel.setText(confirmationMessage + " Welcome " + currentUserName + ".");

                return true;

            }  else  throw new SQLException("");
          
        } catch (SQLException e) {
            Alert a = new Alert(AlertType.ERROR);
            a.setContentText( errorMessage );
            a.initStyle(StageStyle.UTILITY);
            a.show();
            
            //Add message to welcomeLabel
            welcomeLabel.setText(errorMessage);

            return false;
        }
    }
   
    public void initialize(URL url, ResourceBundle rb) {
        
        //New session to hold current user and locale
        session = new Session(conn);
        
        //Display user timezone
        welcomeLabel.setText("Your current timezone is: " + TimeZone.getDefault().getDisplayName() );
        
        //Initialize Database connection
        try {
            mySQLConnection.makeConnection();
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.getMessage());
        }

        initLanguage( Locale.getDefault().getLanguage() );
         
    }    
    
}
