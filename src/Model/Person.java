/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author kevin
 */
public abstract class Person {
    int customerid;
    String customerName;
    int addressId;
    int active;
    Timestamp createDate;
    String createdBy;
    Timestamp lastUpdate;
    String lastUpdateBy;
    String address;
    
}
