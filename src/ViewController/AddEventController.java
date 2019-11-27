/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewController;

import CalendarApp.Lambda;
import DB.Query;
import DB.Session;
import Model.Appointment;
import Model.Event;
import Model.User;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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

    //Create instances for session class
    Session session = new Session();
    User currentUser = session.getCurrentUser();
    
    
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
        ObservableList<Duration> durationChoices = FXCollections.observableArrayList();
        
        
        //FIXME: represent as a String of minutes in combobox
        //Populate with limited choices
        durationChoices.add( Duration.ofMinutes(15) );
        durationChoices.add( Duration.ofMinutes(30) );
        durationChoices.add( Duration.ofMinutes(60) );
        
        return durationChoices;
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

    public void addEvent() {
        
        //Create star Date Time for Date and Time components
        LocalDateTime convertedDateTime;
        LocalDateTime calculatedEndTime;
        Duration eventDuration;
        
        LocalDate selectedDate = eventStartDatePicker.getValue();
        LocalTime selectedTime = (LocalTime) eventStartTimeComboBox.getValue();
        
                
        eventDuration = (Duration) eventDurationComboBox.getValue();
        convertedDateTime = LocalDateTime.of(selectedDate, selectedTime);
        calculatedEndTime = convertedDateTime.plus( eventDuration );
        
        Appointment appt = new Appointment(
            0,
            0, //FIXME: implement getAllCustomers for ChoiceBox and return selection by id
            currentUser.getUserId(),
            eventTitleTextField.getText(),
            eventDescriptionTextField.getText(),
            eventLocationTextField.getText(),
            eventContactTextField.getText(),
            eventTypeTextField.getText(),
            eventURLTextField.getText(),
            convertedDateTime,
            calculatedEndTime,
            LocalDateTime.now(),
            currentUser.getUserName(),
            LocalDateTime.now(),
            currentUser.getUserName()
        );
        
        Query.updateAppointment(currentUser, conn);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        eventStartTimeComboBox.setItems(generateTimes());
        eventDurationComboBox.setItems(generateDurations());
        
    }    
    
}
