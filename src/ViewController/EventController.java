package ViewController;

import DB.Session;
import DB.*;
import static DB.mySQLConnection.conn;
import Model.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class EventController implements Initializable {

    @FXML TableView eventTableView;
    @FXML TableColumn<Event, String> eventTitleColumn;
    @FXML TableColumn<Event, Integer> eventCustomerColumn;
    @FXML TableColumn<Event, String> eventStartColumn;
    @FXML TableColumn<Event, String> eventEndColumn;
    
    @FXML Button addEventButton;
    @FXML Button editEventButton;
    @FXML Button deleteEventButton;
    
    //Navigation bar
    @FXML Button eventViewButton;
    @FXML Button customerViewButton;
    
    //Create instances for session class
    Session session = new Session();
    User currentUser = session.getCurrentUser();
    
    //Receive Session data
    
    public void initData(Session s) throws SQLException {
        this.session = s;
        this.currentUser = s.getCurrentUser();
        System.out.println( "The current user is: " + currentUser.getUserName() );
        
        initTableView( currentUser );
    }
    
    public void initTableView(User user) throws SQLException {
        ObservableList<Event> appointments;
        appointments = Query.getAllEventsByUserId(user, conn);
        
        //FIXME: convert 
        eventTableView.setItems( appointments );
    }
    

    public void initialize(URL url, ResourceBundle rb) {
        
        //TableView for Parts
        
        //Create columns
        eventTitleColumn.setCellValueFactory( new PropertyValueFactory<>("title"));
        eventCustomerColumn.setCellValueFactory( new PropertyValueFactory<>("customerid"));
        eventStartColumn.setCellValueFactory( new PropertyValueFactory<>("start"));
        eventEndColumn.setCellValueFactory( new PropertyValueFactory<>("end"));
        
        
        
    }    
    
}
