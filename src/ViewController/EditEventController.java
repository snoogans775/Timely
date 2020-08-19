/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewController;

import CalendarApp.EventException;
import static Model.Appointment.*;
import DB.Query;
import DB.Session;
import DB.mySQLConnection;
import Model.Appointment;
import Model.Customer;
import Model.Event;
import Model.User;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EditEventController implements Initializable {
    
    @FXML Label eventAppointmentIdLabel;
    
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
    
    public void saveButtonPushed(ActionEvent event) throws IOException, SQLException {

        editEvent();
        
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
        
        //Format appointmentid Label
        String eventIdMessage = Integer.toString( appt.getAppointmentid() );
        
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
        
        //Create observableList of Customer names for ComboBox
        ObservableList<String> customerNames;
        customerNames = FXCollections.observableArrayList( generateCustomerMap().keySet() );
        //Sort List by name alphabetically
        FXCollections.sort(customerNames, (a,b) -> a.compareTo( b ) );
        
        //Generate combo boxes  
        eventStartTimeComboBox.setItems( generateTimes() );
        eventDurationComboBox.setItems( generateDurations() );
        eventCustomerComboBox.setItems( customerNames );
        
        //Populate Fields
        eventAppointmentIdLabel.setText( eventIdMessage );
        eventTitleTextField.setText( appt.getTitle() );
        eventDescriptionTextField.setText( appt.getDescription() );
        eventLocationTextField.setText( appt.getLocation() );
        eventContactTextField.setText( appt.getContact() );
        eventTypeTextField.setText( appt.getType() );
        eventURLTextField.setText( appt.getUrl() );

        eventStartDatePicker.setValue( startDate );
        eventStartTimeComboBox.setValue( startTime );
        eventDurationComboBox.setValue( eventDuration );

        eventCustomerComboBox.setValue( customer.getCustomerName() );
        
    }

    public void editEvent() throws NullPointerException, SQLException {
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
            //Using Local Time for all exception methods
            Appointment checkAppt = new Appointment(
                    Timestamp.valueOf( startDateTime ), 
                    Timestamp.valueOf( endDateTime )
            );
            
            if ( checkForBusinessHours(checkAppt) ) throw new EventException(
                    "Please schedule within business hours."
            );
            
            //Check for scheduling conflicts
            if ( checkForConflict(checkAppt, currentUser, conn) ) throw new EventException(
                    "Schedule conflict found. Add event failed."
            );

            //TIMEZONE CONVERSION
            Timestamp startTimestamp = convertToTimestamp(startDateTime, currentUser);
            Timestamp endTimestamp = convertToTimestamp(endDateTime, currentUser);

            //Get customer from selected String
            String selectedCustomerName = (String) eventCustomerComboBox.getValue();
            Customer selectedCustomer = generateCustomerMap().get( selectedCustomerName );

            Appointment appt = new Appointment(
                Integer.parseInt( eventAppointmentIdLabel.getText() ),
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
        
            Query.updateAppointment(appt, conn);


        } catch (EventException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.initStyle(StageStyle.UTILITY);
            a.setContentText( e.getMessage() );
            a.show();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
