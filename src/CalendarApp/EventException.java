/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CalendarApp;

import java.util.List;

public class EventException extends Exception {
    public EventException(String errorMessage) {
        super(errorMessage);
    }
    
    public EventException(List errors) {
        //super(errors);
    }
}
