/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewController;

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
    @FXML ChoiceBox eventStartTimeComboBox;
    @FXML ChoiceBox eventDurationComboBox;
    ObservableList<LocalTime> timeChoices = FXCollections.observableArrayList();
    ObservableList<Duration> durationChoices = FXCollections.observableArrayList();
    
    @FXML ChoiceBox eventCustomerChoiceBox;

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
        Duration timeOffset = Duration.ofMinutes(0);
        Duration halfHour = Duration.ofMinutes(30);

        for( int i = 0; i < 24; i++ ) {
            timeChoicesList.add(timeIndex);
            timeOffset.plus(halfHour);
        }
        
        return timeChoicesList;
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
    /*
    public void addEvent() {
        
        //Create star Date Time for Date and Time components
        LocalDate selectedDate = eventStartDatePicker.getValue();
        LocalTime selectedTime = eventStartTimeChoiceBox.getValue();
        
        LocalDateTime eventStartDateTime = LocalDateTime.of(eventStartDatePicker.getValue(), )
        
        Appointment appt = new Appointment(
            0, //FIXME: implement getAllCustomers for ChoiceBox and return selection by id
            currentUser.getUserId(),
            eventTitleTextField.getText(),
            eventDescriptionTextField.getText(),
            eventLocationTextField.getText(),
            eventContactTextField.getText(),
            eventTypeTextField.getText(),
            eventURLTextField.getText(),
            eventStartDatePicker.getValue(),
            eventEndDatePicker.getValue(),
            LocalDateTime.now(),
            currentUser.getUserName(),
            LocalDateTime.now(),
            currentUser.getUserName())
        );
        
        Query.updateAppointment(currentUser, appt);
    }
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        eventStartTimeComboBox.setItems(generateTimes());
        
    }    
    
}
