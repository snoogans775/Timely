/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewController;

import CalendarApp.EventException;
import CalendarApp.Lambda;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
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
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

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
    
    public ObservableList<Customer> generateCustomers() throws SQLException {
        
        ObservableList<Customer> customerList = Query.getAllCustomers(conn);
        
        return customerList;
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
            
            //TIMEZONE CONVERSION
            Timestamp startTimestamp = convertToTimestamp(startDateTime, currentUser);
            Timestamp endTimestamp = convertToTimestamp(endDateTime, currentUser);
            
            /*
            //The user ZoneId will be applied to startTime and endTime
            ZoneId userZoneId= currentUser.getTimezone().toZoneId();
            
            //Convert LocalDateTime to ZonedDateTime objects
            ZonedDateTime zonedStartTime = startDateTime.atZone(userZoneId);
            zonedStartTime = ZonedDateTime.ofInstant(zonedStartTime.toInstant(), ZoneId.of("UTC"));
            
            ZonedDateTime zonedEndTime = endDateTime.atZone(userZoneId);
            zonedEndTime = ZonedDateTime.ofInstant(zonedEndTime.toInstant(), ZoneId.of("UTC"));
            
            //Convert all objects to sql.Timestamp
            //The offset from UTC will be baked in to this value
            Timestamp startTime = Timestamp.valueOf( zonedStartTime.toLocalDateTime() );
            Timestamp endTime = Timestamp.valueOf( zonedEndTime.toLocalDateTime() );
            
            
            */
            //Get customer id from selected object
            Customer selectedCustomer = (Customer)eventCustomerComboBox.getValue();

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
            
            //Use partial constructor to check business hours
            //FIXME: Change Event.checkForBusinessHours to accept two LocalDateTimes instead of Even object
            Appointment checkAppt = new Appointment(
                    Timestamp.valueOf(startDateTime), 
                    Timestamp.valueOf(endDateTime) 
            );
            if ( checkForBusinessHours(checkAppt) ) throw new EventException(
                    "Please schedule within business hours."
            );
            
            //Check for scheduling conflicts
            if ( checkForConflict(appt, currentUser, conn) ) throw new EventException(
                    "Schedule conflict found. Add event failed."
            );
            else Query.addAppointment(appt, conn);


        } catch (EventException e) {
            Alert a = new Alert(AlertType.ERROR);
            a.setContentText( e.getMessage() );
            a.show();
        }

    }
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            
            eventStartTimeComboBox.setItems( generateTimes() );
            eventDurationComboBox.setItems( generateDurations() );
            eventCustomerComboBox.setItems( generateCustomers() );
            
        } catch (SQLException ex) {
            
            Logger.getLogger(AddEventController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
}
