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
import Model.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
import javafx.stage.StageStyle;

public class EditCustomerController implements Initializable {
    
    @FXML Label customeridLabel;
    
    @FXML TextField customerNameTextField;
    @FXML TextField customerAddressTextField;
    @FXML TextField customerAddressLineTwoTextField;
    @FXML TextField customerPostalCodeTextField;
    @FXML TextField customerPhoneTextField;
    @FXML TextField customerCityTextField;
    
    @FXML ComboBox customerCountryComboBox;
    @FXML CheckBox customerActiveCheckBox;
    
    //Get connection object for all other scenes
    Connection conn = mySQLConnection.getConnection();
    
    Session session;
    User currentUser;
    
    //Receive Session data
    
    public void initSession(Session s) throws SQLException {
        this.session = s;
        this.currentUser = s.getCurrentUser();
        System.out.println( "The current user is: " + currentUser.getUserName() );
    }
    
    //BEGIN CONTROL METHODS
    
    public void saveButtonPushed(ActionEvent event) throws SQLException, ParseException, IOException, EventException {
        
        try {
            
            //Get country from input
            String countryString = customerCountryComboBox.getValue().toString();
            Country countryObject = Query.getCountryByName( countryString, conn);

            //Add city
            String city = customerCityTextField.getText();

            Query.addCity( new City(
                0, //Placeholder cityid
                customerCityTextField.getText(), //city
                countryObject.getCountryid(),  //countryid
                Timestamp.from( Instant.now() ),  //createDate
                currentUser.getUserName(),  //createdBy
                Timestamp.from( Instant.now()  ),  //lastUpdate
                currentUser.getUserName()  //lastUpdateBy      

            ), conn);

            City cityObject = Query.getCityByName(city, conn);

            //Add address      
            String address = customerAddressTextField.getText();
            Query.addAddress( new Address(
                0, //All java objects have a placeholder primary id
                address,
                customerAddressLineTwoTextField.getText(),
                cityObject.getCityid(), //CityId
                customerPostalCodeTextField.getText(),
                customerPhoneTextField.getText(),
                Timestamp.from( Instant.now() ),
                currentUser.getUserName(),
                Timestamp.from( Instant.now()  ),
                currentUser.getUserName()

            ), conn);

            //The addressid is AUTO_INCREMENT in the database.
            //So the object id must be a get from the SQL database. 
            Address addressObject = Query.getAddressByFirstLine( address, conn );

            //Create customer with address id

            //Convert adtive checkbox to int
            Integer activeValue;
            activeValue = customerActiveCheckBox.isSelected() ? 1 : 0;

            Customer customer = new Customer(
                Integer.parseInt( customeridLabel.getText() ),
                customerNameTextField.getText(),
                addressObject.getAddressid(),
                activeValue,
                Timestamp.from( Instant.now() ),
                currentUser.getUserName(),
                Timestamp.from( Instant.now()  ),
                currentUser.getUserName()
            );

            List<String> allFields = getAllFields();

            //Check if any of the fields are empty
            if ( !allFields.contains("")) Query.updateCustomer(customer, conn);
            else throw new EventException("Please provide complete customer information.");


            //Change Scene to customer view
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation( getClass().getResource("/ViewController/Customer.fxml") );
            Parent parent = loader.load();
            Scene addParentScene = new Scene(parent);

            CustomerController controller = loader.getController();
            controller.initSession( session );

            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
            window.setScene(addParentScene);
            window.show();
            
        } catch (EventException e) {
            
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.initStyle(StageStyle.UTILITY);
            a.setContentText( e.getMessage() );
            a.show();
        } catch (NullPointerException e) {
            
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.initStyle(StageStyle.UTILITY);
            a.setContentText("Please provide a country.");
            a.show();
        }
        
    }
    
    public void cancelButtonPushed(ActionEvent event) throws IOException, SQLException {
        
        //Change Scene to cutomer view
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
    
    
    //BEGIN DATA METHODS
    
    public void initData(Customer customer) throws SQLException, ParseException {
        
        //Get addressbyid 
        Address customerAddress = Query.getAddressById( customer.getAddressId(), conn );
        //Get citybyid
        City customerCity = Query.getCityById( customerAddress.getCityId(), conn );
        //get country by id
        Country customerCountry = Query.getCountryById( customerCity.getCountryId(), conn );
        
        //Prepare boolean from integer range(0,1) in customer.active
        Boolean customerActive = ( customer.getActive() == 1 ) ? true : false;
        
        //Set text in fields, comboboxes, and checkboxes
        customeridLabel.setText( String.valueOf( customer.getCustomerid()) );
        customerNameTextField.setText( customer.getCustomerName());
        customerAddressTextField.setText( customerAddress.getAddress() );
        customerAddressLineTwoTextField.setText( customerAddress.getAddress2() );
        customerPostalCodeTextField.setText( customerAddress.getPostalCode() );
        customerPhoneTextField.setText( customerAddress.getPhone() );
        customerCityTextField.setText( customerCity.getCity() );

        customerCountryComboBox.setValue( customerCountry.getCountry() );
        customerActiveCheckBox.setSelected( customerActive );
        
    }
    
    public List<String> getAllFields() {
        
        List<String> allFields = new ArrayList<>();
        
        allFields.add( customerNameTextField.getText() );
        allFields.add( customerAddressTextField.getText() );
        allFields.add( customerAddressLineTwoTextField.getText() );
        allFields.add( customerPostalCodeTextField.getText() );
        allFields.add( customerPhoneTextField.getText() );
        allFields.add( customerCityTextField.getText() );

        allFields.add( customerCountryComboBox.getValue().toString() );
        
        return allFields;
    }

    public ObservableList<String> generateCountryList() throws SQLException {
        
        List<String> countryList = Query.getAllCountryNames( conn );
        
        ObservableList<String> countryObservableList = FXCollections.observableArrayList(countryList);
        
        return countryObservableList;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            customerCountryComboBox.setItems( generateCountryList() );
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
