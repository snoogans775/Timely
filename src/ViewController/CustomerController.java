/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewController;

import DB.Query;
import DB.Session;
import DB.mySQLConnection;
import Model.Customer;
import Model.User;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kevin
 */
public class CustomerController implements Initializable {
    
    @FXML TableView customerTableView;
    @FXML TableColumn<Customer, String> customerNameColumn;
    @FXML TableColumn<Customer, String> customerActiveColumn;

    @FXML Button addCustomerButton;
    @FXML Button editCustomerButton;
    @FXML Button deleteCustomerButton;
    
    //Navigation bar
    @FXML Button eventViewButton;
    @FXML Button customerViewButton;
    
    //Get connection object for all other scenes
    Connection conn = mySQLConnection.getConnection();

    Session session;
    User currentUser;
    
    //
    //BEGIN METHODS
    //
    
    //BEGIN DATA METHODS
    public void initSession(Session session) throws SQLException {
        
        this.session = session;
        this.currentUser = session.getCurrentUser();
        
        initTableView();
        
    }
    
    public void initTableView() throws SQLException {
        
        ObservableList<Customer> customerList = Query.getAllCustomers(conn);
        
        //Sort List by name alphabetically
        FXCollections.sort(customerList, (a,b) -> 
                a.getCustomerName().compareTo( b.getCustomerName() ) 
                );
        
        customerTableView.setItems( customerList );
        
    }
    
    //BEGIN CONTROL METHODS
    
    public void addButtonPushed(ActionEvent event) throws IOException, SQLException {

        //Change Scene to event view
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation( getClass().getResource("/ViewController/AddCustomer.fxml") );
        Parent parent = loader.load();
        Scene addParentScene = new Scene(parent);
        
        AddCustomerController controller = loader.getController();
        controller.initSession( session );

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(addParentScene);
        window.show();
          
    }
    
    public void editButtonPushed(ActionEvent event) throws IOException, SQLException, ParseException {
        
        //Get selected customer
        SelectionModel selectionModel = customerTableView.getSelectionModel();
        Customer selectedCustomer = (Customer) selectionModel.getSelectedItem();
        
        //Change Scene to event view
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation( getClass().getResource("/ViewController/EditCustomer.fxml") );
        Parent parent = loader.load();
        Scene addParentScene = new Scene(parent);
        
        EditCustomerController controller = loader.getController();
        controller.initSession( session );
        controller.initData( selectedCustomer );

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(addParentScene);
        window.show();
          
    }
    
    public void deleteButtonPushed(ActionEvent event) throws IOException, SQLException {
        
        SelectionModel eventTable = customerTableView.getSelectionModel();
        Customer customer = (Customer) eventTable.getSelectedItem();
        
        Query.deleteCustomer(customer, conn);
        
        initTableView();
        
    }
    
    public void eventButtonPushed(ActionEvent event) throws IOException, SQLException {

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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //TableView for Customers
        
        //Create columns
        customerNameColumn.setCellValueFactory( new PropertyValueFactory<>("customerName"));
        customerActiveColumn.setCellValueFactory( new PropertyValueFactory<>("active"));
        
 
        
    }    
    
}
