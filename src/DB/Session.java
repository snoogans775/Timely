/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import Model.User;
import java.sql.Connection;
import java.time.*;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author kevin
 */
public class Session {
    private User currentUser;
    private Locale locale;
    private TimeZone timeZone;
    
    public Connection conn;
    
    public Session(Connection conn) {
        this.currentUser = null;
        this.locale = Locale.getDefault();
        this.timeZone = TimeZone.getDefault();
        this.conn = conn;
        
    }
    
    //BEGIN GETTERS AND SETTERS
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return locale;
    }
    
    public void setTimeZone(TimeZone tz) {
        this.timeZone = tz;
    }
    
    public TimeZone getTimeZone() {
        return this.timeZone;
    }
    
    public int getTimeZoneOffset() {
        return this.timeZone.getRawOffset();
    }
}
