package ViewController;

import DB.Session;
import DB.*;
import Model.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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
    
    //Get connection object for all other scenes
    Connection conn = mySQLConnection.getConnection();
    
    Session session = null;
    User currentUser;
    
    //
    //BEGIN DATA METHODS
    //
    
    //Receive Session data
    
    public void initSession(Session s) throws SQLException {
        this.session = s;
        this.currentUser = s.getCurrentUser();
        System.out.println( "The current user is: " + currentUser.getUserName() );
        
        initTableView( currentUser );
        
        alertUpcomingEvent(currentUser, Duration.ofMinutes(15), conn);
    }
    
    public void initTableView(User user) throws SQLException {
        
        if( session != null ) {
            ObservableList<Event> appointments;
            appointments = Query.getAllEventsByUserId(user, conn);

            //FIXME: convert customer id to customer name
            eventTableView.setItems( appointments );
            
        } else System.out.println("No session found.");
    }

    public static void alertUpcomingEvent(User user, Duration duration, Connection conn) throws SQLException {
        
        //Summary: checks if any upcoming events are
        //starting between now() and now() + duration
        
        //Returns: boolean if an upcoming appointment is found
        boolean upcomingAppt = false;
        
        //Record current time and 
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.plus(duration);
        
        //search all appointments for apptStart.between( now, now + duration )
        ObservableList<Event> allEvents = Query.getAllEventsByUserId(user, conn);
        
        Query.process( allEvents, limit, (e,d) -> {
            if( e.isAfter(now) && e.isBefore(limit) ) {
                Alert a = new Alert(AlertType.INFORMATION);
                a.setContentText("Upcoming Appointment at " + e.toString());
                a.show();
            };
        });
    }
    
    //
    // BEGIN CONTROL METHODS
    //
    
    public void addButtonPushed(ActionEvent event) throws IOException, SQLException {

        //Change Scene to event view
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation( getClass().getResource("/ViewController/AddEvent.fxml") );
        Parent parent = loader.load();
        Scene addParentScene = new Scene(parent);
        
        AddEventController controller = loader.getController();
        controller.initSession( session );

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(addParentScene);
        window.show();
          
    }
    
    public void editButtonPushed(ActionEvent event) throws IOException, SQLException {
        
        //Get selected customer
        Appointment selectedAppointment = (Appointment) eventTableView.getSelectionModel().getSelectedItem();
        
        //Change Scene to event view
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation( getClass().getResource("/ViewController/EditEvent.fxml") );
        Parent parent = loader.load();
        Scene addParentScene = new Scene(parent);
        
        EditEventController controller = loader.getController();
        controller.initSession( session );
        controller.initData( selectedAppointment );

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(addParentScene);
        window.show();
          
    }
    
    public void deleteButtonPushed(ActionEvent event) throws IOException, SQLException {
        
        SelectionModel eventTable = eventTableView.getSelectionModel();
        Appointment appt = (Appointment) eventTable.getSelectedItem();
        
        Query.deleteAppointment(appt, conn);
        
        initTableView(currentUser);
        
    }
    
    public void customerButtonPushed(ActionEvent event) throws IOException, SQLException {

        //Change Scene to event view
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation( getClass().getResource("/ViewController/Customer.fxml") );
        Parent parent = loader.load();
        Scene addParentScene = new Scene(parent);
        
        CustomerController controller = loader.getController();
        controller.initSession( session );

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(addParentScene);
        window.show();        
        
    }

    public void initialize(URL url, ResourceBundle rb) {
        
        //TableView for Customers
        
        //Create columns
        eventTitleColumn.setCellValueFactory( new PropertyValueFactory<>("title"));
        eventCustomerColumn.setCellValueFactory( new PropertyValueFactory<>("customerId"));
        eventStartColumn.setCellValueFactory( new PropertyValueFactory<>("start"));
        eventEndColumn.setCellValueFactory( new PropertyValueFactory<>("end"));
        
    }    
    
}
