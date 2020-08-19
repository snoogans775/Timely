/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewController;

import CalendarApp.EventException;
import DB.Query;
import DB.Session;
import DB.mySQLConnection;
import Model.Appointment;
import static Model.Appointment.*;
import Model.Customer;
import Model.Event;
import Model.User;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AddEventController implements Initializable {
    
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
    
    //Get connection object for all other scenes
    Connection conn = mySQLConnection.getConnection();

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
        
        //Simple lambda iterator
        //This lambda is intended as a debugging tool
        //In this case, it does not accomodate any significant performance
        //improvement. It is only here as syntactical sugar.
        allChoices.forEach( dur -> System.out.println( dur.toString()) );
        
        return allChoices;
    }
    
    //
    //BEGIN CONTROL METHODS
    //
    
    public ObservableMap<String, Customer> generateCustomerMap() throws SQLException {
        
        ObservableList<Customer> customerList = Query.getAllCustomers(conn);
        ObservableMap<String, Customer> customerMap = FXCollections.observableHashMap();
        
        customerList.forEach( c -> customerMap.put( c.getCustomerName(), c ) );
        
        return customerMap;
    }
    
    public void saveButtonPushed(ActionEvent event) throws IOException, SQLException {
        
        try { 
            
            addEvent();
            
        } catch (SQLException e) {

        }
            
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
    
    public void cancelButtonPushed(ActionEvent event) throws IOException, SQLException {

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
        
    }

    public void addEvent() throws NullPointerException, SQLException {
        
        try {

            //Create objects for Date and Time components
            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            Duration eventDuration;

            //Get values from input
            LocalDate selectedDate = eventStartDatePicker.getValue();
            LocalTime selectedTime = (LocalTime) eventStartTimeComboBox.getValue();
            startDateTime = LocalDateTime.of(selectedDate, selectedTime);

            //Get Duration and DateTime objects from input and caculate end time        
            eventDuration = (Duration) eventDurationComboBox.getValue();
            endDateTime = startDateTime.plus( eventDuration );
            
            //EXCEPTION HANDLING
            //Using Local Time for all exceptions
            Appointment checkAppt = new Appointment(
                    Timestamp.valueOf( startDateTime ), 
                    Timestamp.valueOf( endDateTime )
            );
            
            if ( checkForBusinessHours(checkAppt) ) throw new EventException(
                    "Please schedule within business hours.");

            //Check for scheduling conflicts
            if ( checkForConflict(checkAppt, currentUser, conn) ) throw new EventException(
                    "Schedule conflict found. Add event failed.");
            
            //TIMEZONE CONVERSION
            Timestamp startTimestamp = convertToTimestamp(startDateTime, currentUser);
            Timestamp endTimestamp = convertToTimestamp(endDateTime, currentUser);
            
            //Get customer from selected String
            String selectedCustomerName = (String) eventCustomerComboBox.getValue();
            Customer selectedCustomer = generateCustomerMap().get( selectedCustomerName );

            Appointment appt = new Appointment(
                0,
                selectedCustomer.getCustomerid(),
                currentUser.getUserId(),
                eventTitleTextField.getText(),
                eventDescriptionTextField.getText(),
                eventLocationTextField.getText(),
                eventContactTextField.getText(),
                eventTypeTextField.getText(),
                eventURLTextField.getText(),
                startTimestamp,
                endTimestamp,
                Timestamp.from( Instant.now() ),
                currentUser.getUserName(),
                Timestamp.from( Instant.now()  ),
                currentUser.getUserName()
            );

            Query.addAppointment(appt, conn);

        } catch (EventException e) {
            Alert a = new Alert(AlertType.ERROR);
            a.initStyle(StageStyle.UTILITY);
            a.setContentText( e.getMessage() );
            a.show();
        }

    }
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            
            ObservableList<String> customerNames;
            customerNames = FXCollections.observableArrayList( generateCustomerMap().keySet() );
            //Sort List by name alphabetically
            FXCollections.sort(customerNames, (a,b) -> a.compareTo( b ) );
            
            eventStartTimeComboBox.setItems( generateTimes() );
            eventDurationComboBox.setItems( generateDurations() );
            eventCustomerComboBox.setItems( customerNames );
            
        } catch (SQLException ex) {
            
            Logger.getLogger(AddEventController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
}
