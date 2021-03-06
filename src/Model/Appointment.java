package Model;

import DB.Query;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Kevin Fredericks
 * 
 * 11/25/19
 * The only type of event in the GUI is an appointment. The abstract Event class exists for extensibility.
 */
public class Appointment extends Event {

    public Appointment(
        int appointmentid,
        int customerId, 
        int userId, 
        String title, 
        String description, 
        String location, 
        String contact, 
        String type, 
        String url, 
        Timestamp start,  
        Timestamp end, 
        Timestamp createDate, 
        String createdBy, //SQL string will convert to User object
        Timestamp lastUpdate, 
        String lastUpdateBy //SQL string will convert to User object
    ) {
        this.appointmentid = appointmentid;
        this.customerId = customerId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.url = url;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }
    
    //Special constructor for time functions
    //Intended to produce brevity
    public Appointment(Timestamp start, Timestamp end) {
        this.start = start;
        this.end = end;
    }
    
    public String getAttributesAsSQL() {
        List<String> list = Arrays.asList(
            //Integer.toString(appointmentid), //Id is AUTO_INCREMENT in the database
            Integer.toString(customerId), //customerId is dependent on customer table
            Integer.toString(userId),
            "'" + title + "'",
            "'" + description + "'",
            "'" + location + "'",
            "'" + contact + "'",
            "'" + type + "'",
            "'" + url + "'",
            "'" + start.toString() + "'",
            "'" + end.toString() + "'",
            "'" + createDate.toString() + "'",
            "'" + createdBy + "'",
            "'" + lastUpdate.toString() + "'",
            "'" + lastUpdateBy + "'"
        );
        
        StringBuilder sb = new StringBuilder();
        for( String str : list ) {
            sb = sb.append(str);
            sb =  sb.append(", ");
        }
        
        String result = sb.toString();
        
        //Clean up the final comma with a -2 substring
        result = result.substring(0, result.length() - 2);
        
        return result;
    }
    
    public static boolean checkForConflict(Event event, User currentUser, Connection conn) throws SQLException {
        
        boolean result = false;
        
        //Lambda Expression
        //This is a lambda expression that features all of the basic
        //advantages of functional methods.
        //The behavior is easier to read, and it takes advantage of
        //a functional interface to allow pipelining of tasks by
        //the JVM.
        BiPredicate<Event, Event> tester = (i,t) ->  {
            if( i.getStart().before( t.getStart() ) 
                && i.getEnd().after( t.getStart() ) ) return true; //Start Overlap
            
            if( i.getEnd().after( t.getEnd() )
                && i.getStart().before( t.getEnd() ) ) return true; //End Overlap
            
            if( i.getStart().before( t.getStart() )
                && i.getEnd().after( t.getEnd() ) ) return true; //Enveloped time
            
            if( i.getStart().equals(t.getStart() ) ) return true; //Concurrent time
            
            else return false;
        };
        
        //Get all events for user
        ObservableList<Event> allEvents = Query.getAllEventsByUserId(currentUser, conn);
        
        for (Event e : allEvents) {
            if( tester.test(event, e) ) result = true;
        }
        
        return result;
    }
    
    public static boolean checkForBusinessHours(Event e) throws SQLException {
        
        boolean result = false;
        
        LocalTime startingHour = LocalTime.of(8, 0);
        LocalTime closingHour = LocalTime.of(17,0);
        
        //Lambda Expression
        //This lambda expression is a small chunk of code that takes one argument.
        //It returns a boolean if the event starts before or after business hours.
        
        Predicate<Event> tester = (t) ->  {
            LocalTime eventStart =  t.getStart().toLocalDateTime().toLocalTime();
            LocalTime eventEnd =  t.getEnd().toLocalDateTime().toLocalTime();
            if( eventStart.isBefore( startingHour ) ) return true;
            if( eventEnd.isAfter( closingHour ) ) return true;
            else return false;
        };
        
        result = tester.test(e);
        
        return result;
    }
    
    
    public static Timestamp convertToTimestamp(LocalDateTime time, User user) {
        
        //Summary: Adds TimeZone offset to all event start and end times
        //Arguments: LocalDateTime and User
        //Returns: Date adjusted to User timezone as an offset from UTC
        
        //The user ZoneId will be applied to startTime and endTime
        ZoneId userZoneId= user.getTimezone().toZoneId();

        //Convert LocalDateTime to ZonedDateTime objects
        ZonedDateTime zonedTime = time.atZone(userZoneId);
        zonedTime = ZonedDateTime.ofInstant(zonedTime.toInstant(), ZoneId.of("UTC"));

        //Convert all objects to sql.Timestamp
        //The offset from UTC will be baked in to this value
        Timestamp timestamp = Timestamp.valueOf( zonedTime.toLocalDateTime() );
        
        return timestamp;

    }
    
    public static ZonedDateTime convertToZonedDateTime(Timestamp timestamp, User user) {
        
        //Summary:   Converts UTC timestamp to ZonedDateTime object with user TimeZone
        //Arguments: Timestamp from mySQL and User
        //Returns:   ZonedTimeStamp in user's correct timezone
        
        ZoneId userZoneId = user.getTimezone().toZoneId();

        ZonedDateTime zonedTime = timestamp.toLocalDateTime().atZone( ZoneId.of("UTC") );
        zonedTime = ZonedDateTime.ofInstant(zonedTime.toInstant(), userZoneId);
        
        return zonedTime;
}
    
    
}
