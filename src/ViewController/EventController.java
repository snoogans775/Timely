package ViewController;

import DB.Session;
import DB.*;
import Model.*;
import java.io.IOException;
import static java.lang.Math.floor;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
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
    
    //Month and Week view control
    @FXML DatePicker monthViewDatePicker;
    @FXML DatePicker weekViewDatePicker;
    @FXML Button monthviewButton;
    @FXML Button weekViewButton;
    
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
    
    public void initSession(Session s) throws SQLException {
        //Summary:    Receive Session data and initiate the scene
        //Arguments:  The defined session
        //Returns:    void
        
        this.session = s;
        this.currentUser = s.getCurrentUser();
        System.out.println( "The current user timezone is: " + currentUser.getTimezone() );
        
        initTableView();
        
        //Alert user of upcoming events
        alertUpcomingEvent(currentUser, Duration.ofMinutes(15), conn);
        
    }
    
    public void initTableView() throws SQLException {
        //Summary: Loads events for user into tableview
        //Arguments: The current user
        //Returns: void
        
        if( session != null ) {
            ObservableList<Event> appointments;
            appointments = Query.getAllEventsByUserId(currentUser, conn); //Timezone conversion included

            eventTableView.setItems( appointments );
            
        } else System.out.println("No session found.");
    }

    public static void alertUpcomingEvent(User user, Duration duration, Connection conn) throws SQLException {
        
        //Summary: checks if any upcoming events are
        //starting between now() and now() + duration
        //Arguments: The current user, the duration to check for upcoming apopintments, and the connection
        //Returns: boolean if an upcoming appointment is found
        boolean upcomingAppt = false;
        
        //Record current time and 
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.plus(duration);
        
        //search all appointments for apptStart.between( now, now + duration )
        ObservableList<Event> allEvents = Query.getAllEventsByUserId(user, conn);
        
        //Lambda function
        //This is not a great use case for a lambda
        //However, this does demonstrate abstraction of an
        //Event and a LocalDateTime in to a functional method.
        
        //Query.listAndTimeConsumer accepts a List<Event> and a LocalDateTime
        //and it performs an iterative comparsion on all members of the List
        Query.listAndTimeConsumer( allEvents, limit, (e,d) -> {
            LocalDateTime eStart = e.getStart().toLocalDateTime();
            
            if( eStart.isAfter(now) && eStart.isBefore(limit) ) {
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
        
        initTableView();
        
    }
    
    public void monthButtonPushed(ActionEvent event) throws IOException, SQLException {
        
        //Summary: Removes items from eventTableView that do not meet month criteria
        //Arguments: ActionEvent of button. Pulls selection from monthDatePicker
        //Returns: void
        //Comment: This code is very verbose to avoid complexity. The predicates
        //can be combined in to on e filter to reduce the method footprint.
        
        //Get selected month from comboBox
        LocalDate monthDate =  monthViewDatePicker.getValue();

        //Create Predicates for testing date values
        Predicate<Event> monthFilter = (Event e) -> {
            return e.getStart().toLocalDateTime().getMonth() != monthDate.getMonth();
        };
        Predicate<Event> yearFilter = (Event e) -> {
            return e.getStart().toLocalDateTime().getYear() != monthDate.getYear();
        };
        
        //Get appointments from mySQL database
        ObservableList<Event> appointments = Query.getAllEventsByUserId(currentUser, conn);
        
        //Remove appointments where month != DatePicker month
        appointments.removeIf( monthFilter );
        appointments.removeIf( yearFilter );
        
        //Update tablevIew
        eventTableView.setItems(appointments);
    }
    
    public void weekButtonPushed(ActionEvent event) throws IOException, SQLException {
        
        //Summary: Removes items from eventTableView that do not meet month criteria
        //Arguments: ActionEvent of button. Pulls selection from monthDatePicker
        //Returns: void
        //Comment: This code is very verbose to avoid complexity. The predicates
        //can be combined in to on e filter to reduce the method footprint.
        
        //Get selected month from comboBox
        LocalDate weekDate =  weekViewDatePicker.getValue();

        //Create Predicates for testing date values
        Predicate<Event> weekFilter = (Event e) -> {
            return getWeek(e.getStart().toLocalDateTime().toLocalDate() ) 
                   != getWeek( weekDate );
        };
        Predicate<Event> monthFilter = (Event e) -> {
            return e.getStart().toLocalDateTime().getMonth() != weekDate.getMonth();
        };
        Predicate<Event> yearFilter = (Event e) -> {
            return e.getStart().toLocalDateTime().getYear() != weekDate.getYear();
        };
        
        //Get appointments from mySQL database
        ObservableList<Event> appointments = Query.getAllEventsByUserId(currentUser, conn);
        
        //Remove appointments where month != DatePicker month
        appointments.removeIf( weekFilter );
        appointments.removeIf( monthFilter );
        appointments.removeIf( yearFilter );
        
        //Update tablevIew
        eventTableView.setItems(appointments);
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
    
    //
    //TIME METHODS
    //
    
    public int getWeek( LocalDate date ) {
        
        //Summary: Retrieves an integer value representing the week of the month
        //Arguments: A LocalDate
        //Returns: An integer from 1 to 4
       
        //Create a calendar object from instant
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getDayOfYear(), date.getDayOfMonth(), date.getDayOfMonth() );
        
        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        
        return weekOfMonth;
    }
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //TableView for Customers
        
        //Create columns
        eventTitleColumn.setCellValueFactory( new PropertyValueFactory<>("title"));
        eventCustomerColumn.setCellValueFactory( new PropertyValueFactory<>("customerId"));
        eventStartColumn.setCellValueFactory( new PropertyValueFactory<>("start"));
        eventEndColumn.setCellValueFactory( new PropertyValueFactory<>("end"));
        
        
    }    
    
}
