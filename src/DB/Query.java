/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import Model.Address;
import Model.Event;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Model.Appointment;
import Model.City;
import Model.Country;
import Model.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Query {

    //BEGIN METHODS
    
    //
    //EVENT METHODS
    //
    
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
    
    public static void updateAppointment(Appointment appt, Connection conn) throws SQLException {
        String sql = 
            "UPDATE appointment SET "
            + "customerId = " + appt.getCustomerId() + ", "
            + "userId = " + appt.getUserId() + ", "
            + "title = '" + appt.getTitle() + "', "
            + "description = '" + appt.getDescription() + "', "
            + "location = '" + appt.getLocation() + "', "
            + "contact = '" + appt.getContact() + "', "
            + "type = '" + appt.getType() + "', "
            + "url = '" + appt.getUrl() + "', "
            + "start = '" + appt.getStart() + "', "
            + "end = '" + appt.getEnd() + "', "
            + "createDate = '" + appt.getCreateDate() + "', "
            + "createdBy = '" + appt.getCreatedBy() + "', "
            + "lastUpdate = '" + appt.getLastUpdate() + "', "
            + "lastUpdateBy = '" + appt.getLastUpdateBy() + "' "
            + "WHERE appointmentid = " + appt.getAppointmentid();

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate(sql);

            System.out.println( stmt.executeUpdate() + " rows updated.");

    }
    
    public static void deleteAppointment(Appointment appt, Connection conn) throws SQLException {
        
        String sql = "DELETE FROM appointment WHERE appointmentid = "
            + appt.getAppointmentid()
            + " LIMIT 1";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.executeUpdate();
        
        System.out.println(sql);
        System.out.println( stmt.executeUpdate() + " rows updated.");
        
    }
    
    //
    //CUSTOMER METHODS
    //
    
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
            rs.getTimestamp("createDate"),
            rs.getString("createdBy"),
            rs.getTimestamp("lastUpdate"),
            rs.getString("lastUpdateBy")
            );
        }
        
        return customer;
        
        
    }

    
    public static ObservableList<Customer> getAllCustomers(Connection conn) throws SQLException {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        
        String sql = "SELECT * FROM customer";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery(sql);
        
        while( rs.next() ) {
            customerList.add( new Customer(
                    rs.getInt("customerId"),
                    rs.getString("customerName"),
                    rs.getInt("addressId"),
                    rs.getInt("active"),
                    rs.getTimestamp("createDate"),
                    rs.getString("createdBy"),
                    rs.getTimestamp("lastUpdate"),
                    rs.getString("lastUpdateBy")
            )
            
            );
        }
        
        return customerList;
    }
    
    public static void addCustomer(Customer customer, Connection conn) throws SQLException {
        String sql = 
            "INSERT INTO customer("
            + "customerId, "
            + "customerName, "
            + "addressId, "
            + "active, "
            + "createDate, "
            + "createdBy, "
            + "lastUpdate, "
            + "lastUpdateBy) VALUES("
            + customer.getAttributesAsSQL()
            + ")";
            
            System.out.println(sql);
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate(sql);

    }
    
    public static void updateCustomer(Customer customer, Connection conn) throws SQLException {
        String sql = 
            "UPDATE customer SET "
            + "customerName = '" + customer.getCustomerName() + "', "
            + "addressId = " + customer.getAddressId() + ", "
            + "active = " + customer.getActive() + ", "
            + "createDate = '" + customer.getCreateDate() + "', "
            + "createdBy = '" + customer.getCreatedBy() + "', "
            + "lastUpdate = '" + customer.getLastUpdate() + "', "
            + "lastUpdateBy = '" + customer.getLastUpdateBy() + "' "
            + "WHERE customerid = " + customer.getCustomerid();

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate(sql);

            System.out.println( stmt.executeUpdate() + " rows updated.");

    }
    
    public static void deleteCustomer(Customer customer, Connection conn) throws SQLException {
        
        String sql = "DELETE FROM customer WHERE customerid = "
            + customer.getCustomerid()
            + " LIMIT 1";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.executeUpdate();
        
        System.out.println(sql);
        System.out.println( stmt.executeUpdate() + " rows updated.");
        
        
    }
    
    //BEGIN ADDRESS METHODS
    
    //Java Address.java objects include the address, city, and country.
    //The object requires multiple SQL requests 
    //because the address, city, and country tables are separate in the database.
    //Country has no foreign key and is used for the first update.
    
    public static List getAllCountryNames(Connection conn) throws SQLException {
        List<String> countryList = new ArrayList<>();
        
        String sql = "SELECT * FROM country";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery(sql);
        
        while( rs.next() ) {
            countryList.add(rs.getString("country"));
        }
        
        
        
        return countryList;
        
    }
    
    public static void addCountry(Country country, Connection conn) throws SQLException {
        
        List<Country> countryList = getAllCountryNames(conn);
        
        if( countryList.contains(country) ) {
            Integer currentIndex = countryList.indexOf(country);
            Country currentCountry = countryList.get( currentIndex );

            System.out.println(currentCountry.getCountry() + " already in database.");
        } else {

            String sql = 
            "INSERT INTO country("
            + "country, "
            + "createDate, "
            + "createdBy, "
            + "lastUpdate, "
            + "lastUpdateBy"
            + ") VALUES("
            + country.getAttributesAsSQL()
            + ")";

            System.out.println(sql);

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate(sql);
            
            

        }
    }
    
    public static Country getCountryByName(String countryName, Connection conn) throws SQLException, ParseException {
        
        Country country = new Country();
        
        try {
            String sql = "SELECT * FROM country WHERE country = '"+ countryName + "'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);

            if( rs.next() ) {

                country = new Country(
                    rs.getInt("countryid"),
                    rs.getString("country"),
                    rs.getTimestamp("createDate"),
                    rs.getString("createdBy"),
                    rs.getTimestamp("lastUpdate"),
                    rs.getString("lastUpdateBy")

                );  
            }
            
            
        } catch (Exception e) {
            
            System.out.println("Failed to retrieve getCountryByName result.");
        }
        
        return country;

    }
    
    public static Country getCountryById(int id, Connection conn) throws SQLException, ParseException {
        
        Country country = new Country();
        
        try {
            String sql = "SELECT * FROM country WHERE countryid = "+ id + " LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);

            if( rs.next() ) {

                country = new Country(
                    rs.getInt("countryid"),
                    rs.getString("country"),
                    rs.getTimestamp("createDate"),
                    rs.getString("createdBy"),
                    rs.getTimestamp("lastUpdate"),
                    rs.getString("lastUpdateBy")

                );  
            }
            
            
        } catch (Exception e) {
            
            System.out.println("Failed to retrieve getCountryByName result.");
        }
        
        return country;

    }
    
    public static List getAllCityNames(Connection conn) throws SQLException {
        List<String> cityList = new ArrayList<>();
        
        String sql = "SELECT * FROM city";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery(sql);
        
        while( rs.next() ) {
            cityList.add(rs.getString("city"));
        }
        
        
        
        return cityList;
        
    }
        
    public static void addCity(City city, Connection conn) throws SQLException {
        
            String sql = 
            "INSERT INTO city("
            + "city, "
            + "countryId, "
            + "createDate, "
            + "createdBy, "
            + "lastUpdate, "
            + "lastUpdateBy"
            + ") VALUES("
            + city.getAttributesAsSQL()
            + ")";
            
            System.out.println(sql);
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate(sql);
    }
    
    public static City getCityByName(String cityName, Connection conn) throws SQLException, ParseException {
        
        City city = new City();
        
        try {
            String sql = "SELECT * FROM city WHERE city = '" + cityName + "' LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);

            while( rs.next() ) {

                city = new City(
                    rs.getInt("cityid"),
                    rs.getString("city"),
                    rs.getInt("countryId"),
                    rs.getTimestamp("createDate"),
                    rs.getString("createdBy"),
                    rs.getTimestamp("lastUpdate"),
                    rs.getString("lastUpdateBy")

                );  
            }
            
            
        } catch (Exception e) {
            
            System.out.println("Failed to retrieve getCityByName result.");
        }
        
        return city;
    }
    
    public static City getCityById(Integer id, Connection conn) throws SQLException, ParseException {
        
        City city = new City();
        
        try {
            String sql = "SELECT * FROM city WHERE cityid = " + id + " LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);

            while( rs.next() ) {

                city = new City(
                    rs.getInt("cityid"),
                    rs.getString("city"),
                    rs.getInt("countryId"),
                    rs.getTimestamp("createDate"),
                    rs.getString("createdBy"),
                    rs.getTimestamp("lastUpdate"),
                    rs.getString("lastUpdateBy")

                );  
            }
            
            
        } catch (Exception e) {
            
            System.out.println("Failed to retrieve getCityByName result.");
        }
        
        return city;
    }
    
    public static void addAddress(Address address, Connection conn) throws SQLException {
        
        String sql = 
            "INSERT INTO address("
            + "address, "
            + "address2, "
            + "cityId, "
            + "postalCode, "
            + "phone, "
            + "createDate, "
            + "createdBy, "
            + "lastUpdate, "
            + "lastUpdateBy"
            + ") VALUES("
            + address.getAttributesAsSQL()
            + ")";
            
            System.out.println(sql);
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate(sql);
    }
    
    public static Address getAddressByFirstLine(String addressFirstLine, Connection conn) throws SQLException, ParseException {
        
        Address address = new Address();
        
        try {
            String sql = "SELECT * FROM address WHERE address = '" + addressFirstLine + "' LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);

            if( rs.next() ) {

                address = new Address(
                    rs.getInt("addressid"),
                    rs.getString("address"),
                    rs.getString("address2"),
                    rs.getInt("cityId"),
                    rs.getString("postalCode"),
                    rs.getString("phone"),
                    rs.getTimestamp("createDate"),
                    rs.getString("createdBy"),
                    rs.getTimestamp("lastUpdate"),
                    rs.getString("lastUpdateBy")

                );  
            }
            
            
        } catch (Exception e) {
            
            System.out.println("Failed to retrieve Address result.");
        }
        
        return address;

    }
    
    public static Address getAddressById(int id, Connection conn) throws SQLException, ParseException {
        
        Address address = new Address();
        
        try {
            String sql = "SELECT * FROM address WHERE addressid = " + id + " LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);

            if( rs.next() ) {

                address = new Address(
                    rs.getInt("addressid"),
                    rs.getString("address"),
                    rs.getString("address2"),
                    rs.getInt("cityId"),
                    rs.getString("postalCode"),
                    rs.getString("phone"),
                    rs.getTimestamp("createDate"),
                    rs.getString("createdBy"),
                    rs.getTimestamp("lastUpdate"),
                    rs.getString("lastUpdateBy")

                );  
            }
            
            
        } catch (Exception e) {
            
            System.out.println("Failed to retrieve Address result.");
        }
        
        return address;

    }

}
