/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import Model.Event;
import Model.User;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import Model.Appointment;
import Model.Customer;
import java.time.LocalDateTime;


public class Query {

    //BEGIN METHODS
    
    //Getters
    
    public static ObservableList<Event> getAllEventsByUserId(User user, Connection conn) throws SQLException {
        
        ObservableList<Event> eventList = FXCollections.observableArrayList();
        
        String sql = "SELECT * FROM appointment WHERE userId = " + "'" + user.getUserId() + "'";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery(sql);
        
        while( rs.next() ) {
            //FIXME: Convert to appointment
            eventList.add( new Appointment
                (
                rs.getInt("appointmentid"),
                rs.getInt("customerId"),
                rs.getInt("userId"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("location"),
                rs.getString("contact"),
                rs.getString("type"),
                rs.getString("url"),
                rs.getTimestamp("start"),
                rs.getTimestamp("end"),
                rs.getTimestamp("createDate"),
                rs.getString("createdBy"),
                rs.getTimestamp("lastUpdate"),
                rs.getString("lastUpdateBy")
                )
            );
            
        }
        
        return eventList;

    }
    
    public static Customer getCustomerById(int id, Connection conn) throws SQLException {
        
        Customer customer = null;
        
        String sql = "SELECT * FROM customer where customerId = " + "'" + id + "'";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery(sql);
        
        if( rs.absolute(1) ) {
        customer = new Customer(
            rs.getInt("customerid"),
            rs.getString("customerName"),
            rs.getInt("addressId"),
            rs.getInt("active"),
            rs.getTimestamp("createDate").toLocalDateTime(),
            rs.getString("createdBy"),
            rs.getTimestamp("lastUpdate").toLocalDateTime(),
            rs.getString("lastUpdateBy")
            );
        }
        
        return customer;
        
        
    }
    
    //Database Update Methods
    
    public static void addAppointment(Appointment appt, Connection conn) throws SQLException {
        String sql = 
            "INSERT INTO appointment("
            + "customerId, "
            + "userId, "
            + "title, "
            + "description, "
            + "location, "
            + "contact, "
            + "type, "
            + "url, "
            + "start, "
            + "end, "
            + "createDate, "
            + "createdBy, "
            + "lastUpdate, "
            + "lastUpdateBy) VALUES("
            + appt.getAttributesAsSQL()
            + ")";
            
            System.out.println(sql);
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate(sql);
            
                
                
        
    }
}
