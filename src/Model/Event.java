package Model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author kevin
 */
public abstract class Event {
    int appointmentid;
    int customerId;
    int userId;
    String title;
    String description;
    String location;
    String contact;
    String type;
    String url;
    Timestamp start;
    Timestamp end;
    Timestamp createDate;
    String createdBy;
    Timestamp lastUpdate;
    String lastUpdateBy;

    public int getAppointmentid() {
        return appointmentid;
    }

    public void setAppointmentid(int appointmentid) {
        this.appointmentid = appointmentid;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }
    
    public void convertToLocalTime(User user) {
        
        //Get start and end times
        Timestamp startStamp = this.getStart();
        Timestamp endStamp = this.getEnd();

        //Calculate UTC offset useing ZonedDateTime
        ZonedDateTime startTime = Appointment.convertToZonedDateTime(startStamp, user);
        ZonedDateTime endTime = Appointment.convertToZonedDateTime(endStamp, user);

        //Convert to timestamp for appointment object
        //This timestamp is not accurate to the UTC setting in the mySQL database
        Timestamp convertedStart = Timestamp.from( startTime.toInstant() );
        Timestamp convertedEnd = Timestamp.from( endTime.toInstant() );

        //Update the appointment object
        this.setStart( convertedStart );
        this.setEnd( convertedEnd );
        
    }
    
    public void convertToUTC(User user) {
        //Summary: Adds TimeZone offset to all event start and end times
        //Arguments: User
        //Returns: void
        
        //Get start and end times
        Timestamp startStamp = this.getStart();
        Timestamp endStamp = this.getEnd();
        
        LocalDateTime startTime = startStamp.toLocalDateTime();
        LocalDateTime endTime = endStamp.toLocalDateTime();
        
        //The user ZoneId will be applied to startTime and endTime
        ZoneId userZoneId = user.getTimezone().toZoneId();

        //Convert LocalDateTime to ZonedDateTime objects
        
        //Start time
        ZonedDateTime zonedStartTime = startTime.atZone(userZoneId);
        zonedStartTime = ZonedDateTime.ofInstant(zonedStartTime.toInstant(), ZoneId.of("UTC"));
        //End time
        ZonedDateTime zonedEndTime = startTime.atZone(userZoneId);
        zonedEndTime = ZonedDateTime.ofInstant(zonedEndTime.toInstant(), ZoneId.of("UTC"));

        //Convert all objects to sql.Timestamp
        //The offset from UTC will be baked in to this value
        Timestamp convertedStartStamp = Timestamp.from( zonedStartTime.toInstant() );
        Timestamp convertedEndStamp = Timestamp.from( zonedEndTime.toInstant() );
        
        //Set value in Appointment object
        this.setStart( convertedStartStamp );
        this.setEnd( convertedEndStamp );
        
    }
    

    
}
