package CalendarApp;

import DB.mySQLConnection;
import Model.Event;
import ViewController.AddCustomerController;
import java.sql.Connection;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CalendarApp extends Application{
    
    @Override
    public void start( Stage stage ) throws Exception {
        
        //Get connection object for all other scenes
        Connection conn = mySQLConnection.getConnection();
        
        Parent root = FXMLLoader.load( getClass().getResource("/ViewController/Login.fxml"));
        
        Scene scene = new Scene( root );
        
        stage.setScene( scene );
        stage.show();
    }
    
}
