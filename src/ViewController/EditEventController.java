/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewController;

import CalendarApp.Lambda;
import DB.Query;
import DB.Session;
import static DB.mySQLConnection.conn;
import Model.Appointment;
import Model.Customer;
import Model.Event;
import Model.User;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditEventController implements Initializable {
    
    @FXML TextField eventTitleTextField;
    @FXML TextField eventDescriptionTextField;
    @FXML TextField eventLocationTextField;
    @FXML TextField eventContactTextField;
    @FXML TextField eventTypeTextField;
    @FXML TextField eventURLTextField;
    
    @FXML DatePicker eventStartDatePicker;
    @FXML ComboBox eventStartTimeComboBox;
    @FXML ComboBox eventDurationComboBox;
    ObservableList<LocalTime> timeChoices = FXCollections.observableArrayList();
    ObservableList<Duration> durationChoices = FXCollections.observableArrayList();
    
    @FXML ComboBox eventCustomerComboBox;

    @FXML Button eventSaveButton;
    @FXML Button eventCancelButton;

    //Create instances for session class
    Session session;
    User currentUser;
    
    
    //
    //BEGIN TIME METHODS
    //
    public ObservableList<LocalTime> generateTimes() {
        ObservableList<LocalTime> timeChoicesList = FXCollections.observableArrayList();
        
        //Populate timeChoice ComboBox with times
        LocalTime timeIndex = LocalTime.of(0, 0);
        Duration halfHour = Duration.ofMinutes(30);

        for( int i = 0; i < 48; i++ ) {
            timeChoicesList.add(timeIndex);
            timeIndex = timeIndex.plus(halfHour);
        }
        
        return timeChoicesList;
    }
    
    public ObservableList<Duration> generateDurations() {
        ObservableList<Duration> allChoices = FXCollections.observableArrayList();
        
        
        //FIXME: represent as a String of minutes in combobox
        //Populate with limited choices
        allChoices.add( Duration.ofMinutes(15) );
        allChoices.add( Duration.ofMinutes(30) );
        allChoices.add( Duration.ofMinutes(60) );
        
        allChoices.forEach( dur -> System.out.println( dur.toString()) );
        
        return allChoices;
    }
    
    //
    //BEGIN CONTROL METHODS
    //
    
    public ObservableList<Customer> generateCustomers() throws SQLException {
        
        ObservableList<Customer> customerList = Query.getAllCustomers(conn);
        
        return customerList;
    }
    
    public void loadEventView(ActionEvent event) throws IOException, SQLException {

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
    //BEGIN DATA METHODS
    //
    
    //Receive Session data
    
    public void initSession(Session s) throws SQLException {
        this.session = s;
        this.currentUser = s.getCurrentUser();
        System.out.println( "The current user is: " + currentUser.getUserName() );
    }
    
    public void initData(Appointment appt) throws SQLException {
        
        //Generate combo boxes
        try {
            
            eventStartTimeComboBox.setItems( generateTimes() );
            eventDurationComboBox.setItems( generateDurations() );
            eventCustomerComboBox.setItems( generateCustomers() );
            
        } catch (SQLException ex) {
            
            Logger.getLogger(AddEventController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Get and convert time for java-based conversion
        Timestamp start = appt.getStart();
        Timestamp end = appt.getEnd();
        LocalDateTime startDateTime = start.toLocalDateTime();
        LocalDateTime endDateTime = end.toLocalDateTime();
        LocalTime endTime = endDateTime.toLocalTime();
        
        //Create date and time objects
        LocalDate startDate = startDateTime.toLocalDate();
        LocalTime startTime = startDateTime.toLocalTime();
        
        //Calculate Duration from start and end time
        Duration eventDuration = Duration.between(endTime, startTime);
        
        //Get customer object from SQL database
        Customer customer = Query.getCustomerById(appt.getCustomerId(), conn);
        
        
        eventTitleTextField.setText( appt.getTitle() );
        eventDescriptionTextField.setText( appt.getDescription() );
        eventLocationTextField.setText( appt.getLocation() );
        eventContactTextField.setText( appt.getContact() );
        eventTypeTextField.setText( appt.getType() );
        eventURLTextField.setText( appt.getUrl() );

        eventStartDatePicker.setValue( startDate );
        eventStartTimeComboBox.setValue( startTime );
        eventDurationComboBox.setValue( eventDuration );

        eventCustomerComboBox.setValue( customer );
        
    }

    public void addEvent() throws NullPointerException, SQLException {
        //FIXME: Add catch for NullPointerException or InvocationTargetException
        
        
        //Create objects for Date and Time components
        LocalDateTime convertedStartTime;
        LocalDateTime calculatedEndTime;
        Duration eventDuration;
        
        //Get values from input
        LocalDate selectedDate = eventStartDatePicker.getValue();
        LocalTime selectedTime = (LocalTime) eventStartTimeComboBox.getValue();
        
        //Get Duration and DateTime objects from input and caculate end time        
        eventDuration = Duration.parse( eventDurationComboBox.getValue().toString() );
        convertedStartTime = LocalDateTime.of(selectedDate, selectedTime);
        calculatedEndTime = convertedStartTime.plus( eventDuration );
        
        //Convert all objects to sql.Timestamp
        Timestamp startTime = Timestamp.valueOf(convertedStartTime);
        Timestamp endTime = Timestamp.valueOf(calculatedEndTime);
        
        //Get customer id from selected object
        Customer selectedCustomer = (Customer)eventCustomerComboBox.getValue();
        
        Appointment appt = new Appointment(
            0,
            selectedCustomer.getCustomerId(),
            currentUser.getUserId(),
            eventTitleTextField.getText(),
            eventDescriptionTextField.getText(),
            eventLocationTextField.getText(),
            eventContactTextField.getText(),
            eventTypeTextField.getText(),
            eventURLTextField.getText(),
            startTime,
            endTime,
            Timestamp.from( Instant.now() ),
            currentUser.getUserName(),
            Timestamp.from( Instant.now()  ),
            currentUser.getUserName()
        );
        
        Query.addAppointment(appt, conn);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
