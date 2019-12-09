package ViewController;

import DB.Query;
import DB.Session;
import DB.mySQLConnection;
import Model.City;
import Model.Customer;
import Model.Event;
import Model.User;
import static ViewController.EventController.alertUpcomingEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ReportController implements Initializable {
    
    @FXML TextArea reportTextArea;
    
    @FXML DatePicker apptTypeDatePicker;
    
    @FXML ComboBox consultantComboBox;
    @FXML ComboBox apptCustomerComboBox;
    
    @FXML Button apptTypeButton;
    @FXML Button consultantButton;
    @FXML Button apptCityButton;
    
    Connection conn = mySQLConnection.getConnection();
    Session session;
    User currentUser;
    
    public void initSession(Session s) throws SQLException {
        //Summary:    Receive Session data and initiate the scene
        //Arguments:  The defined session
        //Returns:    void
        
        this.session = s;
        currentUser = s.getCurrentUser();
        System.out.println( "The current user is: " + currentUser.getUserName() );

        
    }
    
    public String printScheduleByUser(User user) throws SQLException {
        
        //Create blank string
        String result = "";
        
        ObservableList<Event> allEvents = Query.getAllEventsByUserId(user, conn);
        
        try {

            //Prepare filter predicate to remove past events
            Predicate<Event> presentFilter = (Event e) -> {
                //Check if event starts before current time
                return e.getStart().before( Timestamp.valueOf (LocalDateTime.now() ) );
            };

            //Remove past events
            allEvents.removeIf( presentFilter );

            //Create list of events and details
            String eventDetails = "";
            for( Event e : allEvents ) {
                
                String timeAsString = e.getStart().toLocalDateTime()
                        .format(DateTimeFormatter.ISO_DATE);
                
                eventDetails += timeAsString;
                eventDetails += ": " + e.getDescription();
                eventDetails += "\n\t" + Query.getCustomerById(e.getCustomerId(), conn).getCustomerName();
                eventDetails += "\n";

            }

            result += "Upcoming Events for Consultant " + user.getUserName();
            result += "\n";
            
            //Check if event details are empty
            if( eventDetails.contentEquals("") ) result += "No upcoming events.";
            else result += eventDetails;
        
        } catch (Exception e) {
            
            result = "There was an error generating the report.";
        }
            
            
        return result;
        
    }
    
    public String printApptTypeByMonth(Month selectedMonth) throws SQLException {
        
        String result = "";
        try {
            
            //Get all events 
            ObservableList<Event> allEvents = Query.getAllEvents(conn); //FIXME: Filter with SQL

            //Filter events that have start in month
            Predicate<Event> monthFilter = (Event e) -> {
                LocalDateTime dateTime = e.getStart().toLocalDateTime();
                Month dateMonth = dateTime.getMonth();
                if( dateMonth != selectedMonth) return true;
                else return false;
            };

            allEvents.removeIf( monthFilter );

            //create typeMap
            HashMap<String, Integer> typeMap = new HashMap();
            allEvents.forEach((e) -> {
                //add type to typeMap if absent from typeMap
                //then increment if key is present
                typeMap.merge(e.getType(), 1, Integer::sum);

            }); 

            //Format typeMap as string
            String apptTypes = typeMap.toString();
            apptTypes = apptTypes.replace("{", "");
            apptTypes = apptTypes.replace("}", "");
            apptTypes = apptTypes.replace(", ", "\n");
            apptTypes = apptTypes.replace("=", " = ");

            //print Keys and values of typeMap
            System.out.println(typeMap.toString());
            result += "Appointment Types in Month of " + selectedMonth
                    .getDisplayName(TextStyle.FULL, Locale.US);
            result += "\n\n";
            result += apptTypes;
        } catch (Exception e) {
            result = "There was an error while generating the report.";
        }
        
        return result;
        
    }
    
        public String printApptByCustomer(Customer customer) throws SQLException {
        
        String result = "";
        
        //Get all events 
        ObservableList<Event> allEvents = Query.getAllEvents(conn); //FIXME: Filter with SQL
        
        //Filter events that have start in month
        Predicate<Event> customerFilter = (Event e) -> {
            int eventCustomerId = e.getCustomerId();
            if( eventCustomerId != customer.getCustomerid()) return true;
            else return false;
        };
        
        //Remove all events that do not have the selected customerId
        allEvents.removeIf( customerFilter );

        //Create list of events and details
        String eventDetails = "";
        for( Event e : allEvents ) {

            String timeAsString = e.getStart().toLocalDateTime()
                    .format(DateTimeFormatter.ISO_DATE);

            eventDetails += timeAsString;
            eventDetails += ": " + e.getDescription();
            eventDetails += "\n\t" + Query.getCustomerById(e.getCustomerId(), conn).getCustomerName();
            eventDetails += "\n";

        }

        result += "Upcoming Events for Customer " + customer.getCustomerName();
        result += "\n";

        //Check if event details are empty
        if( eventDetails.contentEquals("") ) result += "No upcoming events.";
        else result += eventDetails;
        
        return result;
        
    }
    
    public void apptTypeButtonPushed(ActionEvent event) throws SQLException {
        
        //Get month
        Month selectedMonth = apptTypeDatePicker.getValue().getMonth();
        
        //Print report
        reportTextArea.setText( printApptTypeByMonth( selectedMonth ) );
        
    }
    
    public void consultantButtonPushed(ActionEvent event) throws SQLException {
        
        User selectedUser = (User) consultantComboBox.getValue();
        selectedUser.setTimezone( TimeZone.getTimeZone("UTC") );
        
        //Build report and send to text area
        reportTextArea.setText( printScheduleByUser( selectedUser ) );
        
    }
    
    public void apptCustomerButtonPushed(ActionEvent event) throws SQLException {
        
        Customer selectedCustomer = (Customer) apptCustomerComboBox.getValue();
        
        //Build report and send to text area
        reportTextArea.setText( printApptByCustomer( selectedCustomer ) );
        
        
    }
    
    public void backButtonPushed(ActionEvent event) throws IOException, SQLException {
        
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
    
    //
    //BEGIN DATE METHODS
    //
    
    public ObservableList<User> generateUsers() throws SQLException {
        
        ObservableList<User> allUsers = Query.getAllUsers(conn);
        
        return allUsers;
        
    }
    
    public ObservableList<Customer> generateCustomers() throws SQLException {
        
        ObservableList<Customer> customerList = Query.getAllCustomers(conn);
        
        return customerList;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            //Populate User list
            consultantComboBox.setItems( generateUsers() ); 
            consultantComboBox.getSelectionModel().selectFirst();
            
            //Populate City list
            apptCustomerComboBox.setItems( generateCustomers() ); 
            apptCustomerComboBox.getSelectionModel().selectFirst();
            
        } catch (SQLException ex) {
            
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
